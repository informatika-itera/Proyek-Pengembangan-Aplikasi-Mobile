package com.studyhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studyhub.domain.model.AiUsageStats
import com.studyhub.presentation.theme.Spacing

@Composable
fun AiUsageBadge(
    usageStats: AiUsageStats,
    maxCalls: Int,
    callType: String,
    modifier: Modifier = Modifier
) {
    val used = when (callType) {
        "Priority" -> usageStats.priorityCallsToday
        "Reminder" -> usageStats.reminderCallsToday
        else -> 0
    }
    val remaining = maxCalls - used

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.normal, vertical = Spacing.small),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    Spacing.small
                )
            ) {
                Icon(
                    Icons.Default.AutoAwesome, null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "AI $callType",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(
                "$remaining/$maxCalls tersisa hari ini",
                style = MaterialTheme.typography.labelSmall,
                color = if (remaining <= 2)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
