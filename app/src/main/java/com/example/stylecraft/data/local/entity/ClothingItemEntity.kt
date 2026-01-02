package com.example.stylecraft.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val primaryColor: String,
    val secondaryColor: String?,
    val pattern: String,
    val fit: String,
    val formality: Int,
    val imageUri: String?,
    val createdAt: Long
)

