package com.studyhub.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.core.util.TaskColor
import com.studyhub.core.util.toTaskColor
import com.studyhub.presentation.theme.*
import kotlinx.datetime.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskCard(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDone = task.status == TaskStatus.DONE
    val now = com.studyhub.core.util.currentTimeMillis()
    val isOverdue = !isDone && task.dueDate < now
    
    val daysLeft = (task.dueDate - now) / 86_400_000
    val hoursLeft = (task.dueDate - now) / 3_600_000

    val taskColor = task.colorHex.toTaskColor()
    val taskContainerColor = TaskColor.containerColorFor(
        task.colorHex, isDark
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDone) 0.dp else 2.dp
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(Spacing.normal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                Spacing.small
            )
        ) {
            // ── Left color bar (task color) ──
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDone) MaterialTheme.colorScheme.outline
                        else taskColor
                    )
            )

            // ── Status icon with task color container ──
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (isDone)
                            MaterialTheme.colorScheme.surfaceVariant
                        else taskContainerColor
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isDone -> Icons.Default.CheckCircle
                        isOverdue -> Icons.Default.Warning
                        task.status == TaskStatus.IN_PROGRESS -> Icons.Default.Schedule
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = task.status.name,
                    tint = when {
                        isDone -> MaterialTheme.colorScheme.onSurfaceVariant
                        isOverdue -> MaterialTheme.colorScheme.error
                        else -> taskColor
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            // ── Content ──
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (isDone)
                        TextDecoration.LineThrough
                    else TextDecoration.None,
                    color = if (isDone)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Subject chip
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            task.displaySubject,
                            modifier = Modifier.padding(
                                horizontal = 6.dp, vertical = 2.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Priority chip — uses task color tint
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = if (isDark)
                            taskColor.copy(alpha = 0.2f)
                        else taskColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            task.priority.name.lowercase(),
                            modifier = Modifier.padding(
                                horizontal = 6.dp, vertical = 2.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = taskColor
                        )
                    }
                }
            }

            // ── Right section: deadline + actions ──
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Deadline text
                Text(
                    when {
                        isDone -> "Done ✓"
                        isOverdue -> "Overdue"
                        daysLeft < 1 -> "${hoursLeft}h left"
                        daysLeft < 2 -> "Tomorrow"
                        else -> "${daysLeft}d left"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        isDone -> MaterialTheme.colorScheme.onSurfaceVariant
                        isOverdue || daysLeft < 1 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                // Time
                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(task.dueDate)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // ── "Terlambat" badge — subtle, not aggressive ──
                if (isOverdue) {
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            "Terlambat",
                            modifier = Modifier.padding(
                                horizontal = 6.dp, vertical = 2.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                // Edit button
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskGridCard(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = task.status == TaskStatus.DONE
    val now = com.studyhub.core.util.currentTimeMillis()
    val isOverdue = !isDone && task.dueDate < now
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val taskColor = task.colorHex.toTaskColor()
    val taskContainerColor = TaskColor.containerColorFor(task.colorHex, isDark)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(taskContainerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        task.subject.take(1).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        color = taskColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    imageVector = when {
                        isDone -> Icons.Default.CheckCircle
                        isOverdue -> Icons.Default.Warning
                        else -> Icons.Default.Schedule
                    },
                    contentDescription = "Status",
                    tint = if (isOverdue) MaterialTheme.colorScheme.error else taskColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isDone) TextDecoration.LineThrough else null
                ),
                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.defaultMinSize(minHeight = 36.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val deadline = formatDeadline(task.dueDate)
                Text(
                    deadline,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatDeadline(epochMillis: Long): String {
    val now = com.studyhub.core.util.currentTimeMillis()
    val diff = epochMillis - now
    val days = (diff / 86400000L).toInt()
    
    return when {
        days == 0 -> "Today"
        days == 1 -> "Tomorrow"
        days > 1 -> "${days}d left"
        else -> "Overdue"
    }
}
