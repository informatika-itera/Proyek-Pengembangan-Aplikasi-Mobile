package id.pusakakata.ui.screens.flashcard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.domain.model.Word

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asah Pusaka (SRS)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.isFinished -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Latihan Selesai!", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Pusaka ilmumu semakin tajam.")
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onBack) {
                            Text("Kembali ke Beranda")
                        }
                    }
                }
                uiState.wordsToReview.isEmpty() -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Semua Pusaka Sudah Tajam!", style = MaterialTheme.typography.titleLarge)
                        Text("Belum ada kata yang perlu diulang hari ini.")
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onBack) {
                            Text("Kembali")
                        }
                    }
                }
                else -> {
                    val currentWord = uiState.wordsToReview[uiState.currentIndex]
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Kata ke ${uiState.currentIndex + 1} dari ${uiState.wordsToReview.size}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        FlipCard(
                            word = currentWord,
                            isFlipped = uiState.isFlipped,
                            onFlip = { viewModel.flipCard() }
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        if (uiState.isFlipped) {
                            Text("Bagaimana tingkat kesulitan hafalanmu?", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ReviewButton(
                                    onClick = { viewModel.submitReview(1) },
                                    icon = Icons.Default.SentimentDissatisfied,
                                    label = "Sulit",
                                    color = Color(0xFFEF5350)
                                )
                                ReviewButton(
                                    onClick = { viewModel.submitReview(2) },
                                    icon = Icons.Default.SentimentNeutral,
                                    label = "Sedang",
                                    color = Color(0xFFFFB74D)
                                )
                                ReviewButton(
                                    onClick = { viewModel.submitReview(3) },
                                    icon = Icons.Default.SentimentSatisfied,
                                    label = "Mudah",
                                    color = Color(0xFF66BB6A)
                                )
                            }
                        } else {
                            Text("Ketuk kartu untuk melihat makna", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlipCard(
    word: Word,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = word.term,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = word.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Makna:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = word.definition,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = color.copy(alpha = 0.1f))
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(32.dp))
        }
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
