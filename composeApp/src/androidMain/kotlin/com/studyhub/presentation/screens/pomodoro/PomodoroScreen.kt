package com.studyhub.presentation.screens.pomodoro

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.domain.model.PomodoroPhase
import com.studyhub.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PomodoroScreen(
    taskId: String?,
    navController: NavController
) {
    val viewModel: PomodoroViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomodoro") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Phase chips
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                PomodoroPhase.entries.forEach { phase ->
                    FilterChip(
                        selected = uiState.phase == phase,
                        onClick = { },
                        label = { Text(phase.displayName) }
                    )
                }
            }

            // Circular progress
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                val progress = uiState.timeRemainingSeconds.toFloat() / uiState.totalSeconds.coerceAtLeast(1).toFloat()

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = uiState.timeRemainingSeconds / 60
                    val seconds = uiState.timeRemainingSeconds % 60
                    Text(
                        "%02d:%02d".format(minutes, seconds),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(uiState.phase.displayName, style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Controls
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.large)) {
                FilledTonalIconButton(onClick = { viewModel.reset() }) {
                    Icon(Icons.Default.Replay, "Reset")
                }

                FloatingActionButton(
                    onClick = { if (uiState.isRunning) viewModel.pause() else viewModel.start() }
                ) {
                    Icon(if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, if (uiState.isRunning) "Pause" else "Mulai")
                }

                FilledTonalIconButton(onClick = { viewModel.skipPhase() }) {
                    Icon(Icons.Default.SkipNext, "Lewati")
                }
            }
        }
    }
}
