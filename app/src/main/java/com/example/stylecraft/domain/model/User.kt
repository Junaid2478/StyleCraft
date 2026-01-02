package com.example.stylecraft.domain.model

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val bodyShape: BodyShape,
    val colorSeason: ColorSeason,
    val aesthetics: List<Aesthetic>,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BodyShape(val displayName: String, val description: String) {
    INVERTED_TRIANGLE("Inverted Triangle", "Broader shoulders, narrower hips"),
    RECTANGLE("Rectangle", "Balanced shoulders and hips, less defined waist"),
    TRIANGLE("Triangle", "Narrower shoulders, wider hips"),
    OVAL("Oval", "Fuller midsection, balanced shoulders and hips"),
    TRAPEZOID("Trapezoid", "Broad shoulders, defined waist, balanced build")
}

enum class ColorSeason(val displayName: String, val description: String) {
    SPRING("Spring", "Warm undertone, bright and clear colors"),
    SUMMER("Summer", "Cool undertone, soft and muted colors"),
    AUTUMN("Autumn", "Warm undertone, rich and earthy colors"),
    WINTER("Winter", "Cool undertone, bold and high-contrast colors")
}

enum class Aesthetic(val displayName: String, val description: String) {
    MINIMAL("Minimal", "Clean lines, neutral palette, understated"),
    TAILORED("Tailored", "Structured, polished, classic fits"),
    STREETWEAR("Streetwear", "Urban, relaxed, trend-forward"),
    RUGGED("Rugged", "Durable, outdoorsy, workwear-inspired"),
    CLASSIC("Classic", "Timeless pieces, traditional patterns"),
    ATHLEISURE("Athleisure", "Sporty comfort meets casual style")
}
