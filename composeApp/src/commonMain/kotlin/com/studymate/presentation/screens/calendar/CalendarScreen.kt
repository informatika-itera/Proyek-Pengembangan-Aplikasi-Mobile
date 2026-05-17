package com.studymate.presentation.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("📅 Kalender") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Kalender & Planner", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Catat jadwal ujian dan deadline tugasmu di sini.")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Coming in Sprint 3 🚀", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
