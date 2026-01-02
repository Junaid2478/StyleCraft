package com.example.stylecraft.presentation.crafting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylecraft.domain.model.ClothingCategory
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.Outfit
import com.example.stylecraft.domain.model.OutfitScore
import com.example.stylecraft.domain.model.User
import com.example.stylecraft.domain.repository.OutfitRepository
import com.example.stylecraft.domain.repository.UserRepository
import com.example.stylecraft.domain.repository.WardrobeRepository
import com.example.stylecraft.domain.rules.RuleEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Represents a slot on the crafting board (one per category)
data class CraftingSlot(
    val category: ClothingCategory,
    val item: ClothingItem? = null
)

data class CraftingState(
    val slots: List<CraftingSlot> = ClothingCategory.entries.map { CraftingSlot(it) },
    val score: OutfitScore? = null,
    val isScoring: Boolean = false,
    val selectedCategory: ClothingCategory? = null,
    val showItemPicker: Boolean = false,
    val savedOutfitId: String? = null // set after save, triggers navigation
)

@HiltViewModel
class CraftingViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository,
    private val userRepository: UserRepository,
    private val outfitRepository: OutfitRepository,
    private val ruleEngine: RuleEngine
) : ViewModel() {

    private val _state = MutableStateFlow(CraftingState())
    val state: StateFlow<CraftingState> = _state.asStateFlow()

    // Exposed for item picker to show available items
    val wardrobeItems: StateFlow<List<ClothingItem>> = wardrobeRepository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cache user to avoid repeated DB calls during scoring
    private var cachedUser: User? = null

    init {
        viewModelScope.launch {
            cachedUser = userRepository.getUserOnce()
        }
    }

    fun selectSlot(category: ClothingCategory) {
        _state.update {
            it.copy(
                selectedCategory = category,
                showItemPicker = true
            )
        }
    }

    fun dismissItemPicker() {
        _state.update {
            it.copy(
                selectedCategory = null,
                showItemPicker = false
            )
        }
    }

    fun assignItemToSlot(item: ClothingItem) {
        val category = _state.value.selectedCategory ?: return

        _state.update { current ->
            val newSlots = current.slots.map { slot ->
                if (slot.category == category) {
                    slot.copy(item = item)
                } else {
                    slot
                }
            }
            current.copy(
                slots = newSlots,
                selectedCategory = null,
                showItemPicker = false,
                score = null
            )
        }

        scoreCurrentOutfit()
    }

    fun removeItemFromSlot(category: ClothingCategory) {
        _state.update { current ->
            val newSlots = current.slots.map { slot ->
                if (slot.category == category) {
                    slot.copy(item = null)
                } else {
                    slot
                }
            }
            current.copy(
                slots = newSlots,
                score = null
            )
        }
    }

    fun scoreCurrentOutfit() {
        val items = _state.value.slots.mapNotNull { it.item }
        val user = cachedUser

        if (items.isEmpty() || user == null) {
            _state.update { it.copy(score = null) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isScoring = true) }
            val score = ruleEngine.scoreOutfit(items, user)
            _state.update { it.copy(score = score, isScoring = false) }
        }
    }

    fun saveOutfit(name: String = "") {
        val items = _state.value.slots.mapNotNull { it.item }
        val score = _state.value.score

        if (items.isEmpty()) return

        viewModelScope.launch {
            val outfit = Outfit(
                name = name.ifBlank { "Outfit ${System.currentTimeMillis() / 1000}" },
                items = items,
                score = score
            )
            outfitRepository.saveOutfit(outfit)
            _state.update { it.copy(savedOutfitId = outfit.id) }
        }
    }

    fun clearBoard() {
        _state.update { CraftingState() }
    }

    fun clearSavedOutfitId() {
        _state.update { it.copy(savedOutfitId = null) }
    }
}
