package com.example.stylecraft.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stylecraft.data.local.entity.OutfitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits ORDER BY createdAt DESC")
    fun getAllOutfits(): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE id = :id")
    suspend fun getOutfitById(id: String): OutfitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun deleteOutfitById(id: String)
}

