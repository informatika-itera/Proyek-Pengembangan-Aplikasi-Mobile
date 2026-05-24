package com.example.todomaster.presentation.screens.addtask

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todomaster.domain.model.Quadrant
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = koinInject()
) {
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is AddTaskViewModel.UiEvent.SaveSuccess) onNavigateBack()
        }
    }

    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTaskForEdit(taskId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Tambah Tugas" else "Edit Tugas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Judul Tugas Kuliah") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.breakdownTaskWithAI() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                enabled = !viewModel.isLoadingAi && viewModel.title.isNotBlank()
            ) {
                if (viewModel.isLoadingAi) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Sedang Memecah Tugas...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Icon", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Uraikan Sub-Task dengan AI (Gemini)")
                }
            }

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Deskripsi / Sub-Task Catatan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Pilih Prioritas Matriks Eisenhower:", style = MaterialTheme.typography.labelLarge)

            Quadrant.entries.forEach { q ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (viewModel.priority == q),
                        onClick = { viewModel.priority = q }
                    )
                    Text(text = q.name.replace("_", " "))
                }
            }

            if (viewModel.error != null) {
                Text(text = viewModel.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onSaveTask() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (taskId == null) "Simpan Tugas" else "Perbarui Tugas")
            }
        }
    }

    if (viewModel.showAiDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAiDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // ✅ YANG BENAR
                    Icon(Icons.Default.AutoAwesome, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hasil Uraian AI Gemini")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Gemini merekomendasikan pecahan langkah konkret berikut untuk tugasmu:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.generatedSubTasks.forEachIndexed { index, subTask ->
                                Text(
                                    text = "${index + 1}. ${subTask.title} (${subTask.estimatedMinutes} Menit)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.acceptAiSubTasks() }) {
                    Text("Masukkan ke Deskripsi")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAiDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}