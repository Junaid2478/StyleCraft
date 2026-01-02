package com.example.stylecraft.data.remote

import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.OutfitScore
import com.example.stylecraft.domain.model.User
import com.example.stylecraft.domain.rules.RuleEngine
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Wraps Gemini to generate natural language explanations of outfit scores.
// Important: AI only explains the pre-calculated scores - it doesn't invent rules.
@Singleton
class GeminiStyleExplainer @Inject constructor(
    private val ruleEngine: RuleEngine
) {
    private var apiKey: String? = null

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey ?: "",
            generationConfig = generationConfig {
                temperature = 0.7f
                maxOutputTokens = 300
            }
        )
    }

    fun setApiKey(key: String) {
        apiKey = key
    }

    fun hasApiKey(): Boolean = !apiKey.isNullOrBlank()

    // Returns AI explanation if API key set, otherwise falls back to template
    suspend fun explainOutfit(
        items: List<ClothingItem>,
        score: OutfitScore,
        user: User
    ): String = withContext(Dispatchers.IO) {
        if (!hasApiKey()) {
            return@withContext generateFallbackExplanation(score)
        }

        try {
            val prompt = buildPrompt(items, score, user)
            val response = model.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank() || containsProhibitedContent(text)) {
                generateFallbackExplanation(score)
            } else {
                text.trim()
            }
        } catch (e: Exception) {
            generateFallbackExplanation(score)
        }
    }

    private fun buildPrompt(
        items: List<ClothingItem>,
        score: OutfitScore,
        user: User
    ): String {
        val ruleReasons = ruleEngine.generateRuleReasons(score)
        val itemDescriptions = items.joinToString("\n") { item ->
            "- ${item.category.displayName}: ${item.name} (${item.primaryColor.displayName}, ${item.fit.displayName}, formality ${item.formality}/5)"
        }

        return """
You are a friendly personal stylist. Based on the scoring data below, write a brief explanation of this outfit.

RULES:
- Only reference the scoring reasons provided
- Keep it to 2-3 sentences
- Be warm and constructive
- If score is low, suggest ONE improvement
- Do NOT mention brands, prices, or shopping

USER: ${user.bodyShape.displayName}, ${user.colorSeason.displayName}, ${user.aesthetics.joinToString { it.displayName }}

ITEMS:
$itemDescriptions

SCORES: Overall ${score.overall}/100, Color ${score.colorHarmony}, Silhouette ${score.silhouetteBalance}, Cohesion ${score.cohesion}

REASONS:
${ruleReasons.joinToString("\n")}

Write a brief explanation:
""".trimIndent()
    }

    // Filter out responses that mention shopping or body-shaming terms
    private fun containsProhibitedContent(text: String): Boolean {
        val prohibited = listOf("buy", "purchase", "shop", "store", "price", "\$", "ugly", "fat", "skinny")
        return prohibited.any { it in text.lowercase() }
    }

    // Template-based fallback when API is unavailable
    private fun generateFallbackExplanation(score: OutfitScore): String {
        val positiveFlags = score.flags.filter { it.impact > 0 }
        val negativeFlags = score.flags.filter { it.impact < 0 }

        val builder = StringBuilder()

        when {
            score.overall >= 80 -> builder.append("Great outfit! ")
            score.overall >= 65 -> builder.append("Solid combination. ")
            score.overall >= 50 -> builder.append("A workable outfit with room to improve. ")
            else -> builder.append("This combination needs some adjustments. ")
        }

        positiveFlags.maxByOrNull { it.impact }?.let {
            builder.append(it.message.replaceFirstChar { c -> c.uppercase() })
            builder.append(". ")
        }

        negativeFlags.minByOrNull { it.impact }?.let {
            builder.append("Consider: ")
            builder.append(it.message.lowercase())
            builder.append(".")
        }

        return builder.toString()
    }
}
