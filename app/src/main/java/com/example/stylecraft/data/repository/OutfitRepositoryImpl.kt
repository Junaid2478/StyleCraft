package com.example.stylecraft.data.repository

import com.example.stylecraft.data.local.dao.ClothingItemDao
import com.example.stylecraft.data.local.dao.OutfitDao
import com.example.stylecraft.data.mapper.getItemIds
import com.example.stylecraft.data.mapper.toDomain
import com.example.stylecraft.data.mapper.toEntity
import com.example.stylecraft.domain.model.Outfit
import com.example.stylecraft.domain.repository.OutfitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutfitRepositoryImpl @Inject constructor(
    private val outfitDao: OutfitDao,
    private val clothingItemDao: ClothingItemDao
) : OutfitRepository {

    override fun getAllOutfits(): Flow<List<Outfit>> {
        return outfitDao.getAllOutfits().map { entities ->
            entities.map { entity ->
                val items = clothingItemDao.getItemsByIds(entity.getItemIds())
                    .map { it.toDomain() }
                entity.toDomain(items)
            }
        }
    }

    override suspend fun getOutfitById(id: String): Outfit? {
        val entity = outfitDao.getOutfitById(id) ?: return null
        val items = clothingItemDao.getItemsByIds(entity.getItemIds())
            .map { it.toDomain() }
        return entity.toDomain(items)
    }

    override suspend fun saveOutfit(outfit: Outfit) {
        outfitDao.insertOutfit(outfit.toEntity())
    }

    override suspend fun updateOutfit(outfit: Outfit) {
        outfitDao.updateOutfit(outfit.toEntity())
    }

    override suspend fun deleteOutfit(id: String) {
        outfitDao.deleteOutfitById(id)
    }
}

