package com.example.bridgebit.presentation.screens.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bridgebit.domain.model.QuizQuestion
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────────────────────
// METRIC CARD
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A gradient-tinted stat card showing an icon, label, and a bold value.
 */
@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STREAK CARD
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A motivational header card showing the current learning streak.
 */
@Composable
fun StreakCard(
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFF8F00), Color(0xFFE64A19))
    )

    val animatedStreak by animateIntAsState(
        targetValue = streakCount,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "StreakAnimation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🔥",
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = "Learning Streak",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = if (streakCount > 0) "$animatedStreak Hari Berturut-turut!" else "Mulai Streak Hari Ini!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// WEEKLY GOAL RING
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A circular progress ring for the weekly translation goal.
 */
@Composable
fun WeeklyGoalRing(
    progress: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (goal > 0) (progress.toFloat() / goal).coerceIn(0f, 1f) else 0f
    
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1500),
        label = "weeklyGoalAnim"
    )
    
    val animatedProgressCount by animateIntAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "weeklyGoalCountAnim"
    )
    
    LaunchedEffect(percentage) { animationProgress = percentage }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Target Minggu Ini",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    if (animatedProgress > 0f) {
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$animatedProgressCount",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/ $goal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            val remaining = (goal - progress).coerceAtLeast(0)
            Text(
                text = if (remaining > 0) "$remaining lagi menuju target!" else "Target tercapai! 🎉",
                style = MaterialTheme.typography.labelSmall,
                color = if (remaining > 0) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF43A047)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// VOCABULARY BAR CHART
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A custom Canvas bar chart showing translation counts for the last 7 days.
 *
 * Uses only Compose's built-in [Canvas] API — no external charting library.
 * Bars animate from 0 to their actual height on first composition.
 *
 * @param data List of (dayLabel, count) pairs ordered Sunday→Saturday.
 */
@Composable
fun VocabularyBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val maxValue = data.maxOfOrNull { it.second }?.coerceAtLeast(1)?.toFloat() ?: 1f

    // Animate bar heights on first render
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 900)
    )
    LaunchedEffect(data) { animationProgress = 1f }

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val barCount = data.size
            if (barCount == 0) return@Canvas

            val horizontalPadding = 8.dp.toPx()
            val totalWidth = size.width - horizontalPadding * 2
            val barWidth = (totalWidth / barCount) * 0.55f
            val gapWidth = (totalWidth / barCount) * 0.45f
            val chartHeight = size.height - 24.dp.toPx() // reserve bottom for labels

            data.forEachIndexed { index, (_, count) ->
                val x = horizontalPadding + index * (barWidth + gapWidth)
                val barHeightFraction = (count.toFloat() / maxValue) * animatedProgress
                val barHeightPx = barHeightFraction * chartHeight

                // Background track
                drawRoundRect(
                    color = surfaceVariant,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, chartHeight),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )

                // Filled bar
                if (barHeightPx > 0f) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryColor.copy(alpha = 0.5f)),
                            startY = chartHeight - barHeightPx,
                            endY = chartHeight
                        ),
                        topLeft = Offset(x, chartHeight - barHeightPx),
                        size = Size(barWidth, barHeightPx),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )
                }
            }
        }

        // Day labels below bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TOPIC DISTRIBUTION SECTION
// ─────────────────────────────────────────────────────────────────────────────

/** Palette of distinct accent colors cycled for each topic bar. */
private val topicColors = listOf(
    Color(0xFFE53935), // Red
    Color(0xFF1E88E5), // Blue
    Color(0xFF43A047), // Green
    Color(0xFFFF8F00), // Amber
    Color(0xFF8E24AA), // Purple
    Color(0xFF00897B), // Teal
    Color(0xFFF4511E), // Deep Orange
)

/**
 * Animated horizontal progress bars for topic distribution.
 *
 * Each bar shows: colored dot · category name · count · percentage.
 * Bars animate smoothly when data changes.
 */
@Composable
fun TopicDistributionSection(
    topicsDistribution: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (topicsDistribution.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada data riwayat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val total = topicsDistribution.values.sum().coerceAtLeast(1)
            val maxCount = topicsDistribution.values.maxOrNull()?.toFloat() ?: 1f
            val sortedEntries = topicsDistribution.entries
                .sortedByDescending { it.value }

            sortedEntries.forEachIndexed { colorIndex, (category, count) ->
                val accentColor = topicColors[colorIndex % topicColors.size]
                val percentage = (count.toFloat() / total * 100).roundToInt()
                val progressFraction = count.toFloat() / maxCount

                val animatedProgress by animateFloatAsState(
                    targetValue = progressFraction,
                    animationSpec = tween(durationMillis = 900, delayMillis = colorIndex * 80)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                            Text(
                                text = "($percentage%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = accentColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QUIZ GENERATOR CARD (CTA)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Eye-catching gradient CTA card that lets the user pick a question count
 * and trigger quiz generation.
 *
 * @param selectedCount     The currently selected question count.
 * @param isLoading         True while quiz is being generated.
 * @param onCountSelected   Called when user taps a count chip.
 * @param onStartQuiz       Called when user taps the "Mulai Kuis" button.
 */
@Composable
fun QuizGeneratorCard(
    selectedCount: Int,
    isLoading: Boolean,
    quizError: String?,
    onCountSelected: (Int) -> Unit,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFB71C1C), Color(0xFFE64A19))
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(gradientBrush)
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "AI Vocabulary Quiz",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Test your vocabulary from Phrase Vault",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Berapa soal yang ingin diuji?",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(3, 5, 10).forEach { count ->
                    val isSelected = count == selectedCount
                    Surface(
                        modifier = Modifier.clickable(enabled = !isLoading) {
                            onCountSelected(count)
                        },
                        shape = RoundedCornerShape(50.dp),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            text = "$count",
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFFB71C1C) else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val infiniteTransition = rememberInfiniteTransition()
            val buttonScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "buttonPulse"
            )

            Button(
                onClick = onStartQuiz,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .scale(if (!isLoading) buttonScale else 1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFB71C1C),
                    disabledContainerColor = Color.White.copy(alpha = 0.5f),
                    disabledContentColor = Color(0xFFB71C1C).copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    Text("Menyiapkan Kosakata...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mulai Kuis", fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(
                visible = quizError != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (quizError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = quizError,
                        color = Color(0xFFFFCDD2),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QUIZ PROGRESS HEADER
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Shows "Soal X dari N" and a [LinearProgressIndicator] at the top of the quiz screen.
 *
 * Progress value animates smoothly when [currentIndex] changes.
 */
@Composable
fun QuizProgressHeader(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalCount > 0) (currentIndex + 1).toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = 200f)
    )
    val targetPercentage = if (totalCount > 0) ((currentIndex + 1).toFloat() / totalCount * 100).roundToInt() else 0
    val animatedPercentage by animateIntAsState(
        targetValue = targetPercentage,
        animationSpec = spring(stiffness = 200f)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Soal ${currentIndex + 1} dari $totalCount",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${animatedPercentage}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QUIZ ANSWER OPTION
// ─────────────────────────────────────────────────────────────────────────────

private val AnswerNeutralContainerDark = Color(0xFF2C2C2C)
private val AnswerNeutralContainerLight = Color(0xFFF5F5F5)
private val AnswerSelectedBlue = Color(0xFF1565C0)
private val AnswerCorrectGreen = Color(0xFF2E7D32)
private val AnswerWrongRed = Color(0xFFC62828)

/**
 * A single multiple-choice answer option button with animated color feedback.
 *
 * States:
 * - **Unanswered**: neutral surface color, no border.
 * - **Selected (before reveal)**: blue tint — feedback that this option was tapped.
 * - **Correct answer revealed**: green tint + green border.
 * - **Wrong selection revealed**: red tint + red border.
 * - **Other un-selected options after reveal**: dimmed, no border.
 *
 * @param optionLabel   Single character label displayed before option text ("A", "B"…).
 * @param optionText    The answer option text.
 * @param isAnswered    Whether the user has already answered this question.
 * @param isSelected    Whether this option was the one the user selected.
 * @param isCorrect     Whether this option is the correct answer.
 * @param onClick       Called when the user taps this option (only when !isAnswered).
 */
@Composable
fun QuizAnswerOption(
    optionLabel: String,
    optionText: String,
    isAnswered: Boolean,
    isSelected: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetContainerColor = when {
        !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
        isCorrect -> Color(0xFF43A047).copy(alpha = 0.15f)
        isSelected && !isCorrect -> Color(0xFFE53935).copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val targetBorderColor = when {
        isAnswered && isCorrect -> Color(0xFF43A047)
        isAnswered && isSelected && !isCorrect -> Color(0xFFE53935)
        else -> Color.Transparent
    }

    val targetLabelBg = when {
        !isAnswered -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        isCorrect -> Color(0xFF43A047).copy(alpha = 0.25f)
        isSelected && !isCorrect -> Color(0xFFE53935).copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val targetLabelColor = when {
        !isAnswered -> MaterialTheme.colorScheme.primary
        isCorrect -> AnswerCorrectGreen
        isSelected && !isCorrect -> AnswerWrongRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }

    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(300),
        label = "containerColor"
    )
    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(300),
        label = "borderColor"
    )
    val labelBgColor by animateColorAsState(
        targetValue = targetLabelBg,
        animationSpec = tween(300),
        label = "labelBg"
    )
    val labelColor by animateColorAsState(
        targetValue = targetLabelColor,
        animationSpec = tween(300),
        label = "labelColor"
    )

    val textColor = when {
        isAnswered && isCorrect -> AnswerCorrectGreen
        isAnswered && isSelected && !isCorrect -> AnswerWrongRed
        isAnswered && !isCorrect && !isSelected ->
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnim"
    )

    val haptic = LocalHapticFeedback.current

    // Shake animation state
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isAnswered, isSelected, isCorrect) {
        if (isAnswered && isSelected && !isCorrect) {
            for (i in 0..2) {
                shakeOffset.animateTo(10f, animationSpec = tween(50, easing = LinearEasing))
                shakeOffset.animateTo(-10f, animationSpec = tween(50, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f, animationSpec = tween(50, easing = LinearEasing))
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp)
            .scale(scale)
            .clickable(enabled = !isAnswered, onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!isAnswered) 1.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Option label badge
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(labelBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = labelColor
                )
            }

            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isAnswered && (isCorrect || isSelected)) FontWeight.SemiBold
                             else FontWeight.Normal,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            // Icon feedback
            AnimatedVisibility(
                visible = isAnswered && (isCorrect || isSelected),
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = fadeOut()
            ) {
                when {
                    isCorrect -> Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Benar",
                        tint = AnswerCorrectGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    isSelected && !isCorrect -> Icon(
                        Icons.Default.Cancel,
                        contentDescription = "Salah",
                        tint = AnswerWrongRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QUIZ RESULT SCREEN
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Displays the final quiz result with a Canvas arc gauge, score breakdown cards,
 * and a retry button.
 *
 * @param correctCount  Number of correctly answered questions.
 * @param totalCount    Total number of questions in the quiz.
 * @param onRetry       Called when user taps "Kembali ke Menu".
 */
@Composable
fun QuizResultScreen(
    correctCount: Int,
    totalCount: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f
    val scoreText = (percentage * 100).roundToInt()

    val gaugeColor = when {
        percentage < 0.5f -> Color(0xFFE53935)
        percentage < 0.8f -> Color(0xFFFF8F00)
        else -> Color(0xFF43A047)
    }
    val scoreLabel = when {
        percentage == 1f -> "🎉 Sempurna!"
        percentage >= 0.8f -> "👍 Bagus!"
        percentage >= 0.5f -> "💪 Terus Berlatih!"
        else -> "📚 Perlu Belajar Lagi"
    }

    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedArc by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "arcAnimation"
    )
    val animatedScore by animateIntAsState(
        targetValue = scoreText,
        animationSpec = tween(durationMillis = 1000),
        label = "scoreAnimation"
    )
    LaunchedEffect(Unit) { animationProgress = percentage }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Arc gauge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.size(220.dp, 110.dp)) {
                // Track arc
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.25f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 40f, cap = StrokeCap.Round)
                )
                // Score arc
                drawArc(
                    color = gaugeColor,
                    startAngle = 180f,
                    sweepAngle = animatedArc * 180f,
                    useCenter = false,
                    style = Stroke(width = 40f, cap = StrokeCap.Round)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = 10.dp)
            ) {
                Text(
                    text = "$animatedScore%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = gaugeColor,
                    fontSize = 48.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = scoreLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Correct / Wrong breakdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Correct card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF43A047).copy(alpha = 0.10f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Benar",
                        tint = Color(0xFF43A047),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Benar",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "$correctCount Soal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            // Wrong card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE53935).copy(alpha = 0.10f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = "Salah",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Salah",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                    Text(
                        text = "${totalCount - correctCount} Soal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val infiniteTransition = rememberInfiniteTransition(label = "retryPulse")
        val buttonScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "retryPulseAnim"
        )

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .scale(buttonScale),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Kembali ke Menu",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
