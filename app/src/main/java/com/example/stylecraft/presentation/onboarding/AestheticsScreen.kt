package com.example.stylecraft.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stylecraft.domain.model.Aesthetic
import com.example.stylecraft.presentation.common.OnboardingHeader
import com.example.stylecraft.presentation.common.PrimaryButton
import com.example.stylecraft.presentation.common.SecondaryButton
import com.example.stylecraft.presentation.common.SelectableChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AestheticsScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        OnboardingHeader(
            step = 3,
            totalSteps = 3,
            title = "Your style aesthetics",
            subtitle = "Pick one or more styles you gravitate towards"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Aesthetic.entries.forEach { aesthetic ->
                    SelectableChip(
                        label = aesthetic.displayName,
                        isSelected = aesthetic in state.selectedAesthetics,
                        onClick = { viewModel.toggleAesthetic(aesthetic) }
                    )
                }
            }

            // Show descriptions for selected aesthetics
            if (state.selectedAesthetics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Your vibe:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    state.selectedAesthetics.forEach { aesthetic ->
                        Text(
                            text = "• ${aesthetic.displayName}: ${aesthetic.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (state.isSaving) "Setting up..." else "Complete Setup",
            onClick = { viewModel.completeOnboarding() },
            enabled = state.selectedAesthetics.isNotEmpty() && !state.isSaving
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBack
        )
    }
}

