package com.example.Feelia.presentation.screens.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.usecase.EmotionStat
import com.example.Feelia.domain.usecase.MoodTrendData
import com.example.Feelia.domain.usecase.WeeklyInsight
import com.example.Feelia.domain.usecase.WordFrequency
import com.example.Feelia.presentation.components.EmptyState
import com.example.Feelia.presentation.components.LoadingIndicator
import com.example.Feelia.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analitik Mood 📊") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.weeklyInsight?.totalJournals == 0 -> {
                EmptyState(
                    title = "Belum Ada Data",
                    message = "Mulai tulis jurnal untuk melihat analitik mood kamu 🌸",
                    icon = {
                        Icon(
                            Icons.Outlined.BarChart,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Spacing.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.itemSpacing)
                ) {
                    uiState.weeklyInsight?.let { insight ->

                        // Summary Cards
                        item {
                            SummaryRow(insight = insight)
                        }

                        // Mood Trend Chart
                        item {
                            SectionCard(title = "Tren Mood 7 Hari", icon = "📈") {
                                MoodTrendChart(trendData = insight.moodTrend)
                            }
                        }

                        // Emotion Statistics
                        item {
                            SectionCard(title = "Statistik Emosi", icon = "🎯") {
                                EmotionStatsList(stats = insight.emotionStats)
                            }
                        }
                    }

                    // AI Weekly Insight
                    item {
                        AIInsightCard(
                            insight = uiState.aiInsight,
                            isLoading = uiState.isLoadingAIInsight,
                            error = uiState.aiInsightError,
                            onLoadInsight = { viewModel.loadAIInsight() }
                        )
                    }

                    // Frequent Words
                    item {
                        SectionCard(title = "Kata yang Sering Muncul", icon = "💬") {
                            Column {
                                // Filter by emotion
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                                    contentPadding = PaddingValues(horizontal = Spacing.sm)
                                ) {
                                    item {
                                        FilterChip(
                                            selected = uiState.selectedWordFilter == null,
                                            onClick = { viewModel.onWordFilterChanged(null) },
                                            label = { Text("Semua") }
                                        )
                                    }
                                    items(Emotion.entries) { emotion ->
                                        FilterChip(
                                            selected = uiState.selectedWordFilter == emotion,
                                            onClick = { viewModel.onWordFilterChanged(emotion) },
                                            label = { Text("${emotion.emoji} ${emotion.displayName}") }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(Spacing.sm))
                                FrequentWordsCloud(words = uiState.frequentWords)
                            }
                        }
                    }

                    item { Spacer(Modifier.height(Spacing.xl)) }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(insight: WeeklyInsight) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            icon = insight.dominantEmotion.emoji,
            label = "Emosi Dominan",
            value = insight.dominantEmotion.displayName,
            containerColor = Color(insight.dominantEmotion.colorHex)
        )
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            icon = "📝",
            label = "Total Jurnal",
            value = "${insight.totalJournals}",
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        SummaryStatCard(
            modifier = Modifier.weight(1f),
            icon = "🔥",
            label = "Streak",
            value = "${insight.streakDays} hari",
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    }
}

@Composable
private fun SummaryStatCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MoodTrendChart(trendData: List<MoodTrendData>) {
    if (trendData.isEmpty()) {
        Text(
            text = "Belum cukup data untuk grafik",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(Spacing.md)
        )
        return
    }
    val maxCount = trendData.maxOf { it.count }.coerceAtLeast(1)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        trendData.forEach { data ->
            val heightFraction = data.count.toFloat() / maxCount
            val animatedHeight by animateFloatAsState(
                targetValue = heightFraction,
                animationSpec = tween(durationMillis = 600),
                label = "bar_height"
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = data.emotion.emoji,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.height(Spacing.xs))
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height((120 * animatedHeight).dp.coerceAtLeast(8.dp))
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(Color(data.emotion.colorHex))
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = data.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EmotionStatsList(stats: List<EmotionStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        stats.filter { it.count > 0 }.forEach { stat ->
            EmotionStatRow(stat = stat)
        }
        if (stats.all { it.count == 0 }) {
            Text(
                text = "Belum ada data emosi minggu ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmotionStatRow(stat: EmotionStat) {
    val animatedProgress by animateFloatAsState(
        targetValue = stat.percentage / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.emotion.emoji,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stat.emotion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${stat.count} jurnal (${stat.percentage.toInt()}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(Spacing.xs))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(stat.emotion.colorHex))
                )
            }
        }
    }
}

@Composable
private fun AIInsightCard(
    insight: String?,
    isLoading: Boolean,
    error: String?,
    onLoadInsight: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = "Insight AI Minggu Ini",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            when {
                isLoading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = "AI sedang menganalisis jurnal kamu...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                insight != null -> {
                    Text(
                        text = insight,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                error != null -> {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(Spacing.sm))
                    OutlinedButton(onClick = onLoadInsight) {
                        Text("Coba Lagi")
                    }
                }
                else -> {
                    Text(
                        text = "Dapatkan insight personal dari AI tentang pola emosi kamu minggu ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(Spacing.sm))
                    Button(onClick = onLoadInsight) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(Spacing.xs))
                        Text("Analisis Sekarang")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FrequentWordsCloud(words: List<WordFrequency>) {
    if (words.isEmpty()) {
        Text(
            text = "Belum cukup data kata",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(Spacing.sm)
        )
        return
    }
    val maxCount = words.maxOf { it.count }.coerceAtLeast(1)
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        words.take(15).forEach { word ->
            val sizeFraction = word.count.toFloat() / maxCount
            val fontSize = (12 + (sizeFraction * 10)).toInt()
            Box(
                modifier = Modifier
                    .background(
                        color = Color(word.associatedEmotion.colorHex),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs)
            ) {
                Text(
                    text = "${word.word} (${word.count})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = androidx.compose.ui.unit.TextUnit(
                            fontSize.toFloat(),
                            androidx.compose.ui.unit.TextUnitType.Sp
                        )
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (sizeFraction > 0.7f) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(Spacing.md))
            content()
        }
    }
}