package com.example.stylecraft.di

import android.content.Context
import androidx.room.Room
import com.example.stylecraft.data.local.AppDatabase
import com.example.stylecraft.data.local.dao.ClothingItemDao
import com.example.stylecraft.data.local.dao.OutfitDao
import com.example.stylecraft.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "stylecraft_db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideClothingItemDao(database: AppDatabase): ClothingItemDao = database.clothingItemDao()

    @Provides
    fun provideOutfitDao(database: AppDatabase): OutfitDao = database.outfitDao()
}

