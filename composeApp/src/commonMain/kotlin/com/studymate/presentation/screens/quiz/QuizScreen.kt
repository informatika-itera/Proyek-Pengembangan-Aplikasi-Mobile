package com.studymate.presentation.screens.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.domain.model.Quiz
import com.studymate.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: QuizViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.generateQuizFromLatestNote()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quiz Time 🧠", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is QuizUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryLight, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Gemini AI sedang meracik soal...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                is QuizUiState.Success -> {
                    val currentQuiz = state.quizzes[state.currentIndex]
                    QuizContent(
                        quiz = currentQuiz,
                        currentIndex = state.currentIndex,
                        total = state.quizzes.size,
                        onNext = { viewModel.nextQuestion() }
                    )
                }
                is QuizUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("Ups! Ada kendala.", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(state.message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.generateQuizFromLatestNote() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is QuizUiState.Idle -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉 Selesai!", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
                        Text("Luar biasa, kamu makin paham materi ini!", modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.generateQuizFromLatestNote() },
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                        ) {
                            Text("Mulai Lagi", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizContent(quiz: Quiz, currentIndex: Int, total: Int, onNext: () -> Unit) {
    var selectedIndex by remember(quiz) { mutableStateOf(-1) }
    var revealed by remember(quiz) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / total },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape),
                color = PrimaryLight,
                trackColor = PrimaryLight.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("${currentIndex + 1} / $total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Question Flashcard
        Flashcard(text = quiz.question, isRevealed = revealed)

        Spacer(modifier = Modifier.height(40.dp))

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            quiz.options.forEachIndexed { index, option ->
                val isCorrect = index == quiz.correctAnswerIndex
                val isSelected = selectedIndex == index
                
                OptionButton(
                    text = option,
                    isSelected = isSelected,
                    isCorrect = isCorrect,
                    isRevealed = revealed,
                    onClick = {
                        if (!revealed) {
                            selectedIndex = index
                            revealed = true
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = revealed,
            enter = fadeIn() + expandVertically()
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight)
            ) {
                Text(
                    if (currentIndex + 1 == total) "Selesaikan Kuis" else "Pertanyaan Selanjutnya",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun Flashcard(text: String, isRevealed: Boolean) {
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .shadow(12.dp, RoundedCornerShape(32.dp))
    ) {
        if (rotation <= 90f) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                shape = RoundedCornerShape(32.dp),
                color = SuccessStreak.copy(alpha = 0.1f),
                border = BorderStroke(2.dp, SuccessStreak)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = SuccessStreak, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Kunci Jawaban Terbuka!",
                        fontWeight = FontWeight.Black,
                        color = SuccessStreak,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Review jawabanmu di bawah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessStreak.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isRevealed: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isRevealed && isCorrect -> SuccessStreak
        isRevealed && isSelected && !isCorrect -> Color(0xFFE63946) // Sharp Red
        isSelected -> PrimaryLight.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isRevealed && (isCorrect || (isSelected && !isCorrect))) Color.White else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRevealed) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(
            width = if (isSelected || (isRevealed && isCorrect)) 2.dp else 1.dp,
            color = when {
                isRevealed && isCorrect -> SuccessStreak
                isSelected -> PrimaryLight
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            }
        ),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (contentColor == Color.White) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (isRevealed && isCorrect) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = if (isRevealed && isSelected && !isCorrect) "✕" else "",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
