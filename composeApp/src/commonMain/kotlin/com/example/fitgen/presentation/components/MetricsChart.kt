package com.example.fitgen.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun WeightTrackerChart(weights: List<Float>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Grafik Berat Badan",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (weights.size >= 2) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(8.dp)
            ) {
                val maxW = weights.max()
                val minW = weights.min()
                val range = if (maxW - minW == 0f) 1f else maxW - minW
                val stepX = size.width / (weights.size - 1).toFloat()

                for (i in 0 until weights.size - 1) {
                    val x1 = i.toFloat() * stepX
                    val y1 = size.height - ((weights[i] - minW) / range) * size.height
                    val x2 = (i + 1).toFloat() * stepX
                    val y2 = size.height - ((weights[i + 1] - minW) / range) * size.height

                    drawLine(
                        color = Color(0xFF4CAF50),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = Color(0xFF4CAF50),
                        radius = 6f,
                        center = Offset(x1, y1)
                    )
                }
                // Titik terakhir
                val lastX = (weights.size - 1).toFloat() * stepX
                val lastY = size.height - ((weights.last() - minW) / range) * size.height
                drawCircle(
                    color = Color(0xFF4CAF50),
                    radius = 6f,
                    center = Offset(lastX, lastY)
                )
            }
        } else {
            Text(
                text = "Data belum cukup untuk menampilkan grafik",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BmiGauge(bmi: Float) {
    val status = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25.0f -> "Normal"
        bmi < 30.0f -> "Overweight"
        else         -> "Obese"
    }
    val gaugeColor = when (status) {
        "Underweight" -> Color(0xFF2196F3)
        "Normal"      -> Color(0xFF4CAF50)
        "Overweight"  -> Color(0xFFFFC107)
        else          -> Color(0xFFF44336)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "BMI: ${"%.1f".format(bmi)}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Status: $status",
            style = MaterialTheme.typography.bodyMedium,
            color = gaugeColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        ) {
            // Background bar
            drawRoundRect(
                color = Color(0xFFE0E0E0),
                size = size.copy(height = size.height)
            )
            // Progress bar (BMI max dianggap 40)
            val progress = (bmi / 40f).coerceIn(0f, 1f)
            drawRoundRect(
                color = gaugeColor,
                size = size.copy(width = size.width * progress)
            )
        }
    }
}