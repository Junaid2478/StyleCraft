package com.example.stylecraft.presentation.savedoutfits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylecraft.domain.model.Outfit
import com.example.stylecraft.domain.repository.OutfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedOutfitsViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    val outfits: StateFlow<List<Outfit>> = outfitRepository.getAllOutfits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteOutfit(id: String) {
        viewModelScope.launch {
            outfitRepository.deleteOutfit(id)
        }
    }
}

