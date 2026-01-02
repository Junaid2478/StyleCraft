package com.example.stylecraft.domain.rules

import com.example.stylecraft.domain.model.Aesthetic
import com.example.stylecraft.domain.model.BodyShape
import com.example.stylecraft.domain.model.ClothingCategory
import com.example.stylecraft.domain.model.ClothingColor
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.ColorSeason
import com.example.stylecraft.domain.model.Fit
import com.example.stylecraft.domain.model.FlagType
import com.example.stylecraft.domain.model.OutfitScore
import com.example.stylecraft.domain.model.ScoreFlag
import com.example.stylecraft.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleEngine @Inject constructor() {

    // Main entry point - scores outfit across 3 dimensions with weighted average
    fun scoreOutfit(items: List<ClothingItem>, user: User): OutfitScore {
        if (items.isEmpty()) {
            return OutfitScore(
                overall = 0,
                colorHarmony = 0,
                silhouetteBalance = 0,
                cohesion = 0,
                flags = listOf(ScoreFlag(FlagType.ISSUE, "No items in outfit", 0))
            )
        }

        val allFlags = mutableListOf<ScoreFlag>()

        val colorResult = calculateColorHarmony(items, user.colorSeason)
        val silhouetteResult = calculateSilhouetteBalance(items, user.bodyShape)
        val cohesionResult = calculateCohesion(items, user.aesthetics)

        allFlags.addAll(colorResult.flags)
        allFlags.addAll(silhouetteResult.flags)
        allFlags.addAll(cohesionResult.flags)

        // Weights: Color 40%, Silhouette 35%, Cohesion 25%
        val overall = (
            colorResult.score * 0.40 +
            silhouetteResult.score * 0.35 +
            cohesionResult.score * 0.25
        ).toInt().coerceIn(0, 100)

        return OutfitScore(
            overall = overall,
            colorHarmony = colorResult.score,
            silhouetteBalance = silhouetteResult.score,
            cohesion = cohesionResult.score,
            flags = allFlags
        )
    }

    // COLOR HARMONY: checks seasonal palette match, neutral balance, pattern clashing
    private fun calculateColorHarmony(items: List<ClothingItem>, season: ColorSeason): RuleResult {
        var score = 70 // base score
        val flags = mutableListOf<ScoreFlag>()

        val colors = items.map { it.primaryColor }
        val seasonName = season.name

        // Check what % of colors match user's seasonal palette
        val inPaletteCount = colors.count { seasonName in it.seasons }
        val paletteRatio = inPaletteCount.toFloat() / colors.size

        when {
            paletteRatio >= 0.8 -> {
                score += 20
                flags.add(ScoreFlag(FlagType.POSITIVE, "Colors match your $season palette", 20))
            }
            paletteRatio >= 0.5 -> {
                score += 10
                flags.add(ScoreFlag(FlagType.POSITIVE, "Most colors suit your palette", 10))
            }
            paletteRatio < 0.3 -> {
                score -= 15
                flags.add(ScoreFlag(FlagType.WARNING, "Colors don't match your seasonal palette", -15))
            }
        }

        // Neutrals are always safe - reward outfits that stick to them
        val neutrals = setOf(
            ClothingColor.WHITE, ClothingColor.OFF_WHITE, ClothingColor.BLACK,
            ClothingColor.CHARCOAL, ClothingColor.NAVY, ClothingColor.GREY,
            ClothingColor.BEIGE, ClothingColor.BROWN, ClothingColor.TAN,
            ClothingColor.CREAM, ClothingColor.DENIM_BLUE
        )

        val nonNeutralColors = colors.filter { it !in neutrals }
        if (nonNeutralColors.size <= 1) {
            score += 10
            flags.add(ScoreFlag(FlagType.POSITIVE, "Safe neutral palette", 10))
        } else if (nonNeutralColors.size > 3) {
            score -= 10
            flags.add(ScoreFlag(FlagType.WARNING, "Too many competing colors", -10))
        }

        // Check for pattern clashing (visual weight 3+ = busy pattern)
        val patterns = items.map { it.pattern }
        val busyPatterns = patterns.filter { it.visualWeight >= 3 }
        if (busyPatterns.size > 1) {
            score -= 15
            flags.add(ScoreFlag(FlagType.ISSUE, "Multiple busy patterns clash", -15))
        }

        return RuleResult(score.coerceIn(0, 100), flags)
    }

    // SILHOUETTE: checks fit combinations and body-shape-specific rules
    private fun calculateSilhouetteBalance(items: List<ClothingItem>, bodyShape: BodyShape): RuleResult {
        var score = 70
        val flags = mutableListOf<ScoreFlag>()

        val top = items.find { it.category == ClothingCategory.TOP || it.category == ClothingCategory.OUTERWEAR }
        val bottom = items.find { it.category == ClothingCategory.BOTTOM }

        if (top != null && bottom != null) {
            // General fit combo rules (e.g., relaxed + slim = balanced)
            val fitScore = getFitCombinationScore(top.fit, bottom.fit)
            score += fitScore.first
            fitScore.second?.let { flags.add(it) }

            // Body-shape-specific adjustments
            val shapeScore = getBodyShapeScore(top.fit, bottom.fit, bodyShape)
            score += shapeScore.first
            shapeScore.second?.let { flags.add(it) }
        }

        return RuleResult(score.coerceIn(0, 100), flags)
    }

    // Fit combo matrix - some combos create balance, others look off
    private fun getFitCombinationScore(topFit: Fit, bottomFit: Fit): Pair<Int, ScoreFlag?> {
        return when {
            topFit == Fit.RELAXED && bottomFit == Fit.SLIM ->
                15 to ScoreFlag(FlagType.POSITIVE, "Relaxed top + slim bottom creates balance", 15)
            topFit == Fit.SLIM && bottomFit == Fit.REGULAR ->
                10 to ScoreFlag(FlagType.POSITIVE, "Well-proportioned fit combination", 10)
            topFit == Fit.REGULAR && bottomFit == Fit.REGULAR ->
                5 to null
            topFit == Fit.OVERSIZED && bottomFit == Fit.OVERSIZED ->
                -15 to ScoreFlag(FlagType.WARNING, "Double oversized can look shapeless", -15)
            topFit == Fit.SLIM && bottomFit == Fit.SLIM ->
                -5 to ScoreFlag(FlagType.WARNING, "All slim can be restrictive", -5)
            else -> 0 to null
        }
    }

    // Body shape rules - different shapes benefit from different proportions
    private fun getBodyShapeScore(topFit: Fit, bottomFit: Fit, shape: BodyShape): Pair<Int, ScoreFlag?> {
        return when (shape) {
            BodyShape.INVERTED_TRIANGLE -> when {
                // Broad shoulders - add volume below to balance
                bottomFit == Fit.RELAXED || bottomFit == Fit.REGULAR ->
                    10 to ScoreFlag(FlagType.POSITIVE, "Bottom volume balances broader shoulders", 10)
                topFit == Fit.OVERSIZED ->
                    -10 to ScoreFlag(FlagType.WARNING, "Oversized top may exaggerate shoulder width", -10)
                else -> 0 to null
            }
            BodyShape.TRIANGLE -> when {
                // Wider hips - add volume on top to balance
                topFit == Fit.REGULAR || topFit == Fit.RELAXED ->
                    10 to ScoreFlag(FlagType.POSITIVE, "Top volume balances proportions", 10)
                bottomFit == Fit.OVERSIZED ->
                    -10 to ScoreFlag(FlagType.WARNING, "Oversized bottom may add unwanted volume", -10)
                else -> 0 to null
            }
            BodyShape.RECTANGLE -> when {
                // Balanced frame - varied fits add visual interest
                topFit != bottomFit ->
                    5 to ScoreFlag(FlagType.POSITIVE, "Varied fits create visual interest", 5)
                else -> 0 to null
            }
            BodyShape.OVAL -> when {
                // Fuller midsection - structured fits create clean lines
                topFit == Fit.REGULAR && bottomFit == Fit.REGULAR ->
                    10 to ScoreFlag(FlagType.POSITIVE, "Structured fits create clean lines", 10)
                topFit == Fit.OVERSIZED ->
                    -5 to ScoreFlag(FlagType.WARNING, "Very loose tops may lack definition", -5)
                else -> 0 to null
            }
            BodyShape.TRAPEZOID ->
                // Athletic build - most things work
                5 to ScoreFlag(FlagType.POSITIVE, "Your proportions are versatile", 5)
        }
    }

    // COHESION: checks formality consistency and aesthetic alignment
    private fun calculateCohesion(items: List<ClothingItem>, aesthetics: List<Aesthetic>): RuleResult {
        var score = 70
        val flags = mutableListOf<ScoreFlag>()

        val formalities = items.map { it.formality }
        val formalityRange = (formalities.maxOrNull() ?: 3) - (formalities.minOrNull() ?: 3)

        // Formality consistency - mixing formal blazer with joggers looks off
        when {
            formalityRange <= 1 -> {
                score += 15
                flags.add(ScoreFlag(FlagType.POSITIVE, "Consistent formality level", 15))
            }
            formalityRange == 2 -> {
                score += 5
            }
            formalityRange >= 3 -> {
                // Exception: streetwear/athleisure intentionally mixes formality
                if (Aesthetic.STREETWEAR in aesthetics || Aesthetic.ATHLEISURE in aesthetics) {
                    flags.add(ScoreFlag(FlagType.POSITIVE, "Formality mix works for your aesthetic", 0))
                } else {
                    score -= 20
                    flags.add(ScoreFlag(FlagType.ISSUE, "Large formality gap (casual + formal)", -20))
                }
            }
        }

        // Check if outfit matches user's declared aesthetic
        val avgFormality = formalities.average()
        val aestheticFit = when {
            Aesthetic.MINIMAL in aesthetics && avgFormality >= 2.5 -> true
            Aesthetic.TAILORED in aesthetics && avgFormality >= 3.5 -> true
            Aesthetic.STREETWEAR in aesthetics && avgFormality <= 3 -> true
            Aesthetic.RUGGED in aesthetics && avgFormality <= 3.5 -> true
            Aesthetic.ATHLEISURE in aesthetics && avgFormality <= 2.5 -> true
            Aesthetic.CLASSIC in aesthetics && avgFormality >= 3 -> true
            else -> false
        }

        if (aestheticFit) {
            score += 10
            flags.add(ScoreFlag(FlagType.POSITIVE, "Outfit matches your style aesthetic", 10))
        }

        return RuleResult(score.coerceIn(0, 100), flags)
    }

    private data class RuleResult(val score: Int, val flags: List<ScoreFlag>)

    // Formats flags for display or AI prompt input
    fun generateRuleReasons(score: OutfitScore): List<String> {
        return score.flags.map { flag ->
            val prefix = when (flag.type) {
                FlagType.POSITIVE -> "✓"
                FlagType.WARNING -> "⚠"
                FlagType.ISSUE -> "✗"
            }
            val impact = if (flag.impact >= 0) "+${flag.impact}" else "${flag.impact}"
            "$prefix ${flag.message} ($impact)"
        }
    }
}
