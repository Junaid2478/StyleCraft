package com.example.stylecraft.domain.repository

import com.example.stylecraft.domain.model.ClothingCategory
import com.example.stylecraft.domain.model.ClothingItem
import kotlinx.coroutines.flow.Flow

interface WardrobeRepository {
    fun getAllItems(): Flow<List<ClothingItem>>
    fun getItemsByCategory(category: ClothingCategory): Flow<List<ClothingItem>>
    suspend fun getItemById(id: String): ClothingItem?
    suspend fun getItemsByIds(ids: List<String>): List<ClothingItem>
    suspend fun saveItem(item: ClothingItem)
    suspend fun updateItem(item: ClothingItem)
    suspend fun deleteItem(id: String)
}
