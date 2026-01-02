package com.example.stylecraft.presentation.navigation

/**
 * Navigation destinations for StyleCraft.
 */
sealed class Screen(val route: String) {
    // Onboarding
    data object Welcome : Screen("welcome")
    data object BodyShape : Screen("onboarding/body_shape")
    data object ColorSeason : Screen("onboarding/color_season")
    data object Aesthetics : Screen("onboarding/aesthetics")

    // Main app (bottom nav)
    data object Wardrobe : Screen("wardrobe")
    data object Crafting : Screen("crafting")
    data object SavedOutfits : Screen("saved_outfits")
    data object Profile : Screen("profile")

    // Detail screens
    data object AddItem : Screen("wardrobe/add")
    data object EditItem : Screen("wardrobe/edit/{itemId}") {
        fun createRoute(itemId: String) = "wardrobe/edit/$itemId"
    }
    data object OutfitResult : Screen("outfit/result/{outfitId}") {
        fun createRoute(outfitId: String) = "outfit/result/$outfitId"
    }
}

