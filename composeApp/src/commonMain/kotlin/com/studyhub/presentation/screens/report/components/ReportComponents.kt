package com.studyhub.presentation.screens.report.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.domain.model.*
import com.studyhub.presentation.components.glassBorderColor
import com.studyhub.presentation.components.glassSurfaceColor
import com.studyhub.presentation.theme.Spacing

@Composable
fun ReportSummaryStats(
    report: LearningReport,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        SummaryStatCard(
            value = report.totalTasks.toString(),
            label = "Total Tugas",
            icon = Icons.AutoMirrored.Filled.Assignment,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        SummaryStatCard(
            value = "${(report.completionRate * 100).toInt()}%",
            label = "Selesai",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF6B8F71), // Original Secondary Green
            modifier = Modifier.weight(1f)
        )
        SummaryStatCard(
            value = formatFocusTime(report.totalFocusMinutes),
            label = "Fokus",
            icon = Icons.Default.Timer,
            color = Color(0xFFF5A623), // HighlightGold
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryStatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(color.copy(alpha = 0.1f))
            .border(
                0.5.dp,
                color.copy(alpha = 0.3f),
                MaterialTheme.shapes.large
            )
            .padding(Spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon, null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

fun formatFocusTime(minutes: Int): String = when {
    minutes >= 60 -> "${minutes / 60}h ${minutes % 60}m"
    else -> "${minutes}m"
}

@Composable
fun ReportAiInsightCard(
    insight: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
            )
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                MaterialTheme.shapes.large
            )
            .padding(Spacing.normal),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Insight AI",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                insight,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ReportStreakCard(
    report: LearningReport,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(glassSurfaceColor())
            .border(0.5.dp, glassBorderColor(), MaterialTheme.shapes.large)
            .padding(Spacing.normal),
        horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔥", style = MaterialTheme.typography.headlineMedium)
            Text(
                "${report.currentStreak}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF5A623) // HighlightGold
            )
            Text(
                "Hari Berturut",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        VerticalDivider(
            modifier = Modifier.height(60.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⏱️", style = MaterialTheme.typography.headlineMedium)
            Text(
                "${report.averageDailyFocusMinutes}m",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                "Rata-rata/Hari",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        VerticalDivider(
            modifier = Modifier.height(60.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (report.overdueTasks == 0) "✅" else "⚠️",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "${report.overdueTasks}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (report.overdueTasks == 0) Color(0xFF6B8F71) else Color(0xFFB91C1C) // Secondary vs Error
            )
            Text(
                "Terlambat",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ReportDailyFocusChart(
    data: List<DailyFocusItem>,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    ReportChartCard(title = title, subtitle = subtitle) {
        if (data.isEmpty()) {
            EmptyChartPlaceholder()
            return@ReportChartCard
        }

        val maxMinutes = data.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1
        val chartHeight = 120.dp
        val barColor = MaterialTheme.colorScheme.primary
        val todayColor = Color(0xFFF5A623) // HighlightGold

        Column {
            Row(
                modifier = Modifier.fillMaxWidth().height(chartHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (item.minutes > 0) {
                            Text(
                                "${item.minutes}m",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = if (item.isToday) todayColor else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                        }
                        val barFraction = item.minutes.toFloat() / maxMinutes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(maxOf(barFraction, 0.03f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (item.isToday) todayColor else barColor)
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEach { item ->
                    Text(
                        item.dayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (item.isToday) todayColor else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(Spacing.small))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                LegendDot(barColor, "Hari lainnya")
                LegendDot(todayColor, "Hari ini")
            }
        }
    }
}

@Composable
fun ReportCompletionDonut(
    status: TaskCompletionStatus,
    modifier: Modifier = Modifier
) {
    ReportChartCard(
        title = "Status Penyelesaian Tugas",
        subtitle = "Distribusi berdasarkan ketepatan waktu"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                val onTimeColor = MaterialTheme.colorScheme.primary
                val lateColor = Color(0xFFB91C1C) // Error Red
                val pendingColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

                Canvas(modifier = Modifier.size(120.dp)) {
                    val strokeWidth = 20.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    
                    drawCircle(
                        color = pendingColor,
                        radius = radius,
                        style = Stroke(strokeWidth)
                    )

                    if (status.total > 0) {
                        val onTimeAngle = status.onTimeRate * 360f
                        val lateAngle = status.lateRate * 360f

                        if (onTimeAngle > 0f) {
                            drawArc(
                                color = onTimeColor,
                                startAngle = -90f,
                                sweepAngle = onTimeAngle,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        if (lateAngle > 0f) {
                            drawArc(
                                color = lateColor,
                                startAngle = -90f + onTimeAngle,
                                sweepAngle = lateAngle,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(status.onTimeRate * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Selesai",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                DonutLegendItem(MaterialTheme.colorScheme.primary, "Tepat Waktu", status.onTime, (status.onTimeRate * 100).toInt())
                DonutLegendItem(Color(0xFFB91C1C), "Terlambat", status.late, (status.lateRate * 100).toInt())
                DonutLegendItem(MaterialTheme.colorScheme.outline, "Belum Selesai", status.pending, (status.pendingRate * 100).toInt())
            }
        }
    }
}

@Composable
private fun DonutLegendItem(color: Color, label: String, count: Int, pct: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text("$count tugas ($pct%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ReportWeeklyActivityChart(
    data: List<WeeklyActivityItem>,
    modifier: Modifier = Modifier
) {
    ReportChartCard(
        title = "Aktivitas Tugas Bulanan",
        subtitle = "Ditambah · Selesai · Terlambat per minggu"
    ) {
        val addedColor = MaterialTheme.colorScheme.primary
        val completedColor = Color(0xFF6B8F71) // Secondary
        val overdueColor = Color(0xFFB91C1C) // Error
        val maxVal = data.flatMap { listOf(it.added, it.completed, it.overdue) }.maxOrNull()?.coerceAtLeast(1) ?: 1
        val chartHeight = 100.dp

        Column {
            Row(
                modifier = Modifier.fillMaxWidth().height(chartHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { item ->
                    Row(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val addedFrac = item.added.toFloat() / maxVal
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(maxOf(addedFrac, 0.03f)).clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)).background(addedColor))
                        
                        val compFrac = item.completed.toFloat() / maxVal
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(maxOf(compFrac, 0.03f)).clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)).background(completedColor))
                        
                        val overdueFrac = item.overdue.toFloat() / maxVal
                        Box(modifier = Modifier.weight(1f).fillMaxHeight(maxOf(overdueFrac, 0.03f)).clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)).background(overdueColor))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                data.forEach { item ->
                    Text(item.weekLabel, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(Modifier.height(Spacing.small))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium), modifier = Modifier.fillMaxWidth()) {
                LegendDot(addedColor, "Ditambah")
                LegendDot(completedColor, "Selesai")
                LegendDot(overdueColor, "Terlambat")
            }
        }
    }
}

@Composable
fun ReportSubjectProgress(
    subjects: List<SubjectReportItem>,
    modifier: Modifier = Modifier
) {
    ReportChartCard(
        title = "Progress per Mata Kuliah",
        subtitle = "${subjects.size} mata kuliah terdaftar"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            subjects.take(6).forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                            Text(item.subject, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 120.dp))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("${item.completed}/${item.total}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            if (item.overdue > 0) {
                                Surface(shape = MaterialTheme.shapes.extraSmall, color = Color(0xFFB91C1C).copy(alpha = 0.12f)) {
                                    Text("${item.overdue} terlambat", modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = Color(0xFFB91C1C))
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            if (item.completionRate > 0) {
                                Box(modifier = Modifier.fillMaxHeight().weight(item.completionRate).background(MaterialTheme.colorScheme.primary))
                            }
                            val overdueRate = if (item.total > 0) item.overdue.toFloat() / item.total else 0f
                            if (overdueRate > 0) {
                                Box(modifier = Modifier.fillMaxHeight().weight(overdueRate).background(Color(0xFFB91C1C)))
                            }
                            val remaining = 1f - item.completionRate - overdueRate
                            if (remaining > 0) {
                                Box(modifier = Modifier.weight(maxOf(remaining, 0.01f)))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                LegendDot(MaterialTheme.colorScheme.primary, "Selesai")
                LegendDot(Color(0xFFB91C1C), "Terlambat")
                LegendDot(MaterialTheme.colorScheme.surfaceVariant, "Pending")
            }
        }
    }
}

@Composable
fun ReportPriorityBreakdown(
    breakdown: PriorityBreakdown,
    modifier: Modifier = Modifier
) {
    ReportChartCard(
        title = "Tugas per Prioritas",
        subtitle = "Selesai vs total per level prioritas"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            PriorityBar("HIGH", breakdown.highCompleted, breakdown.highTotal, Color(0xFFB91C1C)) // Error Red
            PriorityBar("MEDIUM", breakdown.mediumCompleted, breakdown.mediumTotal, Color(0xFFF5A623)) // HighlightGold
            PriorityBar("LOW", breakdown.lowCompleted, breakdown.lowTotal, Color(0xFF6B8F71)) // Secondary Green
        }
    }
}

@Composable
private fun PriorityBar(label: String, completed: Int, total: Int, color: Color) {
    val rate = if (total > 0) completed.toFloat() / total else 0f
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Surface(shape = MaterialTheme.shapes.extraSmall, color = color.copy(alpha = 0.12f), modifier = Modifier.width(56.dp)) {
            Text(label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
        }
        Box(modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(rate).clip(CircleShape).background(color))
        }
        Text("$completed/$total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
    }
}

@Composable
fun ReportConsistencyTrend(
    data: List<ConsistencyItem>,
    modifier: Modifier = Modifier
) {
    ReportChartCard(
        title = "Tren Konsistensi Fokus",
        subtitle = "Skor konsistensi belajar per hari"
    ) {
        Column {
            val lineColor = MaterialTheme.colorScheme.secondary
            val fillColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            val dotColor = MaterialTheme.colorScheme.secondary

            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                if (data.isEmpty()) return@Canvas
                val w = size.width
                val h = size.height
                val pad = 16.dp.toPx()
                val chartW = w - pad * 2
                val chartH = h - pad * 2
                val stepX = chartW / (data.size - 1).coerceAtLeast(1)

                val points = data.mapIndexed { i, item ->
                    Offset(pad + i * stepX, h - pad - item.score * chartH)
                }

                if (points.size > 1) {
                    val fillPath = Path().apply {
                        moveTo(points.first().x, h - pad)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, h - pad)
                        close()
                    }
                    drawPath(fillPath, fillColor)

                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(linePath, lineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                }

                points.forEach { point ->
                    drawCircle(color = dotColor, radius = 4.dp.toPx(), center = point)
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = point)
                }
            }
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                data.forEach { item ->
                    Text(item.dayLabel, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ReportChartCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(glassSurfaceColor())
            .border(
                0.5.dp,
                glassBorderColor(),
                MaterialTheme.shapes.extraLarge
            )
            .padding(Spacing.normal),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

@Composable
fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun EmptyChartPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
        Text("Belum ada data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
