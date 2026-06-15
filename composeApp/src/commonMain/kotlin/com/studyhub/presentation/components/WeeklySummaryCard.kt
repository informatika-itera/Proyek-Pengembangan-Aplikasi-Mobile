package com.studyhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.studyhub.presentation.theme.Spacing

@Composable
fun WeeklySummaryCard(
    completedThisWeek: Int,
    totalThisWeek: Int,
    completionRate: Float,
    focusMinutesToday: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.large)) {
            Text(
                "Minggu Ini",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(Spacing.normal))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "$completedThisWeek/$totalThisWeek",
                    label = "Tugas selesai",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    value = "${(completionRate * 100).toInt()}%",
                    label = "Completion rate",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    value = "${focusMinutesToday}m",
                    label = "Fokus hari ini",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(Spacing.normal))
            LinearProgressIndicator(
                progress = { completionRate },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary
                    .copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
