package com.example.stylecraft.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylecraft.domain.model.Aesthetic
import com.example.stylecraft.domain.model.BodyShape
import com.example.stylecraft.domain.model.ColorSeason
import com.example.stylecraft.domain.model.User
import com.example.stylecraft.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val selectedBodyShape: BodyShape? = null,
    val selectedColorSeason: ColorSeason? = null,
    val selectedAesthetics: Set<Aesthetic> = emptySet(),
    val isSaving: Boolean = false,
    val isComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun selectBodyShape(bodyShape: BodyShape) {
        _state.update { it.copy(selectedBodyShape = bodyShape) }
    }

    fun selectColorSeason(colorSeason: ColorSeason) {
        _state.update { it.copy(selectedColorSeason = colorSeason) }
    }

    fun toggleAesthetic(aesthetic: Aesthetic) {
        _state.update { current ->
            val newSet = if (aesthetic in current.selectedAesthetics) {
                current.selectedAesthetics - aesthetic
            } else {
                current.selectedAesthetics + aesthetic
            }
            current.copy(selectedAesthetics = newSet)
        }
    }

    fun completeOnboarding() {
        val currentState = _state.value
        val bodyShape = currentState.selectedBodyShape ?: return
        val colorSeason = currentState.selectedColorSeason ?: return
        val aesthetics = currentState.selectedAesthetics.toList()

        if (aesthetics.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val user = User(
                bodyShape = bodyShape,
                colorSeason = colorSeason,
                aesthetics = aesthetics
            )

            userRepository.saveUser(user)
            userRepository.setOnboardingCompleted()

            _state.update { it.copy(isSaving = false, isComplete = true) }
        }
    }
}

