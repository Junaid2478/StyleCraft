package com.example.stylecraft.data.mapper

import com.example.stylecraft.data.local.entity.OutfitEntity
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.FlagType
import com.example.stylecraft.domain.model.Outfit
import com.example.stylecraft.domain.model.OutfitScore
import com.example.stylecraft.domain.model.ScoreFlag

fun OutfitEntity.toDomain(items: List<ClothingItem>): Outfit = Outfit(
    id = id,
    name = name,
    items = items,
    score = if (overallScore != null) {
        OutfitScore(
            overall = overallScore,
            colorHarmony = colorHarmonyScore ?: 0,
            silhouetteBalance = silhouetteScore ?: 0,
            cohesion = cohesionScore ?: 0,
            flags = parseFlags(flags)
        )
    } else null,
    aiExplanation = aiExplanation,
    createdAt = createdAt
)

fun Outfit.toEntity(): OutfitEntity = OutfitEntity(
    id = id,
    name = name,
    itemIds = items.joinToString(",") { it.id },
    overallScore = score?.overall,
    colorHarmonyScore = score?.colorHarmony,
    silhouetteScore = score?.silhouetteBalance,
    cohesionScore = score?.cohesion,
    flags = score?.flags?.let { serializeFlags(it) },
    aiExplanation = aiExplanation,
    createdAt = createdAt
)

private fun parseFlags(flagsString: String?): List<ScoreFlag> {
    if (flagsString.isNullOrBlank()) return emptyList()
    return flagsString.split(";").mapNotNull { flagEntry ->
        val parts = flagEntry.split("|")
        if (parts.size == 3) {
            ScoreFlag(
                type = FlagType.valueOf(parts[0]),
                message = parts[1],
                impact = parts[2].toIntOrNull() ?: 0
            )
        } else null
    }
}

private fun serializeFlags(flags: List<ScoreFlag>): String {
    return flags.joinToString(";") { "${it.type.name}|${it.message}|${it.impact}" }
}

fun OutfitEntity.getItemIds(): List<String> {
    return itemIds.split(",").filter { it.isNotBlank() }
}

