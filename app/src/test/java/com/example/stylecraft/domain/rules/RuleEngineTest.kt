package com.example.stylecraft.domain.rules

import com.example.stylecraft.domain.model.Aesthetic
import com.example.stylecraft.domain.model.BodyShape
import com.example.stylecraft.domain.model.ClothingCategory
import com.example.stylecraft.domain.model.ClothingColor
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.ColorSeason
import com.example.stylecraft.domain.model.Fit
import com.example.stylecraft.domain.model.FlagType
import com.example.stylecraft.domain.model.Pattern
import com.example.stylecraft.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RuleEngineTest {

    private lateinit var ruleEngine: RuleEngine

    @Before
    fun setup() {
        ruleEngine = RuleEngine()
    }

    private fun createUser(
        bodyShape: BodyShape = BodyShape.RECTANGLE,
        colorSeason: ColorSeason = ColorSeason.AUTUMN,
        aesthetics: List<Aesthetic> = listOf(Aesthetic.MINIMAL)
    ) = User(
        bodyShape = bodyShape,
        colorSeason = colorSeason,
        aesthetics = aesthetics
    )

    private fun createItem(
        category: ClothingCategory,
        color: ClothingColor = ClothingColor.NAVY,
        fit: Fit = Fit.REGULAR,
        formality: Int = 3,
        pattern: Pattern = Pattern.SOLID
    ) = ClothingItem(
        name = "Test ${category.displayName}",
        category = category,
        primaryColor = color,
        fit = fit,
        formality = formality,
        pattern = pattern
    )

    // Empty outfit tests

    @Test
    fun `empty outfit returns zero score`() {
        val user = createUser()
        val result = ruleEngine.scoreOutfit(emptyList(), user)

        assertEquals(0, result.overall)
        assertEquals(0, result.colorHarmony)
        assertEquals(0, result.silhouetteBalance)
        assertEquals(0, result.cohesion)
        assertTrue(result.flags.any { it.type == FlagType.ISSUE })
    }

    // Color harmony tests

    @Test
    fun `autumn colors score well for autumn user`() {
        val user = createUser(colorSeason = ColorSeason.AUTUMN)
        val items = listOf(
            createItem(ClothingCategory.TOP, ClothingColor.BURGUNDY),
            createItem(ClothingCategory.BOTTOM, ClothingColor.OLIVE)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.colorHarmony >= 80)
        assertTrue(result.flags.any { it.message.contains("palette") && it.impact > 0 })
    }

    @Test
    fun `winter colors score poorly for autumn user`() {
        val user = createUser(colorSeason = ColorSeason.AUTUMN)
        val items = listOf(
            createItem(ClothingCategory.TOP, ClothingColor.TRUE_RED),
            createItem(ClothingCategory.BOTTOM, ClothingColor.ROYAL_BLUE)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.colorHarmony < 70)
    }

    @Test
    fun `neutral palette scores bonus points`() {
        val user = createUser()
        val items = listOf(
            createItem(ClothingCategory.TOP, ClothingColor.WHITE),
            createItem(ClothingCategory.BOTTOM, ClothingColor.NAVY),
            createItem(ClothingCategory.FOOTWEAR, ClothingColor.BROWN)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("neutral") && it.impact > 0 })
    }

    @Test
    fun `too many competing colors penalized`() {
        val user = createUser()
        val items = listOf(
            createItem(ClothingCategory.TOP, ClothingColor.CORAL),
            createItem(ClothingCategory.BOTTOM, ClothingColor.EMERALD),
            createItem(ClothingCategory.OUTERWEAR, ClothingColor.LAVENDER),
            createItem(ClothingCategory.FOOTWEAR, ClothingColor.MUSTARD)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("competing colors") && it.impact < 0 })
    }

    @Test
    fun `multiple busy patterns clash`() {
        val user = createUser()
        val items = listOf(
            createItem(ClothingCategory.TOP, pattern = Pattern.CHECK),
            createItem(ClothingCategory.BOTTOM, pattern = Pattern.PRINT)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("patterns clash") && it.impact < 0 })
    }

    // Silhouette tests

    @Test
    fun `relaxed top slim bottom scores well`() {
        val user = createUser()
        val items = listOf(
            createItem(ClothingCategory.TOP, fit = Fit.RELAXED),
            createItem(ClothingCategory.BOTTOM, fit = Fit.SLIM)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("balance") && it.impact > 0 })
    }

    @Test
    fun `double oversized penalized`() {
        val user = createUser()
        val items = listOf(
            createItem(ClothingCategory.TOP, fit = Fit.OVERSIZED),
            createItem(ClothingCategory.BOTTOM, fit = Fit.OVERSIZED)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("shapeless") && it.impact < 0 })
    }

    @Test
    fun `inverted triangle benefits from bottom volume`() {
        val user = createUser(bodyShape = BodyShape.INVERTED_TRIANGLE)
        val items = listOf(
            createItem(ClothingCategory.TOP, fit = Fit.REGULAR),
            createItem(ClothingCategory.BOTTOM, fit = Fit.RELAXED)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("shoulders") && it.impact > 0 })
    }

    @Test
    fun `triangle benefits from top volume`() {
        val user = createUser(bodyShape = BodyShape.TRIANGLE)
        val items = listOf(
            createItem(ClothingCategory.TOP, fit = Fit.RELAXED),
            createItem(ClothingCategory.BOTTOM, fit = Fit.REGULAR)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("proportions") && it.impact > 0 })
    }

    // Cohesion tests

    @Test
    fun `consistent formality scores well`() {
        val user = createUser()
        val items = listOf(
            createItem(ClothingCategory.TOP, formality = 3),
            createItem(ClothingCategory.BOTTOM, formality = 3),
            createItem(ClothingCategory.FOOTWEAR, formality = 3)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("formality level") && it.impact > 0 })
    }

    @Test
    fun `large formality gap penalized for non-streetwear`() {
        val user = createUser(aesthetics = listOf(Aesthetic.TAILORED))
        val items = listOf(
            createItem(ClothingCategory.TOP, formality = 5),
            createItem(ClothingCategory.BOTTOM, formality = 1)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("formality gap") && it.impact < 0 })
    }

    @Test
    fun `formality mix allowed for streetwear`() {
        val user = createUser(aesthetics = listOf(Aesthetic.STREETWEAR))
        val items = listOf(
            createItem(ClothingCategory.TOP, formality = 5),
            createItem(ClothingCategory.BOTTOM, formality = 1)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.none { it.message.contains("formality gap") && it.impact < 0 })
    }

    @Test
    fun `aesthetic match scores bonus`() {
        val user = createUser(aesthetics = listOf(Aesthetic.TAILORED))
        val items = listOf(
            createItem(ClothingCategory.TOP, formality = 4),
            createItem(ClothingCategory.BOTTOM, formality = 4)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.flags.any { it.message.contains("aesthetic") && it.impact > 0 })
    }

    // Overall score tests

    @Test
    fun `well-crafted outfit scores above 70`() {
        val user = createUser(
            bodyShape = BodyShape.RECTANGLE,
            colorSeason = ColorSeason.AUTUMN,
            aesthetics = listOf(Aesthetic.MINIMAL)
        )
        val items = listOf(
            createItem(ClothingCategory.TOP, ClothingColor.NAVY, Fit.REGULAR, 3),
            createItem(ClothingCategory.BOTTOM, ClothingColor.TAN, Fit.REGULAR, 3),
            createItem(ClothingCategory.FOOTWEAR, ClothingColor.BROWN, Fit.REGULAR, 3)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.overall >= 70)
    }

    @Test
    fun `score is capped at 100`() {
        val user = createUser(
            colorSeason = ColorSeason.AUTUMN,
            aesthetics = listOf(Aesthetic.MINIMAL)
        )
        val items = listOf(
            createItem(ClothingCategory.TOP, ClothingColor.BURGUNDY, Fit.RELAXED, 3),
            createItem(ClothingCategory.BOTTOM, ClothingColor.OLIVE, Fit.SLIM, 3)
        )

        val result = ruleEngine.scoreOutfit(items, user)

        assertTrue(result.overall <= 100)
        assertTrue(result.colorHarmony <= 100)
        assertTrue(result.silhouetteBalance <= 100)
        assertTrue(result.cohesion <= 100)
    }

    // Rule reasons tests

    @Test
    fun `generateRuleReasons formats flags correctly`() {
        val user = createUser()
        val items = listOf(
            createItem(ClothingCategory.TOP),
            createItem(ClothingCategory.BOTTOM)
        )

        val result = ruleEngine.scoreOutfit(items, user)
        val reasons = ruleEngine.generateRuleReasons(result)

        assertTrue(reasons.all { it.contains("(") && it.contains(")") })
        assertTrue(reasons.all { it.startsWith("✓") || it.startsWith("⚠") || it.startsWith("✗") })
    }
}

