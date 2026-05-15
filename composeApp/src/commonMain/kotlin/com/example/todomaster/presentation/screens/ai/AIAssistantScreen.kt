package com.example.todomaster.presentation.screens.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AIAssistantScreen(
    taskId: Long?,
    initialText: String?,
    onNavigateBack: () -> Unit,
    onApplyResult: ((String) -> Unit)? = null,
    // sengaja dibiarkan kosong
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Layar AI Assistant - Akan dibangun di Sprint 4")
    }
}