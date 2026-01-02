package com.example.stylecraft.di

import com.example.stylecraft.data.repository.OutfitRepositoryImpl
import com.example.stylecraft.data.repository.UserRepositoryImpl
import com.example.stylecraft.data.repository.WardrobeRepositoryImpl
import com.example.stylecraft.domain.repository.OutfitRepository
import com.example.stylecraft.domain.repository.UserRepository
import com.example.stylecraft.domain.repository.WardrobeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindWardrobeRepository(impl: WardrobeRepositoryImpl): WardrobeRepository

    @Binds
    @Singleton
    abstract fun bindOutfitRepository(impl: OutfitRepositoryImpl): OutfitRepository
}

