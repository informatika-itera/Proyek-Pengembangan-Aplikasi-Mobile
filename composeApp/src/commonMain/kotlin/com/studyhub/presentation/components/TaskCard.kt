package com.studyhub.presentation.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.theme.*
import kotlinx.datetime.*

@Composable
fun TaskCard(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = task.status == TaskStatus.DONE
    val now = Clock.System.now().toEpochMilliseconds()
    val isOverdue = !isDone && task.dueDate < now
    
    val statusIcon = when {
        isDone -> Icons.Default.CheckCircle
        isOverdue -> Icons.Default.ErrorOutline
        task.status == TaskStatus.IN_PROGRESS -> Icons.Default.Schedule
        else -> Icons.Default.Schedule 
    }
    val statusColor = when {
        isDone -> Color(0xFF10B981)
        isOverdue -> Color(0xFFE24B4A)
        task.status == TaskStatus.IN_PROGRESS -> Color(0xFF3B82F6)
        else -> Color(0xFFBBBBBB)
    }
    
    val subjectAccentColor = when {
        isOverdue -> Color(0xFFE24B4A)
        task.subject.lowercase() == "mathematics" || task.subject.lowercase() == "calculus" -> Color(0xFF3B82F6)
        task.subject.lowercase() == "chemistry" || task.subject.lowercase() == "code" -> Color(0xFF10B981)
        task.subject.lowercase() == "physics" -> Color(0xFF0EA5E9)
        else -> GoldenSuedeDark
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) Color(0xFFFFF5F5) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isOverdue) 1.5.dp else 1.dp,
            color = if (isOverdue) Color(0xFFFCA5A5) else Color(0xFFE8E0D4)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Accent Strip
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(subjectAccentColor)
            )
            
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (isDone || isOverdue) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        ),
                        color = when {
                            isDone -> MutedText
                            isOverdue -> Color(0xFFB91C1C)
                            else -> DarkText
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (subjectBg, subjectText) = if (isOverdue) {
                            Color(0xFFFEE2E2) to Color(0xFF991B1B)
                        } else {
                            Color(0xFFF1EBE0) to Color(0xFF7A5C2E)
                        }
                        TaskTag(task.subject, subjectBg, subjectText)
                        
                        val (priorityBg, priorityText) = when {
                            isOverdue -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
                            task.priority == Priority.HIGH -> Color(0xFFFEECEC) to Color(0xFFA32D2D)
                            task.priority == Priority.MEDIUM -> Color(0xFFFAEEDA) to Color(0xFF854F0B)
                            else -> Color(0xFFE1F5EE) to Color(0xFF1B5E20)
                        }
                        TaskTag(task.priority.name.lowercase(), priorityBg, priorityText)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val deadline = formatDeadline(task.dueDate)
                    Text(
                        deadline,
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            deadline == "Overdue" -> Color(0xFFE24B4A)
                            deadline == "Tomorrow" -> Color(0xFFE85D35)
                            else -> Color(0xFF888888)
                        },
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "~${task.estimatedMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF888888)
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, null, tint = Color(0xFFBBBBBB), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFE24B4A), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskTag(text: String, containerColor: Color, textColor: Color) {
    Surface(
        color = containerColor,
        shape = CircleShape
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = textColor,
            fontWeight = FontWeight.Medium
        )
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
    val now = Clock.System.now().toEpochMilliseconds()
    val isOverdue = !isDone && task.dueDate < now

    val statusIcon = when {
        isDone -> Icons.Default.CheckCircle
        isOverdue -> Icons.Default.ErrorOutline
        task.status == TaskStatus.IN_PROGRESS -> Icons.Default.Schedule
        else -> Icons.Default.Schedule
    }
    val statusColor = when {
        isDone -> Color(0xFF10B981)
        isOverdue -> Color(0xFFE24B4A)
        task.status == TaskStatus.IN_PROGRESS -> Color(0xFF3B82F6)
        else -> Color(0xFFBBBBBB)
    }
    
    val subjectAccentColor = when {
        isOverdue -> Color(0xFFE24B4A)
        task.subject.lowercase() == "mathematics" || task.subject.lowercase() == "calculus" -> Color(0xFF3B82F6)
        task.subject.lowercase() == "chemistry" || task.subject.lowercase() == "code" -> Color(0xFF10B981)
        task.subject.lowercase() == "physics" -> Color(0xFF0EA5E9)
        else -> GoldenSuedeDark
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) Color(0xFFFFF5F5) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isOverdue) 1.5.dp else 1.dp,
            color = if (isOverdue) Color(0xFFFCA5A5) else Color(0xFFE8E0D4)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(subjectAccentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        task.subject.take(1).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        color = subjectAccentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isDone || isOverdue) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                ),
                color = when {
                    isDone -> MutedText
                    isOverdue -> Color(0xFFB91C1C)
                    else -> DarkText
                },
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
                    color = when {
                        deadline == "Overdue" -> Color(0xFFE24B4A)
                        deadline == "Tomorrow" -> Color(0xFFE85D35)
                        else -> Color(0xFF888888)
                    }
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, null, tint = Color(0xFFBBBBBB), modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFE24B4A), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

private fun getPriorityColor(priority: Priority): Color = when (priority) {
    Priority.HIGH -> PriorityHigh
    Priority.MEDIUM -> PriorityMedium
    Priority.LOW -> PriorityLow
}

fun formatDeadline(epochMillis: Long): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val diff = epochMillis - now
    val days = (diff / 86400000L).toInt()
    
    return when {
        days == 0 -> "Today"
        days == 1 -> "Tomorrow"
        days > 1 -> "${days}d left"
        else -> "Overdue"
    }
}
