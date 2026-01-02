package com.example.stylecraft.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val itemIds: String, // JSON array of item IDs
    val overallScore: Int?,
    val colorHarmonyScore: Int?,
    val silhouetteScore: Int?,
    val cohesionScore: Int?,
    val flags: String?, // JSON array of flags
    val aiExplanation: String?,
    val createdAt: Long
)

