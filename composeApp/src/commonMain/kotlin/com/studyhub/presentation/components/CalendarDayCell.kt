package com.studyhub.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.core.util.TaskColor

@Composable
fun CalendarDayCell(
    dayOfMonth: Int,
    isSelected: Boolean,
    isToday: Boolean,
    taskColors: List<String>,
    onSelect: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isToday -> MaterialTheme.colorScheme.primary
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "day_bg"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isToday -> MaterialTheme.colorScheme.onPrimary
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(200),
        label = "day_text"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                ),
                color = textColor
            )
            
            if (taskColors.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    taskColors.take(3).forEach { hex ->
                        val dotColor = if (isToday || isSelected) {
                            Color.White.copy(alpha = 0.9f)
                        } else {
                            TaskColor.fromHex(hex)
                        }
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }
        }
    }
}
