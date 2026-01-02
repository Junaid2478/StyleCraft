package com.example.stylecraft.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.stylecraft.data.local.dao.UserDao
import com.example.stylecraft.data.mapper.toDomain
import com.example.stylecraft.data.mapper.toEntity
import com.example.stylecraft.domain.model.User
import com.example.stylecraft.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val dataStore: DataStore<Preferences>
) : UserRepository {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    override fun getUser(): Flow<User?> {
        return userDao.getUser().map { it?.toDomain() }
    }

    override suspend fun getUserOnce(): User? {
        return userDao.getUserOnce()?.toDomain()
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun hasCompletedOnboarding(): Boolean {
        return dataStore.data.first()[ONBOARDING_COMPLETED] == true
    }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = true
        }
    }
}

