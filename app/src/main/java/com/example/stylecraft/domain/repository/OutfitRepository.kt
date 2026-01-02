package com.example.stylecraft.domain.repository

import com.example.stylecraft.domain.model.Outfit
import kotlinx.coroutines.flow.Flow

interface OutfitRepository {
    fun getAllOutfits(): Flow<List<Outfit>>
    suspend fun getOutfitById(id: String): Outfit?
    suspend fun saveOutfit(outfit: Outfit)
    suspend fun updateOutfit(outfit: Outfit)
    suspend fun deleteOutfit(id: String)
}
