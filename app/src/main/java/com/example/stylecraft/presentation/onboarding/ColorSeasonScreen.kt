package com.example.stylecraft.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stylecraft.domain.model.ColorSeason
import com.example.stylecraft.presentation.common.OnboardingHeader
import com.example.stylecraft.presentation.common.PrimaryButton
import com.example.stylecraft.presentation.common.SecondaryButton
import com.example.stylecraft.presentation.common.SelectableCard

// Sample colors for each season palette
private val seasonPalettes = mapOf(
    ColorSeason.SPRING to listOf(
        Color(0xFFFFCBA4), // Peach
        Color(0xFF50C878), // Emerald
        Color(0xFFFFD700), // Gold
        Color(0xFFFF7F50), // Coral
        Color(0xFFC8AD7F)  // Beige
    ),
    ColorSeason.SUMMER to listOf(
        Color(0xFFE6E6FA), // Lavender
        Color(0xFF9DC183), // Sage
        Color(0xFFADD8E6), // Light Blue
        Color(0xFFDE5D83), // Rose
        Color(0xFF808080)  // Grey
    ),
    ColorSeason.AUTUMN to listOf(
        Color(0xFF722F37), // Burgundy
        Color(0xFF808000), // Olive
        Color(0xFFB7410E), // Rust
        Color(0xFFFFDB58), // Mustard
        Color(0xFF6E4B3A)  // Brown
    ),
    ColorSeason.WINTER to listOf(
        Color(0xFF000000), // Black
        Color(0xFF1A3A5C), // Navy
        Color(0xFFFF0000), // True Red
        Color(0xFF4169E1), // Royal Blue
        Color(0xFFFFFFFF)  // White
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSeasonScreen(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        OnboardingHeader(
            step = 2,
            totalSteps = 3,
            title = "Your color season",
            subtitle = "Which palette feels most natural on you?"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorSeason.entries.forEach { season ->
                SeasonCard(
                    season = season,
                    colors = seasonPalettes[season] ?: emptyList(),
                    isSelected = state.selectedColorSeason == season,
                    onClick = { viewModel.selectColorSeason(season) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Continue",
            onClick = onNext,
            enabled = state.selectedColorSeason != null
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBack
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SeasonCard(
    season: ColorSeason,
    colors: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    SelectableCard(
        title = season.displayName,
        description = season.description,
        isSelected = isSelected,
        onClick = onClick
    )

    // Color swatches below the card
    if (colors.isNotEmpty()) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (color == Color.White || color == Color(0xFFFFFFFF)) {
                                Modifier.background(
                                    color = Color.LightGray,
                                    shape = CircleShape
                                )
                            } else Modifier
                        )
                )
            }
        }
    }
}
