package com.example.stylecraft.presentation.outfit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylecraft.data.remote.GeminiStyleExplainer
import com.example.stylecraft.domain.model.Outfit
import com.example.stylecraft.domain.repository.OutfitRepository
import com.example.stylecraft.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OutfitResultState(
    val outfit: Outfit? = null,
    val aiExplanation: String? = null,
    val isLoading: Boolean = true,
    val isLoadingAI: Boolean = false
)

@HiltViewModel
class OutfitResultViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository,
    private val userRepository: UserRepository,
    private val geminiExplainer: GeminiStyleExplainer
) : ViewModel() {

    private val _state = MutableStateFlow(OutfitResultState())
    val state: StateFlow<OutfitResultState> = _state.asStateFlow()

    fun loadOutfit(outfitId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val outfit = outfitRepository.getOutfitById(outfitId)

            _state.update {
                it.copy(
                    outfit = outfit,
                    aiExplanation = outfit?.aiExplanation,
                    isLoading = false
                )
            }

            // Generate AI explanation if not already present
            if (outfit != null && outfit.aiExplanation == null && outfit.score != null) {
                generateAIExplanation(outfit)
            }
        }
    }

    private fun generateAIExplanation(outfit: Outfit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAI = true) }

            val user = userRepository.getUserOnce()
            if (user != null && outfit.score != null) {
                val explanation = geminiExplainer.explainOutfit(
                    items = outfit.items,
                    score = outfit.score,
                    user = user
                )

                // Save explanation to outfit
                val updatedOutfit = outfit.copy(aiExplanation = explanation)
                outfitRepository.updateOutfit(updatedOutfit)

                _state.update {
                    it.copy(
                        outfit = updatedOutfit,
                        aiExplanation = explanation,
                        isLoadingAI = false
                    )
                }
            } else {
                _state.update { it.copy(isLoadingAI = false) }
            }
        }
    }
}

