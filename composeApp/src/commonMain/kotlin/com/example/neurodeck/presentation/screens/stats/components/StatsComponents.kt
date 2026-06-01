package com.example.neurodeck.presentation.screens.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.neurodeck.presentation.screens.stats.CardStatusBreakdown
import com.example.neurodeck.presentation.screens.stats.StatsPeriod

// ════════════════════════════════════════════════════════════════════════════
// StatsComponents.kt — Sprint 2 P4
//
// Components untuk Stats Tab:
//   - PeriodFilterChips    — chip group filter 7d/30d/90d/All
//   - BigStatCard          — kartu besar dengan icon + nilai + label
//   - WeeklyBarChart       — Canvas bar chart 7 hari activity
//   - CardStatusBars       — 3 horizontal progress bar (New/Learning/Mastered)
// ════════════════════════════════════════════════════════════════════════════

/**
 * Filter chips row untuk pilih periode stats.
 *
 * 4 chips horizontal: 7 Hari / 30 Hari / 90 Hari / Semua.
 * Selected chip highlighted dengan primary color.
 */
@Composable
fun PeriodFilterChips(
    selected: StatsPeriod,
    onSelect: (StatsPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatsPeriod.entries.forEach { period ->
            FilterChip(
                selected = selected == period,
                onClick = { onSelect(period) },
                label = { Text(text = period.label) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Big stat card untuk angka achievement utama (Streak, Reviews, Accuracy, dll).
 *
 * Layout: icon kiri + (value besar + label kecil) kanan.
 *
 * @param icon         Material icon untuk visual cue.
 * @param value        Angka utama yang besar.
 * @param label        Caption di bawah angka.
 * @param accentColor  Warna icon dan value text.
 */
@Composable
fun BigStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Bar chart Canvas untuk activity 7 hari terakhir.
 *
 * Custom drawing pakai Compose `Canvas` — tidak butuh chart library external
 * (lebih ringan binary size, lebih konsisten cross-platform).
 *
 * Layout:
 *   - Y axis: jumlah review per hari (auto-scaled ke max value)
 *   - X axis: 7 hari (today di kanan, 6 hari lalu di kiri)
 *   - Bar styling: rounded top, primary color, value label di atas tiap bar
 *
 * @param dayCounts  Map<dayOffset (0=today, 6=6 days ago), reviewCount>
 */
@Composable
fun WeeklyBarChart(
    dayCounts: Map<Int, Int>,
    modifier: Modifier = Modifier,
) {
    val data = (6 downTo 0).map { offset -> dayCounts[offset] ?: 0 }
    val maxCount = (data.maxOrNull() ?: 0).coerceAtLeast(1)  // hindari div by 0
    val barColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labels = listOf("6h", "5h", "4h", "3h", "2h", "1h", "Hari ini")

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Activity 7 Hari Terakhir",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total review per hari",
                style = MaterialTheme.typography.bodySmall,
                color = labelColor,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
            ) {
                val barCount = data.size
                val totalSpacing = size.width * 0.15f  // 15% reserved for spacing
                val barWidth = (size.width - totalSpacing) / barCount
                val spacing = totalSpacing / (barCount + 1)
                val chartHeight = size.height - 24.dp.toPx()  // reserve for x-axis labels

                // Draw baseline grid
                drawLine(
                    color = gridColor,
                    start = Offset(0f, chartHeight),
                    end = Offset(size.width, chartHeight),
                    strokeWidth = 1.dp.toPx(),
                )

                // Draw each bar
                data.forEachIndexed { index, count ->
                    val barHeight = (count.toFloat() / maxCount) * chartHeight * 0.9f
                    val x = spacing + index * (barWidth + spacing)
                    val y = chartHeight - barHeight

                    // Bar (rounded top kalau ada value)
                    if (count > 0) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                4.dp.toPx(), 4.dp.toPx(),
                            ),
                        )
                    } else {
                        // Empty bar — sedikit visible supaya struktur grid terlihat
                        drawRect(
                            color = gridColor.copy(alpha = 0.3f),
                            topLeft = Offset(x, chartHeight - 4.dp.toPx()),
                            size = Size(barWidth, 4.dp.toPx()),
                        )
                    }
                }
            }

            // X-axis labels — Row di bawah Canvas (lebih simple dari drawText API)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Max value indicator
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Maks: $maxCount review/hari",
                style = MaterialTheme.typography.labelSmall,
                color = labelColor,
            )
        }
    }
}

/**
 * Card status breakdown — 3 horizontal progress bars + count labels.
 *
 * Visual:
 *   [████░░░░░] New      15
 *   [██████░░░] Learning 23
 *   [███████░░] Mastered 31
 *
 * Total ditampilkan di header.
 */
@Composable
fun CardStatusBars(
    breakdown: CardStatusBreakdown,
    modifier: Modifier = Modifier,
) {
    val total = breakdown.total.coerceAtLeast(1)  // hindari div 0
    val newColor = MaterialTheme.colorScheme.error
    val learningColor = MaterialTheme.colorScheme.tertiary
    val masteredColor = MaterialTheme.colorScheme.primary

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Status Kartu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Total ${breakdown.total} kartu",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))

            StatusBarRow(
                label = "Baru",
                count = breakdown.newCount,
                fraction = breakdown.newCount.toFloat() / total,
                color = newColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusBarRow(
                label = "Belajar",
                count = breakdown.learningCount,
                fraction = breakdown.learningCount.toFloat() / total,
                color = learningColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusBarRow(
                label = "Hafal",
                count = breakdown.masteredCount,
                fraction = breakdown.masteredCount.toFloat() / total,
                color = masteredColor,
            )
        }
    }
}

@Composable
private fun StatusBarRow(
    label: String,
    count: Int,
    fraction: Float,
    color: Color,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Custom progress bar pakai Box dengan colored child sebesar fraction
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(end = (1f - fraction.coerceIn(0f, 1f)).times(0).dp),
        ) {
            // Background track
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                    ),
            )
            // Filled portion
            if (fraction > 0f) {
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .height(8.dp)
                        .background(
                            color = color,
                            shape = RoundedCornerShape(4.dp),
                        ),
                )
            }
        }
    }
}

// (End of file)