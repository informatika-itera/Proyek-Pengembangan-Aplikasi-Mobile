package com.example.todomaster.presentation.screens.addtask

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todomaster.domain.model.Quadrant
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is AddTaskViewModel.UiEvent.SaveSuccess) onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Tugas") },
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
                label = { Text("Judul Tugas") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Deskripsi (Opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Pilih Prioritas Matriks Eisenhower:", style = MaterialTheme.typography.labelLarge)

            Quadrant.entries.forEach { q ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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

            Button(
                onClick = { viewModel.onSaveTask() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Tugas")
            }
        }
    }
}