package com.studymate.presentation.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.domain.model.QuizHistory
import com.studymate.domain.model.QuizQuestion
import com.studymate.presentation.theme.PrimaryLight
import com.studymate.presentation.theme.SuccessStreak
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel = koinViewModel(),
    onNavigateToSelectNote: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StudyMate Quiz 🧠", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (uiState !is QuizUiState.History) {
                        IconButton(onClick = { viewModel.backToHistory() }) {
                            Icon(Icons.Default.Close, contentDescription = "Batal")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is QuizUiState.History -> {
                    HistoryContent(
                        history = history,
                        onNewQuiz = onNavigateToSelectNote,
                        onDeleteHistory = { viewModel.deleteHistory(it) }
                    )
                }
                is QuizUiState.Loading -> {
                    LoadingView("AI sedang merancang soal dari catatanmu...")
                }
                is QuizUiState.ActiveSession -> {
                    if (state.isFinished) {
                        QuizResultView(
                            session = state,
                            onFinish = { viewModel.backToHistory() }
                        )
                    } else {
                        QuizActiveView(
                            session = state,
                            onAnswerSelected = { ansIdx -> 
                                viewModel.submitAnswer(state.currentIndex, ansIdx)
                            }
                        )
                    }
                }
                is QuizUiState.Error -> {
                    ErrorView(state.message, onRetry = { viewModel.backToHistory() })
                }
            }
        }
    }
}

@Composable
private fun HistoryContent(
    history: List<QuizHistory>,
    onNewQuiz: () -> Unit,
    onDeleteHistory: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top section with New Quiz Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Button(
                onClick = onNewQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mulai Quiz Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Bottom section with History
        Text(
            "Riwayat Quiz",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        if (history.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Belum ada riwayat quiz.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { item ->
                    HistoryItem(item, onDelete = { onDeleteHistory(item.id) })
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(item: QuizHistory, onDelete: () -> Unit) {
    val date = remember(item.createdAt) {
        val instant = Instant.fromEpochMilliseconds(item.createdAt)
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.dayOfMonth}/${dt.monthNumber}/${dt.year} ${dt.hour}:${dt.minute.toString().padStart(2, '0')}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.noteTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$date • Skor: ${item.score}/${item.totalQuestions}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun QuizActiveView(
    session: QuizUiState.ActiveSession,
    onAnswerSelected: (Int) -> Unit
) {
    val currentQuestion = session.questions[session.currentIndex]
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress
        LinearProgressIndicator(
            progress = { (session.currentIndex + 1).toFloat() / session.questions.size },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = PrimaryLight
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("${session.currentIndex + 1} / ${session.questions.size}", style = MaterialTheme.typography.labelMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Question
        Text(
            text = currentQuestion.question,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            currentQuestion.options.forEachIndexed { index, option ->
                Button(
                    onClick = { onAnswerSelected(index) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(option, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun QuizResultView(
    session: QuizUiState.ActiveSession,
    onFinish: () -> Unit
) {
    val score = session.answers.filter { (idx, ans) -> 
        session.questions[idx].correctAnswerIndex == ans 
    }.size

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉 Selesai!", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Kamu menjawab $score dari ${session.questions.size} soal dengan benar!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Kembali ke Riwayat", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Error", style = MaterialTheme.typography.headlineMedium)
        Text(message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRetry) {
            Text("Kembali")
        }
    }
}
