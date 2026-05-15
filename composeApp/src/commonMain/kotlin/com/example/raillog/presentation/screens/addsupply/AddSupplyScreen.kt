package com.example.raillog.presentation.screens.addsupply

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AddNoteScreen(
    noteId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToAI: (String) -> Unit
) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("Add Item - Sprint 2")
    }
}