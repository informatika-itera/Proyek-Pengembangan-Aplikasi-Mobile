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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.exp

/**
 * Forgetting Curve chart dengan 3 kurva overlay representasi tipe kartu.
 *
 * Static visualization — tidak butuh data input dinamis. Tujuan edukatif:
 * tunjukkan ke user kenapa spaced repetition penting.
 *
 * Sprint 3+ enhancement: bisa di-personalize berdasarkan actual retention
 * data dari user's review history (advanced).
 */
@Composable
fun ForgettingCurveChart(
    modifier: Modifier = Modifier,
) {
    val newCardColor = MaterialTheme.colorScheme.error
    val learningColor = MaterialTheme.colorScheme.tertiary
    val masteredColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val targetThresholdColor = MaterialTheme.colorScheme.secondary

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🧠 Forgetting Curve",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Probabilitas ingat tanpa review (Ebbinghaus 1885)",
                style = MaterialTheme.typography.bodySmall,
                color = labelColor,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            ) {
                val chartWidth = size.width - 28.dp.toPx()   // reserve right padding
                val chartHeight = size.height - 24.dp.toPx() // reserve bottom for x-labels
                val originX = 24.dp.toPx()                    // reserve left for y-labels
                val originY = 0f

                // GRID LINES (4 horizontal: 0%, 25%, 50%, 75%, 100%)
                val gridStroke = Stroke(width = 1.dp.toPx())
                for (i in 0..4) {
                    val y = originY + (i.toFloat() / 4f) * chartHeight
                    drawLine(
                        color = gridColor.copy(alpha = 0.5f),
                        start = Offset(originX, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }

                val sweetSpotY = originY + (1f - 0.85f) * chartHeight
                drawLine(
                    color = targetThresholdColor,
                    start = Offset(originX, sweetSpotY),
                    end = Offset(size.width, sweetSpotY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(6.dp.toPx(), 4.dp.toPx()),
                        phase = 0f,
                    ),
                )

                val daysRange = 30
                val sampleCount = 60

                fun toCanvasPoint(day: Float, retention: Float): Offset {
                    val x = originX + (day / daysRange) * chartWidth
                    val y = originY + (1f - retention) * chartHeight
                    return Offset(x, y)
                }

                fun drawCurve(stability: Double, color: Color, strokeWidth: Float) {
                    val path = Path()
                    var first = true
                    for (i in 0..sampleCount) {
                        val day = (daysRange.toFloat() * i / sampleCount)
                        val retention = exp(-day / stability).toFloat()
                        val point = toCanvasPoint(day, retention)
                        if (first) {
                            path.moveTo(point.x, point.y)
                            first = false
                        } else {
                            path.lineTo(point.x, point.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = strokeWidth),
                    )
                }

                // Kartu baru (S=1): cepat sekali drop
                drawCurve(stability = 1.0, color = newCardColor, strokeWidth = 2.5.dp.toPx())

                // Kartu learning (S=6): drop sedang
                drawCurve(stability = 6.0, color = learningColor, strokeWidth = 2.5.dp.toPx())

                // Kartu mastered (S=30): drop lambat
                drawCurve(stability = 30.0, color = masteredColor, strokeWidth = 2.5.dp.toPx())
            }

            // X-AXIS LABELS (Hari 0, 7, 14, 21, 30)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf("0", "7", "14", "21", "30").forEach { label ->
                    Text(
                        text = "${label}h",
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LEGEND
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LegendItem(
                    color = newCardColor,
                    label = "Kartu Baru",
                    detail = "Lupa 95% dalam 3 hari",
                )
                LegendItem(
                    color = learningColor,
                    label = "Sedang Belajar",
                    detail = "Lupa 50% dalam ~4 hari",
                )
                LegendItem(
                    color = masteredColor,
                    label = "Sudah Hafal",
                    detail = "Masih ingat 37% setelah 30 hari",
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 16.dp, height = 2.dp)
                            .background(targetThresholdColor),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Target SM-2 (85% retention)",
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // INSIGHT FOOTER
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "💡 SM-2 menjadwalkan review TEPAT sebelum kamu lupa — itu kenapa belajar 10 menit/hari lebih efektif daripada marathon 2 jam seminggu sekali.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    detail: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
