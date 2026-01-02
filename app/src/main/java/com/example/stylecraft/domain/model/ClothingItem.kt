package com.example.stylecraft.domain.model

import java.util.UUID

data class ClothingItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: ClothingCategory,
    val primaryColor: ClothingColor,
    val secondaryColor: ClothingColor? = null,
    val pattern: Pattern = Pattern.SOLID,
    val fit: Fit,
    val formality: Int, // 1 = very casual, 5 = formal
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ClothingCategory(val displayName: String, val slotOrder: Int) {
    TOP("Top", 0),
    BOTTOM("Bottom", 1),
    OUTERWEAR("Outerwear", 2),
    FOOTWEAR("Footwear", 3),
    ACCESSORY("Accessory", 4)
}

// Each color has seasons it works best with (for palette matching)
enum class ClothingColor(
    val displayName: String,
    val hexCode: String,
    val seasons: Set<String>
) {
    // Neutrals - work with most seasons
    WHITE("White", "#FFFFFF", setOf("SPRING", "SUMMER", "AUTUMN", "WINTER")),
    OFF_WHITE("Off White", "#FAF9F6", setOf("SPRING", "SUMMER", "AUTUMN")),
    BLACK("Black", "#000000", setOf("WINTER", "SUMMER")),
    CHARCOAL("Charcoal", "#36454F", setOf("WINTER", "SUMMER")),
    NAVY("Navy", "#1A3A5C", setOf("WINTER", "SUMMER", "AUTUMN")),
    GREY("Grey", "#808080", setOf("SUMMER", "WINTER")),
    BEIGE("Beige", "#C8AD7F", setOf("SPRING", "AUTUMN")),
    BROWN("Brown", "#6E4B3A", setOf("AUTUMN")),
    TAN("Tan", "#D2B48C", setOf("SPRING", "AUTUMN")),
    CREAM("Cream", "#FFFDD0", setOf("SPRING", "AUTUMN")),

    // Blues
    LIGHT_BLUE("Light Blue", "#ADD8E6", setOf("SUMMER", "SPRING")),
    ROYAL_BLUE("Royal Blue", "#4169E1", setOf("WINTER")),
    TEAL("Teal", "#008080", setOf("AUTUMN", "WINTER")),
    DENIM_BLUE("Denim Blue", "#6F8FAF", setOf("SPRING", "SUMMER", "AUTUMN", "WINTER")),

    // Greens
    OLIVE("Olive", "#808000", setOf("AUTUMN")),
    FOREST_GREEN("Forest Green", "#228B22", setOf("AUTUMN", "WINTER")),
    SAGE("Sage", "#9DC183", setOf("SUMMER", "SPRING")),
    EMERALD("Emerald", "#50C878", setOf("WINTER", "SPRING")),

    // Reds & Pinks
    BURGUNDY("Burgundy", "#722F37", setOf("AUTUMN", "WINTER")),
    RUST("Rust", "#B7410E", setOf("AUTUMN")),
    CORAL("Coral", "#FF7F50", setOf("SPRING")),
    BLUSH("Blush", "#DE5D83", setOf("SUMMER", "SPRING")),
    TRUE_RED("True Red", "#FF0000", setOf("WINTER")),

    // Yellows & Oranges
    MUSTARD("Mustard", "#FFDB58", setOf("AUTUMN")),
    GOLD("Gold", "#FFD700", setOf("AUTUMN", "SPRING")),
    PEACH("Peach", "#FFCBA4", setOf("SPRING")),
    BURNT_ORANGE("Burnt Orange", "#CC5500", setOf("AUTUMN")),

    // Purples
    LAVENDER("Lavender", "#E6E6FA", setOf("SUMMER")),
    PLUM("Plum", "#8E4585", setOf("WINTER", "AUTUMN")),
    MAUVE("Mauve", "#E0B0FF", setOf("SUMMER"))
}

// visualWeight: higher = busier pattern, used for clash detection
enum class Pattern(val displayName: String, val visualWeight: Int) {
    SOLID("Solid", 1),
    STRIPES("Stripes", 2),
    CHECK("Check/Plaid", 3),
    PRINT("Print/Graphic", 4),
    TEXTURE("Textured", 2)
}

enum class Fit(val displayName: String) {
    SLIM("Slim/Fitted"),
    REGULAR("Regular"),
    RELAXED("Relaxed"),
    OVERSIZED("Oversized")
}
