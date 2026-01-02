package com.example.stylecraft.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val bodyShape: String,
    val colorSeason: String,
    val aesthetics: String, // JSON array of aesthetic names
    val createdAt: Long
)

