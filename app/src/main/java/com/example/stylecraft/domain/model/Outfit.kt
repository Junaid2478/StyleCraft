package com.example.stylecraft.domain.model

import java.util.UUID

data class Outfit(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val items: List<ClothingItem>,
    val score: OutfitScore? = null,
    val aiExplanation: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// Scores are 0-100. Overall is weighted average of the three components.
data class OutfitScore(
    val overall: Int,
    val colorHarmony: Int,      // 40% weight
    val silhouetteBalance: Int, // 35% weight
    val cohesion: Int,          // 25% weight
    val flags: List<ScoreFlag> = emptyList()
)

// Individual rule feedback with point impact (positive or negative)
data class ScoreFlag(
    val type: FlagType,
    val message: String,
    val impact: Int
)

enum class FlagType {
    POSITIVE,
    WARNING,
    ISSUE
}
