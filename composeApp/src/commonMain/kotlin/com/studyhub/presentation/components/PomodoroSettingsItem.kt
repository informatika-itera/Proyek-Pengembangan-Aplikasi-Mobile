package com.studyhub.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PomodoroSettingsItem(
    focusDuration: Int,
    shortBreak: Int,
    longBreak: Int,
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text("Pomodoro", style = MaterialTheme.typography.titleSmall) },
        supportingContent = { 
            Text(
                "Fokus: ${focusDuration}m • Istirahat: ${shortBreak}m / ${longBreak}m",
                style = MaterialTheme.typography.bodySmall
            ) 
        },
        leadingContent = { Icon(Icons.Default.Timer, contentDescription = null) },
        trailingContent = {
            IconButton(onClick = onNavigate) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Edit Pomodoro")
            }
        }
    )
}
