package com.studymate.presentation.screens.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("🧠 Belajar") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📚", style = MaterialTheme.typography.displayLarge)
                Text("AI Quiz", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Fitur ini akan tersedia setelah kamu punya catatan yang sudah di-refine oleh AI.")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Coming in Sprint 3 🚀", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
