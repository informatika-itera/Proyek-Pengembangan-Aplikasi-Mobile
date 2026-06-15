package com.studyhub.presentation.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.capitalizeFirst
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus
import com.studyhub.core.util.formatTimeOnly
import com.studyhub.core.util.formatToDisplay
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
import com.studyhub.presentation.screens.ai.SmartReminderUiState
import com.studyhub.presentation.screens.ai.SmartReminderViewModel
import com.studyhub.presentation.theme.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    navController: NavController,
    viewModel: TaskDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var showSmartReminderSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is TaskDetailEvent.TaskDeleted -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tugas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditBottomSheet = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        val state = uiState
        when (state) {
            is TaskDetailUiState.Loading -> LoadingView(Modifier.padding(paddingValues))
            is TaskDetailUiState.Error -> ErrorView(
                message = state.message,
                onRetry = { viewModel.loadTask(taskId) },
                modifier = Modifier.padding(paddingValues)
            )
            is TaskDetailUiState.Success -> {
                val task = state.task
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(Spacing.large)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacing.large)
                ) {
                    // Title & Status Section
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = task.displaySubject,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Surface(
                                color = when(task.priority) {
                                    Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                    Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                                    Priority.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                                },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = task.priority.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = when(task.priority) {
                                        Priority.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                        Priority.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                                        Priority.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
                                    }
                                )
                            }
                        }
                        
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (task.status == TaskStatus.DONE) TextDecoration.LineThrough else null,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Status Badge
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.normal)) {
                        Icon(
                            imageVector = when(task.status) {
                                TaskStatus.TODO -> Icons.Default.Description
                                TaskStatus.IN_PROGRESS -> Icons.Default.HourglassEmpty
                                TaskStatus.DONE -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = task.status.value.replace("_", " ").capitalizeFirst(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // AI Smart Reminder Button
                    Button(
                        onClick = { showSmartReminderSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.AutoAwesome, "Smart Reminder AI", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Spacing.small))
                        Text("Smart Reminder")
                    }

                    // Info Section (Date & Time)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(Spacing.normal).fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                                    Text("Batas Waktu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                                        Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(formatDate(task.dueDate), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                                    Text("Jam Deadline", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                                        Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            task.dueTime ?: Instant.fromEpochMilliseconds(task.dueDate).formatTimeOnly(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Tampilkan Waktu Pengumpulan jika status DONE
                            if (task.status == TaskStatus.DONE) {
                                Spacer(modifier = Modifier.height(Spacing.normal))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(Spacing.normal))
                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                                    Text("Waktu Pengumpulan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
                                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        
                                        // Gunakan completedAt jika ada, jika tidak gunakan updatedAt sebagai fallback
                                        val displayTime = task.completedAt ?: task.updatedAt
                                        Text(
                                            Instant.fromEpochMilliseconds(displayTime).formatToDisplay(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Description Section
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        Text(
                            text = "Deskripsi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = task.description.ifBlank { "Tidak ada deskripsi." },
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (task.description.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }

    if (showEditBottomSheet) {
        AddEditTaskBottomSheet(
            taskId = taskId,
            onDismiss = { showEditBottomSheet = false },
            onSuccess = { showEditBottomSheet = false }
        )
    }

    if (showSmartReminderSheet) {
        SmartReminderBottomSheet(
            taskId = taskId,
            onDismiss = { showSmartReminderSheet = false },
            onSetReminder = { /* TODO: Implement persistent reminder storage if needed */ }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Tugas") },
            text = { Text("Apakah Anda yakin ingin menghapus tugas ini? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(taskId)
                    showDeleteConfirm = false
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartReminderBottomSheet(
    taskId: String,
    onDismiss: () -> Unit,
    onSetReminder: (Long) -> Unit
) {
    val viewModel: SmartReminderViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(taskId) {
        viewModel.loadReminderForTask(taskId)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = ShapeBottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    Spacing.small
                )
            ) {
                Icon(
                    Icons.Default.AutoAwesome, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Smart Reminder",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.height(Spacing.large))

            when (val state = uiState) {
                is SmartReminderUiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(Spacing.small))
                    Text(
                        "AI menganalisis pola belajarmu...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is SmartReminderUiState.Success -> {
                    val formattedTime = formatReminderTime(
                        state.schedule.suggestedReminderTime
                    )
                    Text(
                        "Disarankan reminder pada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        formattedTime,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(Spacing.small))
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "\"${state.schedule.adaptiveReason}\"",
                            modifier = Modifier.padding(Spacing.medium),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(Spacing.large))
                    Button(
                        onClick = {
                            onSetReminder(
                                state.schedule.suggestedReminderTime
                            )
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(
                            Icons.Default.Alarm, null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(Spacing.small))
                        Text("Set Reminder")
                    }
                }

                is SmartReminderUiState.QuotaExceeded,
                is SmartReminderUiState.Error -> {
                    Icon(
                        Icons.Default.WifiOff, null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(Spacing.small))
                    Text(
                        "AI tidak tersedia — menggunakan default",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                else -> {}
            }

            Spacer(Modifier.height(Spacing.normal))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tutup")
            }
            Spacer(Modifier.height(Spacing.large))
        }
    }
}

private val ShapeBottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

private fun formatDate(epochMillis: Long): String {
    val date = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date.dayOfMonth} ${date.month.name.lowercase().capitalizeFirst()} ${date.year}"
}

private fun formatReminderTime(epochMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dateTime.month.name.lowercase().capitalizeFirst()
    return "${dateTime.dayOfMonth} $month ${dateTime.year}, ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}
