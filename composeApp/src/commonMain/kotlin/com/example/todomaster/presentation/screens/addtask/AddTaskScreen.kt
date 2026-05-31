package com.example.todomaster.presentation.screens.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Judul Tugas Induk") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: Buat aplikasi mobile tugas besar") }
            )

            if (viewModel.generatedSubTasks.isEmpty()) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tugas ini terdeteksi kompleks", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "AI dapat memecah menjadi sub-tasks dan mengklasifikasikan ke kuadran.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.breakdownTaskWithAI() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !viewModel.isLoadingAi && viewModel.title.isNotBlank()
                        ) {
                            if (viewModel.isLoadingAi) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sedang memproses...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Breakdown dengan AI")
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    label = { Text("Deskripsi Catatan (Opsional)") },
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

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.onSaveTask() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (taskId == null) "Simpan Tugas Manual" else "Perbarui Tugas")
                }

            } else {
                val selectedCount = viewModel.generatedSubTasks.count { it.isSelected }
                val totalCount = viewModel.generatedSubTasks.size

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sub-tasks dari AI", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("$selectedCount / $totalCount dipilih", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }

                viewModel.generatedSubTasks.forEachIndexed { index, selectableTask ->
                    val sub = selectableTask.response

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, if (selectableTask.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .background(if (selectableTask.isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { viewModel.toggleSubTaskSelection(index, !selectableTask.isSelected) }
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectableTask.isSelected,
                            onCheckedChange = { isChecked -> viewModel.toggleSubTaskSelection(index, isChecked) }
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sub.title,
                                fontWeight = if (selectableTask.isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                textDecoration = if (selectableTask.isSelected) {
                                    androidx.compose.ui.text.style.TextDecoration.None
                                } else {
                                    androidx.compose.ui.text.style.TextDecoration.LineThrough
                                },
                                color = if (selectableTask.isSelected) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                }
                            )
                            Text(
                                text = "Estimasi: ${sub.estimatedMinutes} menit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = sub.recommended_quadrant.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.onSaveTask() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = selectedCount > 0
                ) {
                    Text("Tambahkan $selectedCount ke Matriks", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (viewModel.error != null) {
                Text(text = viewModel.error!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}