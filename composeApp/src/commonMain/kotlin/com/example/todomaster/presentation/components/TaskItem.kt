package com.example.todomaster.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.presentation.theme.ColorDelegate
import com.example.todomaster.presentation.theme.ColorDoFirst
import com.example.todomaster.presentation.theme.ColorDontDo
import com.example.todomaster.presentation.theme.ColorSchedule
import kotlinx.datetime.toLocalDateTime

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 500),
        label = "BgColorAnimation"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.5f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "AlphaAnimation"
    )

    val quadrantColor = when (task.priority) {
        Quadrant.DO_FIRST -> ColorDoFirst
        Quadrant.SCHEDULE -> ColorSchedule
        Quadrant.DELEGATE -> ColorDelegate
        Quadrant.DONT_DO -> ColorDontDo
    }

    val isDueToday = task.dueDate?.let {
        val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val due = kotlinx.datetime.Instant.fromEpochMilliseconds(it).toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        today == due
    } ?: false

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onToggleComplete(!task.isCompleted) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) androidx.compose.material.icons.Icons.Default.CheckCircle else androidx.compose.material.icons.Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Status",
                    tint = if (task.isCompleted) quadrantColor else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (isDueToday && !task.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Due hari ini",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(quadrantColor.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                val formattedName = task.priority.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                Text(
                    text = formattedName,
                    color = quadrantColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}