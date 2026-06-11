package com.studymate.presentation.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (uiState is QuizUiState.Review) "Detail Hasil Quiz" else "StudyMate Quiz 🧠", 
                        fontWeight = FontWeight.Black
                    ) 
                },
                navigationIcon = {
                    if (uiState is QuizUiState.Review) {
                        IconButton(onClick = { viewModel.backToHistory() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is QuizUiState.Review -> {
                    QuizReviewView(state.history)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // SECTION 1: Mulai Quiz Baru
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "Asah Otakmu!", 
                                        style = MaterialTheme.typography.titleLarge, 
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Kerjakan kuis dari catatan yang sudah di-Refine AI.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = onNavigateToSelectNote,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Add, null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Pilih Catatan & Mulai")
                                    }
                                }
                            }
                        }

                        // SECTION 2: Kuis Aktif / Loading / Error
                        when (val activeState = uiState) {
                            is QuizUiState.Loading -> {
                                item {
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(
                                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text("AI sedang menyiapkan kuis...")
                                        }
                                    }
                                }
                            }
                            is QuizUiState.ActiveSession -> {
                                item {
                                    if (activeState.isFinished) {
                                        QuizResultCard(activeState, onFinish = { viewModel.backToHistory() })
                                    } else {
                                        QuizActiveCard(
                                            session = activeState,
                                            onAnswerSelected = { viewModel.submitAnswer(activeState.currentIndex, it) }
                                        )
                                    }
                                }
                            }
                            is QuizUiState.Error -> {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("Waduh, ada kendala:", fontWeight = FontWeight.Bold)
                                            Text(activeState.message, style = MaterialTheme.typography.bodySmall)
                                            TextButton(onClick = { viewModel.backToHistory() }) {
                                                Text("Coba Lagi")
                                            }
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }

                        // SECTION 3: Riwayat Quiz
                        item {
                            Text(
                                "Riwayat Quiz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (history.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Belum ada riwayat kuis.", color = Color.Gray)
                                }
                            }
                        } else {
                            items(history) { item ->
                                HistoryItem(
                                    item = item, 
                                    onDelete = { viewModel.deleteHistory(item.id) },
                                    onClick = { viewModel.startReview(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizActiveCard(
    session: QuizUiState.ActiveSession,
    onAnswerSelected: (Int) -> Unit
) {
    val currentQuestion = session.questions[session.currentIndex]
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { (session.currentIndex + 1).toFloat() / session.questions.size },
                    modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
                    color = PrimaryLight
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("${session.currentIndex + 1}/${session.questions.size}", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            currentQuestion.options.forEachIndexed { index, option ->
                OutlinedButton(
                    onClick = { onAnswerSelected(index) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(option, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun QuizResultCard(
    session: QuizUiState.ActiveSession,
    onFinish: () -> Unit
) {
    val score = session.answers.filter { (idx, ans) -> 
        session.questions[idx].correctAnswerIndex == ans 
    }.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SuccessStreak.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉 Selesai!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Skor Anda: $score / ${session.questions.size}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SuccessStreak
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Text("Tutup Kuis")
            }
        }
    }
}

@Composable
private fun HistoryItem(item: QuizHistory, onDelete: () -> Unit, onClick: () -> Unit) {
    val date = remember(item.createdAt) {
        val instant = Instant.fromEpochMilliseconds(item.createdAt)
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.dayOfMonth}/${dt.monthNumber}/${dt.year} ${dt.hour}:${dt.minute.toString().padStart(2, '0')}"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
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
private fun QuizReviewView(history: QuizHistory) {
    if (history.questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Detail soal tidak tersedia untuk riwayat lama.", textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(history.questions) { index, question ->
                val userAnswer = history.userAnswers[index]
                val isCorrect = userAnswer == question.correctAnswerIndex

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) 
                            Color(0xFFE8F5E9)
                        else 
                            Color(0xFFFFEBEE)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Pertanyaan ${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            question.question,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        question.options.forEachIndexed { optIdx, option ->
                            val isSelected = userAnswer == optIdx
                            val isRightAnswer = question.correctAnswerIndex == optIdx
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        isRightAnswer -> Icons.Default.CheckCircle
                                        isSelected -> Icons.Default.Cancel
                                        else -> Icons.Default.RadioButtonUnchecked
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isRightAnswer -> Color(0xFF4CAF50)
                                        isSelected -> Color(0xFFF44336)
                                        else -> Color.Gray
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isRightAnswer) Color(0xFF2E7D32) else Color.Unspecified
                                )
                            }
                        }
                        
                        if (question.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "💡 Penjelasan: ${question.explanation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
