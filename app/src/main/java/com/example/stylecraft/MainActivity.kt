package com.example.stylecraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stylecraft.domain.repository.UserRepository
import com.example.stylecraft.presentation.navigation.BottomNavBar
import com.example.stylecraft.presentation.navigation.NavGraph
import com.example.stylecraft.presentation.navigation.Screen
import com.example.stylecraft.presentation.navigation.bottomNavItems
import com.example.stylecraft.ui.theme.StyleCraftTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StyleCraftTheme {
                StyleCraftApp(userRepository = userRepository)
            }
        }
    }
}

@Composable
fun StyleCraftApp(userRepository: UserRepository) {
    val navController = rememberNavController()

    var startDestination by remember { mutableStateOf<String?>(null) }

    // Determine start destination based on onboarding status
    LaunchedEffect(Unit) {
        val hasCompletedOnboarding = userRepository.hasCompletedOnboarding()
        startDestination = if (hasCompletedOnboarding) {
            Screen.Wardrobe.route
        } else {
            Screen.Welcome.route
        }
    }

    // Show nothing until we determine start destination
    if (startDestination == null) return

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if we should show bottom nav
    val showBottomNav = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            startDestination = startDestination!!,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
