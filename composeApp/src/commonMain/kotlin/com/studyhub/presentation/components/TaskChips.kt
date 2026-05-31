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
            deadline == "Overdue" -> Color(0xFFFEE2E2)
            deadline == "Tomorrow" -> Color(0xFFFFF7ED)
            else -> Color(0xFFF1EBE0)
        },
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.CalendarToday, null,
                modifier = Modifier.size(10.dp),
                tint = when {
                    deadline == "Overdue" -> Color(0xFFB91C1C)
                    deadline == "Tomorrow" -> Color(0xFFEA580C)
                    else -> Color(0xFF8B7355)
                }
            )
            Text(
                deadline,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.Bold,
                color = when {
                    deadline == "Overdue" -> Color(0xFFB91C1C)
                    deadline == "Tomorrow" -> Color(0xFFEA580C)
                    else -> Color(0xFF8B7355)
                }
            )
        }
    }
}
