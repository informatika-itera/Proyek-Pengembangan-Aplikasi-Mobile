package com.example.fitkos.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    onNavigateToMealLog: () -> Unit,
    onNavigateToAddMeal: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToWaterTracker: () -> Unit,
    onNavigateToExercise: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 6.dp,
                    bottom = 120.dp
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeaderSection(uiState)

            SummaryGrid(
                uiState = uiState,
                onNavigateToMealLog = onNavigateToMealLog,
                onNavigateToAI = onNavigateToAI,
                onNavigateToWaterTracker = onNavigateToWaterTracker,
                onNavigateToExercise = onNavigateToExercise
            )

            TargetHarianSection(uiState)
        }
    }
}

@Composable
private fun HeaderSection(
    uiState: DashboardUiState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Halo, ${uiState.userName}! ✨",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )

            Text(
                text = "Yuk jaga kesehatan hari ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notifikasi"
            )
        }
    }
}

@Composable
private fun SummaryGrid(
    uiState: DashboardUiState,
    onNavigateToMealLog: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToWaterTracker: () -> Unit,
    onNavigateToExercise: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ringkasan Hari Ini",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Minum Air",
                value = "${uiState.waterGlasses}/${uiState.waterTarget}",
                unit = "gelas",
                icon = "💧",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToWaterTracker() },
                color = Color(0xFFE3F2FD)
            )

            SummaryCard(
                title = "Catatan Makan",
                value = "${uiState.mealCount}",
                unit = "catatan",
                icon = "🍽️",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToMealLog() },
                color = Color(0xFFF1F8E9)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Olahraga Hari Ini",
                value = uiState.exerciseMinutes.toString(),
                unit = "menit",
                icon = "🏃",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToExercise() },
                color = Color(0xFFFFF3E0)
            )

            SummaryCard(
                title = "Langsung Tanya Asisten AI",
                value = "AI",
                unit = "Tanya AI",
                icon = "🤖",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToAI() },
                color = Color(0xFFF3E5F5)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    unit: String,
    icon: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetHarianSection(
    uiState: DashboardUiState
) {
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
                text = "Target Harian",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TargetItem(
                icon = "💧",
                title = "Air",
                current = uiState.waterGlasses,
                target = uiState.waterTarget,
                unit = "gelas",
                progress = if (uiState.waterTarget > 0) {
                    uiState.waterGlasses.toFloat() / uiState.waterTarget
                } else {
                    0f
                }
            )

            TargetItem(
                icon = "🏃",
                title = "Olahraga",
                current = uiState.exerciseMinutes,
                target = uiState.exerciseTarget,
                unit = "menit",
                progress = if (uiState.exerciseTarget > 0) {
                    uiState.exerciseMinutes.toFloat() / uiState.exerciseTarget
                } else {
                    0f
                }
            )
        }
    }
}

@Composable
private fun TargetItem(
    icon: String,
    title: String,
    current: Int,
    target: Int,
    unit: String,
    progress: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "$current / $target $unit",
                style = MaterialTheme.typography.labelSmall
            )
        }

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}