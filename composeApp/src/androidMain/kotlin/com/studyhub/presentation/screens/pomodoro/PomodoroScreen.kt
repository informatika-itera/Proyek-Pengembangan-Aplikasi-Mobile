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
                title = { Text("Focus Timer", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Phase selection tabs
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                PomodoroPhase.entries.forEachIndexed { index, phase ->
                    SegmentedButton(
                        selected = uiState.phase == phase,
                        onClick = { /* Phase switching handled by timer logic */ },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = PomodoroPhase.entries.size),
                        label = { Text(phase.displayName) }
                    )
                }
            }

            // Progress Visualization
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                val progress = uiState.timeRemainingSeconds.toFloat() / uiState.totalSeconds.coerceAtLeast(1).toFloat()

                val activeColor = if (uiState.phase == PomodoroPhase.FOCUS)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = activeColor,
                    trackColor = activeColor.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = uiState.timeRemainingSeconds / 60
                    val seconds = uiState.timeRemainingSeconds % 60
                    Text(
                        text = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = uiState.phase.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = activeColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Session info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Sesi ${uiState.currentSession}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${uiState.completedSessionsToday} sesi selesai hari ini",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Ulangi")
                }

                LargeFloatingActionButton(
                    onClick = { if (uiState.isRunning) viewModel.pause() else viewModel.start() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isRunning) "Jeda" else "Mulai",
                        modifier = Modifier.size(36.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = { viewModel.skipPhase() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Lewati")
                }
            }
        }
    }
}
