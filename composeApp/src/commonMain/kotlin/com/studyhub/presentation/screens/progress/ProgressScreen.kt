package com.studyhub.presentation.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.presentation.components.*
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController) {
    val viewModel: ProgressViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ProgressUiState.Loading -> LoadingView(Modifier.padding(padding))
            is ProgressUiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadStats() },
                modifier = Modifier.padding(padding)
            )
            is ProgressUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(Spacing.normal),
                    verticalArrangement = Arrangement.spacedBy(Spacing.normal)
                ) {
                    // Weekly summary card
                    item {
                        WeeklySummaryCard(
                            completedThisWeek = state.completedThisWeek,
                            totalThisWeek = state.totalThisWeek,
                            completionRate = state.completionRate,
                            focusMinutesToday = state.focusMinutesToday
                        )
                    }

                    // Streak card
                    item {
                        StreakCard(
                            currentStreak = state.currentStreak,
                            longestStreak = state.longestStreak
                        )
                    }

                    // Subject progress
                    item {
                        Text(
                            "Progress per Mata Kuliah",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(
                        items = state.subjectProgress,
                        key = { it.subject },
                        contentType = { "subject_progress" }
                    ) { progress ->
                        SubjectProgressItem(progress = progress)
                    }

                    // Completion rate by priority
                    item {
                        PriorityCompletionCard(
                            highCompletion = state.highPriorityRate,
                            mediumCompletion = state.medPriorityRate,
                            lowCompletion = state.lowPriorityRate
                        )
                    }
                }
            }
            is ProgressUiState.Empty -> EmptyStateView(
                message = "Belum ada data progress",
                actionLabel = "Tambah Tugas",
                onAction = {
                    navController.navigate(Screen.AddTask.createRoute())
                },
                modifier = Modifier.padding(padding)
            )
        }
    }
}
