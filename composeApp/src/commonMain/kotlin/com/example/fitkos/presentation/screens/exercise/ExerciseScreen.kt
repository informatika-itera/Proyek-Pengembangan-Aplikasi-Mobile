package com.example.fitkos.presentation.screens.exercise

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExerciseViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Olahraga",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            // Circular Stopwatch UI
            CircularStopwatch(
                formattedTime = uiState.formattedTime.substringBefore("."), // Hanya HH:MM:SS
                isRunning = uiState.isRunning,
                progress = (uiState.elapsedMillis % 60000) / 60000f, // Progress per menit
                onStart = viewModel::startTimer,
                onPause = viewModel::pauseTimer,
                onReset = viewModel::resetTimer
            )

            Button(
                onClick = viewModel::saveSession,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Simpan Durasi", fontWeight = FontWeight.Bold)
            }

            ExerciseTipsSection()
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun CircularStopwatch(
    formattedTime: String,
    isRunning: Boolean,
    progress: Float,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Waktu Olahraga",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            // Background Circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Progress Arc
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color(0xFF2E7D32),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formattedTime,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Menit : Detik",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start/Pause Button
            IconButton(
                onClick = if (isRunning) onPause else onStart,
                modifier = Modifier.size(64.dp).background(if (isRunning) Color(0xFFFFB74D) else Color(0xFF2E7D32), CircleShape)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Reset Button
            IconButton(
                onClick = onReset,
                modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun ExerciseTipsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tips olahraga singkat",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        ExerciseTipItem(
            icon = "🧘",
            title = "Stretching 5 menit",
            desc = "Lakukan peregangan ringan untuk tubuh lebih rileks."
        )

        ExerciseTipItem(
            icon = "🧱",
            title = "Plank 1 menit",
            desc = "Perkuat otot inti dan tingkatkan stabilitas."
        )

        ExerciseTipItem(
            icon = "🚶",
            title = "Jalan cepat 10 menit",
            desc = "Tingkatkan detak jantung dan stamina."
        )

        ExerciseTipItem(
            icon = "💪",
            title = "Push-up 10x",
            desc = "Latih kekuatan tubuh bagian atas dengan mudah."
        )

        ExerciseTipItem(
            icon = "🪑",
            title = "Squat 15x",
            desc = "Bagus untuk otot kaki dan metabolisme tubuh."
        )

        ExerciseTipItem(
            icon = "🏠",
            title = "Bersih Kos",
            desc = "Aktivitas fisik ringan yang juga bikin kamar rapi."
        )

        ExerciseTipItem(
            icon = "🪜",
            title = "Naik Tangga",
            desc = "Latihan kardio gratis di gedung atau area kos."
        )

        ExerciseTipItem(
            icon = "🤸",
            title = "Jumping Jacks 20x",
            desc = "Meningkatkan detak jantung dengan gerakan cepat."
        )
    }
}

@Composable
private fun ExerciseTipItem(
    icon: String,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 24.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
