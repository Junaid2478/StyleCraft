package com.example.stylecraft.presentation.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylecraft.data.remote.GeminiStyleExplainer
import com.example.stylecraft.domain.model.User
import com.example.stylecraft.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: User? = null,
    val hasApiKey: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStore: DataStore<Preferences>,
    private val geminiExplainer: GeminiStyleExplainer
) : ViewModel() {

    companion object {
        private val API_KEY = stringPreferencesKey("gemini_api_key")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val user = userRepository.getUserOnce()
            val apiKey = dataStore.data.first()[API_KEY]

            if (!apiKey.isNullOrBlank()) {
                geminiExplainer.setApiKey(apiKey)
            }

            _state.update {
                it.copy(
                    user = user,
                    hasApiKey = !apiKey.isNullOrBlank(),
                    isLoading = false
                )
            }
        }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[API_KEY] = key
            }
            geminiExplainer.setApiKey(key)
            _state.update { it.copy(hasApiKey = true) }
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ONBOARDING_COMPLETED] = false
            }
        }
    }
}

