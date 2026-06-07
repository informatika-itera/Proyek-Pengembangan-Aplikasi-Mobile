package com.example.todomaster.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todomaster.core.util.formatToDisplay
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip

data class DeadlineVisuals(
    val badgeText: String,
    val containerColor: Color,
    val contentColor: Color,
    val titleText: String
)

fun getDeadlineVisuals(dueDateMillis: Long): DeadlineVisuals {
    val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
    val due = kotlinx.datetime.Instant.fromEpochMilliseconds(dueDateMillis).toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
    val daysUntil = due.toEpochDays() - today.toEpochDays()

    return when {
        daysUntil < 0 -> DeadlineVisuals("Lewat", Color(0xFFF5F5F5), Color(0xFF616161), "Tugas sudah lewat tenggat!")
        daysUntil == 0 -> DeadlineVisuals("H-0", Color(0xFFFFEBEE), Color(0xFFC62828), "Deadline hari ini!")
        daysUntil == 1 -> DeadlineVisuals("H-1", Color(0xFFFFF3E0), Color(0xFFEF6C00), "Deadline besok!")
        daysUntil <= 3 -> DeadlineVisuals("H-3", Color(0xFFFFF8E1), Color(0xFFF9A825), "Mendekati deadline")
        else -> DeadlineVisuals("H-7+", Color(0xFFE8F5E9), Color(0xFF2E7D32), "Waktu masih panjang")
    }
}

fun formatUIString(millis: Long): String {
    val dateTime = kotlinx.datetime.Instant.fromEpochMilliseconds(millis).toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    val months = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    return "${dateTime.dayOfMonth} ${months[dateTime.monthNumber - 1]} ${dateTime.year}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onShare: (String) -> Unit,
    viewModel: TaskDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TaskDetailEvent.TaskDeleted -> onNavigateBack()
                is TaskDetailEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteTask()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Tugas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    val currentState = uiState
                    if (currentState is TaskDetailUiState.Success) {

                        IconButton(onClick = {
                            viewModel.getShareContent()?.let { onShare(it) }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Bagikan")
                        }

                        IconButton(onClick = onNavigateToEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is TaskDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is TaskDetailUiState.Success -> {

                val qColor = when (state.task.priority.name) {
                    "DO_FIRST" -> Color(0xFF195FA7)
                    "SCHEDULE" -> Color(0xFF1E7B5B)
                    "DELEGATE" -> Color(0xFF9C5B12)
                    else -> Color(0xFF6D6A65)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(qColor.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.task.priority.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                color = qColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Dibuat ${formatUIString(state.task.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = state.task.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 32.sp
                    )

                    state.task.dueDate?.let { dueMillis ->
                        val visuals = getDeadlineVisuals(dueMillis)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = visuals.containerColor),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, visuals.contentColor.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(visuals.contentColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Alarm, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = visuals.titleText,
                                        fontWeight = FontWeight.Bold,
                                        color = visuals.contentColor,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = formatUIString(dueMillis),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = visuals.contentColor.copy(alpha = 0.9f)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(visuals.contentColor)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = visuals.badgeText,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text(
                            text = "DESKRIPSI",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = if (state.task.description.isNullOrBlank()) "Tidak ada catatan tambahan." else state.task.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.toggleTaskCompletion()
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE8F5E9),
                                contentColor = Color(0xFF2E7D32)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (state.task.isCompleted) "Batal Selesai" else "Selesai", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFEBEE),
                                contentColor = Color(0xFFC62828)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hapus", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            is TaskDetailUiState.NotFound -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tugas Tidak Ditemukan", style = MaterialTheme.typography.titleMedium)
                        Text("Tugas mungkin sudah dihapus", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Tugas") },
        text = { Text("Apakah Anda yakin ingin menghapus tugas ini? Tindakan ini tidak dapat dibatalkan.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}