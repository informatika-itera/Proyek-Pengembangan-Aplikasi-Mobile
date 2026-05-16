package com.example.todomaster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val quadrantColor = when (task.priority) {
        Quadrant.DO_FIRST -> ColorDoFirst
        Quadrant.SCHEDULE -> ColorSchedule
        Quadrant.DELEGATE -> ColorDelegate
        Quadrant.DONT_DO -> ColorDontDo
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(quadrantColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}