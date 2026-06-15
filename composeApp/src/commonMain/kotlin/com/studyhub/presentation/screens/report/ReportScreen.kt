package com.studyhub.presentation.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.screens.report.components.*
import com.studyhub.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController) {
    val viewModel: ReportViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Laporan Belajar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ReportUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            is ReportUiState.Empty -> {
                EmptyStateView(
                    message = "Belum ada data laporan",
                    actionLabel = "Tambah Tugas",
                    onAction = {
                        navController.navigate(Screen.AddTask.createRoute())
                    },
                    modifier = Modifier.padding(padding)
                )
            }
            is ReportUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(
                        horizontal = Spacing.normal,
                        vertical = Spacing.small
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.normal)
                ) {
                    // 1. Summary stats row
                    item(key = "stats") {
                        ReportSummaryStats(report = state.report)
                    }

                    // 2. AI insight card
                    if (state.report.aiInsight.isNotBlank()) {
                        item(key = "insight") {
                            ReportAiInsightCard(insight = state.report.aiInsight)
                        }
                    }

                    // 3. Streak card
                    item(key = "streak") {
                        ReportStreakCard(report = state.report)
                    }

                    // 4. Daily focus bar chart
                    item(key = "focus_chart") {
                        ReportDailyFocusChart(
                            data = state.report.dailyFocusDuration,
                            title = "Durasi Fokus Harian",
                            subtitle = "Menit per hari (7 hari terakhir)"
                        )
                    }

                    // 5. Task completion donut
                    item(key = "donut") {
                        ReportCompletionDonut(status = state.report.taskCompletionByStatus)
                    }

                    // 6. Weekly activity grouped bar
                    item(key = "weekly") {
                        ReportWeeklyActivityChart(data = state.report.weeklyActivity)
                    }

                    // 7. Subject progress
                    item(key = "subject") {
                        ReportSubjectProgress(subjects = state.report.subjectProgress)
                    }

                    // 8. Priority breakdown
                    item(key = "priority") {
                        ReportPriorityBreakdown(breakdown = state.report.priorityBreakdown)
                    }

                    // 9. Focus consistency trend line
                    item(key = "trend") {
                        ReportConsistencyTrend(data = state.report.focusConsistencyTrend)
                    }

                    item(key = "bottom") {
                        Spacer(Modifier.height(Spacing.extraLarge))
                    }
                }
            }
        }
    }
}
