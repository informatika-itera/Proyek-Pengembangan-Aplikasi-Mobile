package com.example.neurodeck.presentation.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
import com.example.neurodeck.presentation.components.SectionTitle
import com.example.neurodeck.presentation.screens.stats.components.BigStatCard
import com.example.neurodeck.presentation.screens.stats.components.CardStatusBars
import com.example.neurodeck.presentation.screens.stats.components.ForgettingCurveChart
import com.example.neurodeck.presentation.screens.stats.components.PeriodFilterChips
import com.example.neurodeck.presentation.screens.stats.components.WeeklyBarChart
import org.koin.compose.viewmodel.koinViewModel

// ════════════════════════════════════════════════════════════════════════════
// StatsScreen.kt — Sprint 2 P4
//
// 📊 Stats Tab — Analytics belajar mendalam.
//
// Layout (scrollable LazyColumn):
//   1. PeriodFilterChips         — 4 chip: 7d / 30d / 90d / All
//   2. Section "Overview"
//      ├─ BigStatCard Streak
//      ├─ BigStatCard Total Reviews
//      ├─ BigStatCard Accuracy
//      └─ Row: Decks + Cards (split 50/50)
//   3. WeeklyBarChart             — Canvas bar chart 7 hari activity
//   4. CardStatusBars             — Progress bars New/Learning/Mastered
//
// No Scaffold/TopBar — chrome dari AppNavHost.
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is StatsUiState.Loading -> LoadingIndicator()

        is StatsUiState.Error -> ErrorMessage(
            message = state.message,
            onRetry = { viewModel.changePeriod(StatsPeriod.DEFAULT) },
        )

        is StatsUiState.Success -> StatsSuccessContent(
            state = state,
            onPeriodChange = viewModel::changePeriod,
        )
    }
}

@Composable
private fun StatsSuccessContent(
    state: StatsUiState.Success,
    onPeriodChange: (StatsPeriod) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ════════════════════════════════════════════════════════════════════
        // 1. PERIOD FILTER
        // ════════════════════════════════════════════════════════════════════
        item {
            PeriodFilterChips(
                selected = state.period,
                onSelect = onPeriodChange,
            )
        }

        // ════════════════════════════════════════════════════════════════════
        // 2. OVERVIEW SECTION — 4 BigStatCard
        // ════════════════════════════════════════════════════════════════════
        item { SectionTitle(text = "Overview") }

        // Row 1: Streak + Total Reviews (split 2 columns)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BigStatCard(
                    icon = Icons.Outlined.LocalFireDepartment,
                    value = "${state.streakDays} hari",
                    label = "Streak",
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                )
                BigStatCard(
                    icon = Icons.Outlined.School,
                    value = state.totalReviews.toString(),
                    label = "Review (${state.period.label})",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Row 2: Decks + Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BigStatCard(
                    icon = Icons.Outlined.Style,
                    value = state.totalDecks.toString(),
                    label = "Total Decks",
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )
                BigStatCard(
                    icon = Icons.Outlined.CheckCircle,
                    value = "${state.accuracyPercent.toInt()}%",
                    label = "Akurasi",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ════════════════════════════════════════════════════════════════════
        // 3. ACTIVITY CHART
        // ════════════════════════════════════════════════════════════════════
        item { SectionTitle(text = "Aktivitas") }
        item {
            WeeklyBarChart(dayCounts = state.activityByDay)
        }

        // ════════════════════════════════════════════════════════════════════
        // 4. CARD STATUS BREAKDOWN
        // ════════════════════════════════════════════════════════════════════
        item { SectionTitle(text = "Progres Pembelajaran") }
        item {
            CardStatusBars(breakdown = state.cardsByStatus)
        }

        // ════════════════════════════════════════════════════════════════════
        // 5. FORGETTING CURVE (P4 STRETCH GOAL ⭐)
        // ════════════════════════════════════════════════════════════════════
        item { SectionTitle(text = "Sains di Balik NeuroDeck") }
        item {
            ForgettingCurveChart()
        }

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}