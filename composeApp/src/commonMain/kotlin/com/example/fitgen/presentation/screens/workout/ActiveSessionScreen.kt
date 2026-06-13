package com.example.fitgen.presentation.screens.workout

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(
    exerciseName: String,
    challengeId: String? = null,
    challengeDay: Int? = null,
    viewModel: ActiveSessionViewModel,
    onNavigateBack: () -> Unit,
    onSessionFinished: () -> Unit = onNavigateBack
) {
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()

    val exercisesList by viewModel.exercisesList.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val currentExerciseName by viewModel.currentExerciseName.collectAsState()
    val isLastExercise by viewModel.isLastExercise.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(exerciseName, challengeId, challengeDay)
    }

    // Jika sudah selesai disimpan, otomatis kembali (diatur di AppNavHost)
    LaunchedEffect(isFinished) {
        if (isFinished) {
            onSessionFinished()
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    // Animasi denyut (pulse) untuk lingkaran luar saat bermain
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 0.8f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Active Session") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section: Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sekarang Melakukan:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentExerciseName.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                if (exercisesList.size > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "Latihan ${currentIndex + 1} dari ${exercisesList.size}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Center Section: Glowing Timer
            Box(
                modifier = Modifier
                    .size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasSize = size.minDimension
                    val radius = canvasSize / 2f
                    val strokeWidth = 16.dp.toPx()

                    // Background Track
                    drawArc(
                        color = surfaceVariant,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(canvasSize, canvasSize),
                        topLeft = Offset(0f, 0f)
                    )

                    // Animated Pulse Ring
                    val pulseRadius = radius * pulseScale
                    drawCircle(
                        color = primaryColor.copy(alpha = pulseAlpha * 0.2f),
                        radius = pulseRadius,
                        center = center
                    )

                    // Progress Arc (sisa waktu dari 60 detik)
                    val sweepProgress = (remainingSeconds / 60f) * 360f
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = sweepProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(canvasSize, canvasSize),
                        topLeft = Offset(0f, 0f)
                    )
                }

                // Text Timer di Tengah
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black
                        ),
                        fontSize = 64.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Durasi Latihan",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Bottom Section: Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLastExercise) {
                    // Selesai Button
                    FilledTonalButton(
                        onClick = { viewModel.finishSession(challengeId, challengeDay) },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "Selesai", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Selesai & Simpan", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Selanjutnya / Skip Button
                    FilledTonalButton(
                        onClick = { viewModel.skipToNext() },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Selanjutnya", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Lanjut", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Play/Pause Button
                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(8.dp, RoundedCornerShape(40.dp))
                        .clip(RoundedCornerShape(40.dp))
                        .background(MaterialTheme.colorScheme.primary),
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Mulai",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
