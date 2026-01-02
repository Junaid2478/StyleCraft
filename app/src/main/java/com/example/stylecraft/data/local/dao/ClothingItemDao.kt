package com.example.stylecraft.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stylecraft.data.local.entity.ClothingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Query("SELECT * FROM clothing_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE category = :category ORDER BY createdAt DESC")
    fun getItemsByCategory(category: String): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getItemById(id: String): ClothingItemEntity?

    @Query("SELECT * FROM clothing_items WHERE id IN (:ids)")
    suspend fun getItemsByIds(ids: List<String>): List<ClothingItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClothingItemEntity)

    @Update
    suspend fun updateItem(item: ClothingItemEntity)

    @Delete
    suspend fun deleteItem(item: ClothingItemEntity)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteItemById(id: String)
}

