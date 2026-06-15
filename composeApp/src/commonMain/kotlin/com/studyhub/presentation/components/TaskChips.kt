package com.studyhub.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.presentation.theme.Spacing

@Composable
fun DueDateChip(dueDate: Long) {
    val deadline = formatDeadline(dueDate)
    Surface(
        color = when {
            deadline == "Overdue" -> MaterialTheme.colorScheme.errorContainer
            deadline == "Tomorrow" -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.small, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
        ) {
            Icon(
                Icons.Default.CalendarToday, contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = when {
                    deadline == "Overdue" -> MaterialTheme.colorScheme.error
                    deadline == "Tomorrow" -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                deadline,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.Bold,
                color = when {
                    deadline == "Overdue" -> MaterialTheme.colorScheme.onErrorContainer
                    deadline == "Tomorrow" -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
