package com.example.stylecraft.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stylecraft.presentation.crafting.CraftingScreen
import com.example.stylecraft.presentation.onboarding.AestheticsScreen
import com.example.stylecraft.presentation.onboarding.BodyShapeScreen
import com.example.stylecraft.presentation.onboarding.ColorSeasonScreen
import com.example.stylecraft.presentation.onboarding.OnboardingViewModel
import com.example.stylecraft.presentation.onboarding.WelcomeScreen
import com.example.stylecraft.presentation.outfit.OutfitResultScreen
import com.example.stylecraft.presentation.profile.ProfileScreen
import com.example.stylecraft.presentation.savedoutfits.SavedOutfitsScreen
import com.example.stylecraft.presentation.wardrobe.AddEditItemScreen
import com.example.stylecraft.presentation.wardrobe.WardrobeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Onboarding flow - ViewModel scoped to Welcome screen so all steps share state
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Screen.BodyShape.route)
                }
            )
        }

        composable(Screen.BodyShape.route) { backStackEntry ->
            // Get ViewModel from Welcome screen's back stack entry to share across onboarding
            val welcomeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Welcome.route)
            }
            val viewModel: OnboardingViewModel = hiltViewModel(welcomeEntry)

            BodyShapeScreen(
                onNext = {
                    navController.navigate(Screen.ColorSeason.route)
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable(Screen.ColorSeason.route) { backStackEntry ->
            val welcomeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Welcome.route)
            }
            val viewModel: OnboardingViewModel = hiltViewModel(welcomeEntry)

            ColorSeasonScreen(
                onNext = {
                    navController.navigate(Screen.Aesthetics.route)
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable(Screen.Aesthetics.route) { backStackEntry ->
            val welcomeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Welcome.route)
            }
            val viewModel: OnboardingViewModel = hiltViewModel(welcomeEntry)

            AestheticsScreen(
                onComplete = {
                    navController.navigate(Screen.Wardrobe.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        // Main screens
        composable(Screen.Wardrobe.route) {
            WardrobeScreen(
                onAddItem = {
                    navController.navigate(Screen.AddItem.route)
                },
                onEditItem = { itemId ->
                    navController.navigate(Screen.EditItem.createRoute(itemId))
                }
            )
        }

        composable(Screen.Crafting.route) {
            CraftingScreen(
                onOutfitScored = { outfitId ->
                    navController.navigate(Screen.OutfitResult.createRoute(outfitId))
                }
            )
        }

        composable(Screen.SavedOutfits.route) {
            SavedOutfitsScreen(
                onOutfitClick = { outfitId ->
                    navController.navigate(Screen.OutfitResult.createRoute(outfitId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onResetOnboarding = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Detail screens
        composable(Screen.AddItem.route) {
            AddEditItemScreen(
                itemId = null,
                onSaved = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            AddEditItemScreen(
                itemId = itemId,
                onSaved = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.OutfitResult.route,
            arguments = listOf(navArgument("outfitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val outfitId = backStackEntry.arguments?.getString("outfitId") ?: ""
            OutfitResultScreen(
                outfitId = outfitId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
