package com.example.stylecraft.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stylecraft.data.local.dao.ClothingItemDao
import com.example.stylecraft.data.local.dao.OutfitDao
import com.example.stylecraft.data.local.dao.UserDao
import com.example.stylecraft.data.local.entity.ClothingItemEntity
import com.example.stylecraft.data.local.entity.OutfitEntity
import com.example.stylecraft.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ClothingItemEntity::class,
        OutfitEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun outfitDao(): OutfitDao
}

