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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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

// ════════════════════════════════════════════════════════════════════════════
// ForgettingCurveChart.kt — Sprint 2 P4 STRETCH GOAL ⭐
//
// 🧠 UNIQUE SELLING POINT NeuroDeck — visualisasi Ebbinghaus Forgetting Curve.
//
// KONSEP:
//   Formula Ebbinghaus (1885): R(t) = e^(-t/S)
//     - R = probabilitas recall (0-1)
//     - t = waktu (hari) sejak review terakhir
//     - S = stability (memory strength), dalam praktik proxy dari SM-2 interval
//
//   Insight kunci spaced repetition:
//     - Kartu baru (S=1): cepat dilupakan — hari ke-3 sudah lupa 95%
//     - Kartu learning (S=6): hari ke-14 ingat 10%, hari ke-30 ingat 0.7%
//     - Kartu mastered (S=30): hari ke-30 ingat 37%, hari ke-90 ingat 5%
//
//   SM-2 menjadwalkan review TEPAT sebelum probabilitas turun di bawah ~85%
//   (sweet spot untuk strengthening tanpa over-studying). Inilah mengapa
//   "spaced repetition lebih efektif daripada cramming".
//
// VISUALISASI:
//   3 kurva overlay di Canvas:
//     - 🔴 Light blue (S=1):  proxy kartu baru
//     - 🟡 Medium blue (S=6): proxy kartu learning
//     - 🟢 Deep blue (S=30):  proxy kartu mastered
//
//   X-axis: 0 hingga 30 hari ke depan
//   Y-axis: 0% hingga 100% retention probability
//
//   Plus annotation: "Tanpa review, kartu baru kamu lupa dalam X hari"
// ════════════════════════════════════════════════════════════════════════════

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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(16.dp),
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

            // Canvas chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            ) {
                val chartWidth = size.width - 28.dp.toPx()   // reserve right padding
                val chartHeight = size.height - 24.dp.toPx() // reserve bottom for x-labels
                val originX = 24.dp.toPx()                    // reserve left for y-labels
                val originY = 0f

                // ════════════════════════════════════════════════════════════
                // GRID LINES (4 horizontal: 0%, 25%, 50%, 75%, 100%)
                // ════════════════════════════════════════════════════════════
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

                // ════════════════════════════════════════════════════════════
                // 85% SM-2 SWEET SPOT — dashed horizontal line
                // ════════════════════════════════════════════════════════════
                // SM-2 jadwalkan review sebelum retention drop di bawah ~85%.
                // Visualkan threshold ini biar user paham "kenapa SM-2 work".
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

                // ════════════════════════════════════════════════════════════
                // 3 KURVA forgetting curve
                // Formula: y(t) = exp(-t / S)
                // Sample 60 titik (setiap 0.5 hari) untuk smooth curve
                // ════════════════════════════════════════════════════════════
                val daysRange = 30
                val sampleCount = 60

                // Helper: convert (day, retentionFraction) ke canvas (x, y)
                fun toCanvasPoint(day: Float, retention: Float): Offset {
                    val x = originX + (day / daysRange) * chartWidth
                    val y = originY + (1f - retention) * chartHeight
                    return Offset(x, y)
                }

                // Helper: draw curve untuk stability tertentu
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

            // ════════════════════════════════════════════════════════════════
            // X-AXIS LABELS (Hari 0, 7, 14, 21, 30)
            // ════════════════════════════════════════════════════════════════
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

            // ════════════════════════════════════════════════════════════════
            // LEGEND — 3 colored dots + label
            // ════════════════════════════════════════════════════════════════
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

            // ════════════════════════════════════════════════════════════════
            // INSIGHT FOOTER
            // ════════════════════════════════════════════════════════════════
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