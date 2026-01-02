package com.example.stylecraft.presentation.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylecraft.domain.model.ClothingCategory
import com.example.stylecraft.domain.model.ClothingColor
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.Fit
import com.example.stylecraft.domain.model.Pattern
import com.example.stylecraft.domain.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    val items: StateFlow<List<ClothingItem>> = wardrobeRepository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteItem(id: String) {
        viewModelScope.launch {
            wardrobeRepository.deleteItem(id)
        }
    }
}

// Separate ViewModel for Add/Edit screen
data class AddEditItemState(
    val id: String? = null,
    val name: String = "",
    val category: ClothingCategory = ClothingCategory.TOP,
    val primaryColor: ClothingColor = ClothingColor.WHITE,
    val secondaryColor: ClothingColor? = null,
    val pattern: Pattern = Pattern.SOLID,
    val fit: Fit = Fit.REGULAR,
    val formality: Int = 3,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isEditing: Boolean = false
)

@HiltViewModel
class AddEditItemViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditItemState())
    val state: StateFlow<AddEditItemState> = _state.asStateFlow()

    fun loadItem(itemId: String?) {
        if (itemId == null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val item = wardrobeRepository.getItemById(itemId)
            if (item != null) {
                _state.update {
                    it.copy(
                        id = item.id,
                        name = item.name,
                        category = item.category,
                        primaryColor = item.primaryColor,
                        secondaryColor = item.secondaryColor,
                        pattern = item.pattern,
                        fit = item.fit,
                        formality = item.formality,
                        isLoading = false,
                        isEditing = true
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun updateCategory(category: ClothingCategory) {
        _state.update { it.copy(category = category) }
    }

    fun updatePrimaryColor(color: ClothingColor) {
        _state.update { it.copy(primaryColor = color) }
    }

    fun updateSecondaryColor(color: ClothingColor?) {
        _state.update { it.copy(secondaryColor = color) }
    }

    fun updatePattern(pattern: Pattern) {
        _state.update { it.copy(pattern = pattern) }
    }

    fun updateFit(fit: Fit) {
        _state.update { it.copy(fit = fit) }
    }

    fun updateFormality(formality: Int) {
        _state.update { it.copy(formality = formality.coerceIn(1, 5)) }
    }

    fun saveItem() {
        val currentState = _state.value
        if (currentState.name.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val item = ClothingItem(
                id = currentState.id ?: UUID.randomUUID().toString(),
                name = currentState.name,
                category = currentState.category,
                primaryColor = currentState.primaryColor,
                secondaryColor = currentState.secondaryColor,
                pattern = currentState.pattern,
                fit = currentState.fit,
                formality = currentState.formality
            )

            if (currentState.isEditing) {
                wardrobeRepository.updateItem(item)
            } else {
                wardrobeRepository.saveItem(item)
            }

            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}

