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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.material.icons.filled.Person
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
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.dueDate
    )

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

                AnimatedVisibility(visible = viewModel.title.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (viewModel.isLoadingAi) "AI Sedang Menganalisis..." else "Bantuan AI Task Breakdown",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (viewModel.isLoadingAi) {
                                    "Mengevaluasi tingkat kerumitan tugas '${viewModel.title}'. Mohon tunggu sebentar..."
                                } else {
                                    "Jika tugas ini terlalu kompleks, AI dapat mengevaluasi dan memecahnya menjadi beberapa sub-tasks."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.breakdownTaskWithAI() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !viewModel.isLoadingAi
                            ) {
                                if (viewModel.isLoadingAi) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sedang mengevaluasi...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Evaluasi dengan AI")
                                }
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

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val quadrants = Quadrant.entries
                    for (i in quadrants.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (j in i until minOf(i + 2, quadrants.size)) {
                                val q = quadrants[j]
                                val isSelected = viewModel.priority == q

                                val qColor = when (q) {
                                    Quadrant.DO_FIRST -> com.example.todomaster.presentation.theme.ColorDoFirst
                                    Quadrant.SCHEDULE -> com.example.todomaster.presentation.theme.ColorSchedule
                                    Quadrant.DELEGATE -> com.example.todomaster.presentation.theme.ColorDelegate
                                    Quadrant.DONT_DO -> com.example.todomaster.presentation.theme.ColorDontDo
                                }

                                val icon = when (q.name) {
                                    "DO_FIRST" -> androidx.compose.material.icons.Icons.Default.FlashOn
                                    "SCHEDULE" -> androidx.compose.material.icons.Icons.Default.DateRange
                                    "DELEGATE" -> androidx.compose.material.icons.Icons.Default.Person
                                    else -> androidx.compose.material.icons.Icons.Default.Cancel
                                }

                                val description = when (q.name) {
                                    "DO_FIRST" -> "Penting & mendesak"
                                    "SCHEDULE" -> "Penting, tak mendesak"
                                    "DELEGATE" -> "Mendesak, tak penting"
                                    else -> "Tak penting & tak mendesak"
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) qColor.copy(alpha = 0.1f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) qColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.priority = q }
                                        .padding(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) qColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))

                                            Text(
                                                text = q.name.replace("_", " "),
                                                color = if (isSelected) qColor else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelMedium
                                            )

                                            Spacer(modifier = Modifier.weight(1f))

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                                    contentDescription = "Terpilih",
                                                    tint = qColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = description,
                                            color = if (isSelected) qColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Text("Deadline (opsional):", style = MaterialTheme.typography.labelLarge)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (viewModel.dueDate != null) formatDueDateUI(viewModel.dueDate!!) else "Pilih tenggat waktu...",
                                color = if (viewModel.dueDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    if (viewModel.dueDate != null) {
                        IconButton(
                            onClick = { viewModel.dueDate = null },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                contentDescription = "Hapus Deadline",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.dueDate = datePickerState.selectedDateMillis
                                    showDatePicker = false
                                }
                            ) { Text("Pilih") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
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

fun formatDueDateUI(millis: Long): String {
    val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
    val months = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    return "${dateTime.dayOfMonth} ${months[dateTime.monthNumber - 1]} ${dateTime.year}"
}