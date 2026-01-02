package com.example.stylecraft.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stylecraft.domain.model.BodyShape
import com.example.stylecraft.presentation.common.OnboardingHeader
import com.example.stylecraft.presentation.common.PrimaryButton
import com.example.stylecraft.presentation.common.SecondaryButton
import com.example.stylecraft.presentation.common.SelectableCard

@Composable
fun BodyShapeScreen(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        OnboardingHeader(
            step = 1,
            totalSteps = 3,
            title = "What's your body shape?",
            subtitle = "This helps us recommend silhouettes that complement your proportions"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BodyShape.entries.forEach { shape ->
                SelectableCard(
                    title = shape.displayName,
                    description = shape.description,
                    isSelected = state.selectedBodyShape == shape,
                    onClick = { viewModel.selectBodyShape(shape) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Continue",
            onClick = onNext,
            enabled = state.selectedBodyShape != null
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBack
        )
    }
}

