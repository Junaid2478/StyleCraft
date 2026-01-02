package com.example.stylecraft.presentation.outfit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.FlagType
import com.example.stylecraft.domain.model.OutfitScore
import com.example.stylecraft.domain.model.ScoreFlag
import com.example.stylecraft.ui.theme.ScoreExcellent
import com.example.stylecraft.ui.theme.ScoreGood
import com.example.stylecraft.ui.theme.ScoreOkay
import com.example.stylecraft.ui.theme.ScorePoor
import com.example.stylecraft.ui.theme.Terracotta
import com.example.stylecraft.ui.theme.TerracottaLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitResultScreen(
    outfitId: String,
    onBack: () -> Unit,
    viewModel: OutfitResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(outfitId) {
        viewModel.loadOutfit(outfitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outfit Score") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.outfit != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Score ring
                state.outfit?.score?.let { score ->
                    ScoreRingCard(score = score)
                }

                // Items preview
                Text(
                    text = "Items",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.outfit?.items ?: emptyList()) { item ->
                        ItemPreviewChip(item = item)
                    }
                }

                // Score breakdown
                state.outfit?.score?.let { score ->
                    ScoreBreakdownCard(score = score)
                }

                // AI Explanation
                AIExplanationCard(
                    explanation = state.aiExplanation,
                    isLoading = state.isLoadingAI
                )

                // Flags/reasons
                state.outfit?.score?.let { score ->
                    if (score.flags.isNotEmpty()) {
                        FlagsCard(flags = score.flags)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreRingCard(score: OutfitScore) {
    val scoreColor = when {
        score.overall >= 80 -> ScoreExcellent
        score.overall >= 65 -> ScoreGood
        score.overall >= 50 -> ScoreOkay
        else -> ScorePoor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            scoreColor.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Score value
                Text(
                    text = "${score.overall}",
                    style = MaterialTheme.typography.displayLarge,
                    color = scoreColor
                )
                Text(
                    text = when {
                        score.overall >= 80 -> "Excellent"
                        score.overall >= 65 -> "Good"
                        score.overall >= 50 -> "Okay"
                        else -> "Needs Work"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ItemPreviewChip(item: ClothingItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(android.graphics.Color.parseColor(item.primaryColor.hexCode)))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = item.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ScoreBreakdownCard(score: OutfitScore) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Score Breakdown",
                style = MaterialTheme.typography.titleMedium
            )

            ScoreBar(label = "Color Harmony", score = score.colorHarmony)
            ScoreBar(label = "Silhouette", score = score.silhouetteBalance)
            ScoreBar(label = "Cohesion", score = score.cohesion)
        }
    }
}

@Composable
private fun ScoreBar(label: String, score: Int) {
    val color = when {
        score >= 80 -> ScoreExcellent
        score >= 65 -> ScoreGood
        score >= 50 -> ScoreOkay
        else -> ScorePoor
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun AIExplanationCard(
    explanation: String?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TerracottaLight.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Terracotta,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Style Notes",
                    style = MaterialTheme.typography.titleMedium,
                    color = Terracotta
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Terracotta
                    )
                    Text(
                        text = "Analyzing your outfit...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = !isLoading && explanation != null, enter = fadeIn()) {
                Text(
                    text = explanation ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FlagsCard(flags: List<ScoreFlag>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleMedium
            )

            flags.forEach { flag ->
                val (icon, color) = when (flag.type) {
                    FlagType.POSITIVE -> "✓" to ScoreExcellent
                    FlagType.WARNING -> "⚠" to ScoreOkay
                    FlagType.ISSUE -> "✗" to ScorePoor
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = icon,
                        color = color,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = flag.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (flag.impact >= 0) "+${flag.impact}" else "${flag.impact}",
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                }
            }
        }
    }
}

