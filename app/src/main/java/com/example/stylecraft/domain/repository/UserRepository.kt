package com.example.stylecraft.domain.repository

import com.example.stylecraft.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun getUserOnce(): User?
    suspend fun saveUser(user: User)
    suspend fun hasCompletedOnboarding(): Boolean
    suspend fun setOnboardingCompleted()
}
