package com.example.bridgebit.presentation.screens.insights

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DataExploration
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.filled.Close
import com.example.bridgebit.presentation.components.ShimmerCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showQuizDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistik Belajar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showQuizDialog = true },
                icon = {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                },
                text = { Text("Uji Kosakata (AI Quiz)", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── HEADER: STREAK CARD ───────────────────────────────────────────
            if (state.isLoading) {
                ShimmerCard(modifier = Modifier.fillMaxWidth().height(84.dp))
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                StreakCard(streakCount = state.currentStreak)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── SECTION 1: KEY METRICS ────────────────────────────────────────
            if (state.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShimmerCard(modifier = Modifier.weight(1f))
                    ShimmerCard(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                ShimmerCard(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(12.dp))
                ShimmerCard(modifier = Modifier.fillMaxWidth().height(180.dp))

                Spacer(modifier = Modifier.height(28.dp))

                // ── SECTION 2: TOPIC DISTRIBUTION (SKELETON) ────────────────────
                SectionHeader(title = "Distribusi Topik", subtitle = "berdasarkan kategori")
                Spacer(modifier = Modifier.height(12.dp))

                ShimmerCard(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(28.dp))

                // ── SECTION 3: VOCABULARY GROWTH CHART (SKELETON) ───────────────
                SectionHeader(title = "Pertumbuhan Kosakata", subtitle = "7 hari terakhir")
                Spacer(modifier = Modifier.height(12.dp))

                ShimmerCard(modifier = Modifier.fillMaxWidth())
            } else {
                var isVisible by remember { mutableStateOf(false) }
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    isVisible = true
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = isVisible,
                    enter = androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(500)) + androidx.compose.animation.slideInVertically(androidx.compose.animation.core.tween(500)) { it / 8 }
                ) {
                    Column {
                        Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Total Terjemahan",
                        value = state.totalTranslations.toString(),
                        icon = Icons.Default.GTranslate,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    MetricCard(
                        title = "Kategori Favorit",
                        value = state.topCategory,
                        icon = Icons.Default.DataExploration,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE65100)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Language direction card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Arah Bahasa Paling Sering",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = state.topLanguagePair,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                WeeklyGoalRing(
                    progress = state.weeklyTranslations,
                    goal = state.weeklyGoal,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── SECTION 2: TOPIC DISTRIBUTION ────────────────────────────────
                SectionHeader(title = "Distribusi Topik", subtitle = "berdasarkan kategori")
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TopicDistributionSection(topicsDistribution = state.topicsDistribution)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── SECTION 3: VOCABULARY GROWTH CHART ───────────────────────────
                SectionHeader(title = "Pertumbuhan Kosakata", subtitle = "7 hari terakhir")
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (state.dailyTranslationStats.all { it.second == 0 }) {
                            Text(
                                text = "Belum ada data terjemahan minggu ini.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        } else {
                            VocabularyBarChart(data = state.dailyTranslationStats)
                        }
                    }
                }
                    }
                }
            }

            // Bottom padding so FAB doesn't obscure last content
            Spacer(modifier = Modifier.height(88.dp))
        }
    }

    // ── QUIZ DIALOG ───────────────────────────────────────────────────────────
    if (showQuizDialog) {
        QuizDialog(
            viewModel = viewModel,
            onDismiss = {
                showQuizDialog = false
                viewModel.resetQuiz()
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION HEADER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QUIZ DIALOG
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Full-height dialog housing the three quiz phases:
 *   1. Setup — pick question count, tap "Mulai Kuis".
 *   2. In-Progress — progress header + question card + answer options.
 *   3. Results — arc gauge + score breakdown.
 *
 * The phase is determined by [InsightsViewModel] state flows.
 */
@Composable
private fun QuizDialog(
    viewModel: InsightsViewModel,
    onDismiss: () -> Unit
) {
    val questions by viewModel.quizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswer by viewModel.selectedAnswerIndex.collectAsState()
    val isFinished by viewModel.isQuizFinished.collectAsState()
    val correctCount by viewModel.correctAnswersCount.collectAsState()
    val quizError by viewModel.quizError.collectAsState()
    val isLoading by viewModel.isLoadingQuiz.collectAsState()
    val selectedCount by viewModel.selectedQuestionCount.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // ── Dialog Header ─────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            isFinished -> "Hasil Kuis"
                            questions.isNotEmpty() -> "Vocabulary Quiz"
                            else -> "Vocabulary Quiz"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Phase Router ──────────────────────────────────────────────
                AnimatedContent(
                    targetState = when {
                        isFinished -> QuizPhase.RESULTS
                        questions.isNotEmpty() -> QuizPhase.IN_PROGRESS
                        else -> QuizPhase.SETUP
                    },
                    transitionSpec = {
                        (slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)))
                            .togetherWith(
                                slideOutHorizontally(tween(200)) { -it } + fadeOut(tween(200))
                            )
                    },
                    label = "quizPhaseTransition"
                ) { phase ->
                    when (phase) {
                        QuizPhase.SETUP -> SetupPhase(
                            selectedCount = selectedCount,
                            isLoading = isLoading,
                            quizError = quizError,
                            onCountSelected = viewModel::setQuestionCount,
                            onStartQuiz = viewModel::generateQuiz
                        )
                        QuizPhase.IN_PROGRESS -> InProgressPhase(
                            questions = questions,
                            currentIndex = currentIndex,
                            selectedAnswer = selectedAnswer,
                            onAnswer = viewModel::answerQuestion,
                            onNext = viewModel::nextQuestion
                        )
                        QuizPhase.RESULTS -> ResultsPhase(
                            correctCount = correctCount,
                            totalCount = questions.size,
                            onRetry = {
                                viewModel.resetQuiz()
                            }
                        )
                    }
                }
            }
        }
    }
}

private enum class QuizPhase { SETUP, IN_PROGRESS, RESULTS }

// ── Setup Phase ───────────────────────────────────────────────────────────────

@Composable
private fun SetupPhase(
    selectedCount: Int,
    isLoading: Boolean,
    quizError: String?,
    onCountSelected: (Int) -> Unit,
    onStartQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Menyiapkan Kosakata...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            QuizGeneratorCard(
                selectedCount = selectedCount,
                isLoading = false,
                quizError = quizError,
                onCountSelected = onCountSelected,
                onStartQuiz = onStartQuiz
            )
        }
    }
}

// ── In-Progress Phase ─────────────────────────────────────────────────────────

@Composable
private fun InProgressPhase(
    questions: List<QuizQuestion>,
    currentIndex: Int,
    selectedAnswer: Int?,
    onAnswer: (Int) -> Unit,
    onNext: () -> Unit
) {
    if (questions.isEmpty()) return

    val currentQ = questions[currentIndex]
    val isAnswered = selectedAnswer != null
    val optionLabels = listOf("A", "B", "C", "D")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Progress indicator
        QuizProgressHeader(
            currentIndex = currentIndex,
            totalCount = questions.size
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = currentQ.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Answer options
        currentQ.options.forEachIndexed { index, option ->
            QuizAnswerOption(
                optionLabel = optionLabels.getOrElse(index) { "$index" },
                optionText = option,
                isAnswered = isAnswered,
                isSelected = index == selectedAnswer,
                isCorrect = index == currentQ.correctOptionIndex,
                onClick = { onAnswer(index) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Explanation card — slides in after answering
        AnimatedVisibility(
            visible = isAnswered,
            enter = fadeIn(tween(400)) + androidx.compose.animation.expandVertically(tween(400)),
            exit = fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 Penjelasan",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentQ.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Next / Finish button
                androidx.compose.material3.Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (currentIndex < questions.size - 1) "Soal Selanjutnya →"
                               else "Lihat Hasil 🏆",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Results Phase ─────────────────────────────────────────────────────────────

@Composable
private fun ResultsPhase(
    correctCount: Int,
    totalCount: Int,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        QuizResultScreen(
            correctCount = correctCount,
            totalCount = totalCount,
            onRetry = onRetry
        )
    }
}