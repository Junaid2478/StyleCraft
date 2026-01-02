package com.example.stylecraft.data.repository

import com.example.stylecraft.data.local.dao.ClothingItemDao
import com.example.stylecraft.data.mapper.toDomain
import com.example.stylecraft.data.mapper.toEntity
import com.example.stylecraft.domain.model.ClothingCategory
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.repository.WardrobeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WardrobeRepositoryImpl @Inject constructor(
    private val clothingItemDao: ClothingItemDao
) : WardrobeRepository {

    override fun getAllItems(): Flow<List<ClothingItem>> {
        return clothingItemDao.getAllItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getItemsByCategory(category: ClothingCategory): Flow<List<ClothingItem>> {
        return clothingItemDao.getItemsByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getItemById(id: String): ClothingItem? {
        return clothingItemDao.getItemById(id)?.toDomain()
    }

    override suspend fun getItemsByIds(ids: List<String>): List<ClothingItem> {
        return clothingItemDao.getItemsByIds(ids).map { it.toDomain() }
    }

    override suspend fun saveItem(item: ClothingItem) {
        clothingItemDao.insertItem(item.toEntity())
    }

    override suspend fun updateItem(item: ClothingItem) {
        clothingItemDao.updateItem(item.toEntity())
    }

    override suspend fun deleteItem(id: String) {
        clothingItemDao.deleteItemById(id)
    }
}

