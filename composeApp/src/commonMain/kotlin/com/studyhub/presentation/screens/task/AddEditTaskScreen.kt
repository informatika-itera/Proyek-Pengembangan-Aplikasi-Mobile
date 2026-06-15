package com.studyhub.presentation.screens.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus
import com.studyhub.core.util.combineDateAndTime
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
import com.studyhub.presentation.theme.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    date: String? = null,
    taskId: String? = null,
    navController: NavController
) {
    val viewModel: AddEditTaskViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Umum") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var status by remember { mutableStateOf(TaskStatus.TODO) }
    var dueDate by remember { 
        mutableStateOf(date?.toLongOrNull() ?: Clock.System.now().toEpochMilliseconds()) 
    }
    var dueTime by remember { mutableStateOf<String?>(null) }
    var estimatedMinutes by remember { mutableStateOf(30) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedSubjects by remember { mutableStateOf(false) }

    val actualDeadline = remember(dueDate, dueTime) {
        combineDateAndTime(dueDate, dueTime)
    }
    val isDeadlinePast = actualDeadline < Clock.System.now().toEpochMilliseconds()

    LaunchedEffect(taskId) {
        viewModel.loadSubjects()
        if (taskId != null) {
            viewModel.loadTask(taskId)
        }
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is AddEditTaskUiState.Success) {
            state.existingTask?.let { task ->
                title = task.title
                description = task.description
                selectedSubject = task.subject
                priority = task.priority
                status = task.status
                dueDate = task.dueDate
                dueTime = task.dueTime
                estimatedMinutes = task.estimatedMinutes
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddEditTaskEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is AddEditTaskEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (taskId == null) "Tambah Tugas" else "Edit Tugas",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val state = uiState
        
        when (state) {
            is AddEditTaskUiState.Loading -> LoadingView(Modifier.padding(padding))
            is AddEditTaskUiState.Error -> ErrorView(
                message = state.message,
                onRetry = { 
                    if (taskId != null) viewModel.loadTask(taskId) 
                    else viewModel.loadSubjects() 
                },
                modifier = Modifier.padding(padding)
            )
            else -> {
                val subjects = if (state is AddEditTaskUiState.Success) state.subjects else emptyList()
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.normal),
                    verticalArrangement = Arrangement.spacedBy(Spacing.normal)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { 
                            if (it.length <= 100) title = it
                            titleError = if (it.isBlank()) "Judul tidak boleh kosong" else null
                        },
                        label = { Text("Judul Tugas *") },
                        isError = titleError != null,
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(titleError ?: "")
                                Text(
                                    "${title.length}/100",
                                    color = if (title.length > 80)
                                        MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi (Opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedSubjects,
                        onExpandedChange = { expandedSubjects = !expandedSubjects }
                    ) {
                        OutlinedTextField(
                            value = selectedSubject,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Mata Kuliah") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubjects) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSubjects,
                            onDismissRequest = { expandedSubjects = false }
                        ) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject.name) },
                                    onClick = {
                                        selectedSubject = subject.name
                                        expandedSubjects = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Text(
                        "Prioritas",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        Priority.entries.forEach { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p.name) }
                            )
                        }
                    }

                    Text(
                        "Status",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        TaskStatus.entries.forEach { s ->
                            FilterChip(
                                selected = status == s,
                                onClick = { status = s },
                                label = { Text(s.value.replace("_", " ").replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.normal)
                    ) {
                        OutlinedTextField(
                            value = Instant.fromEpochMilliseconds(dueDate)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tanggal Deadline") },
                            isError = isDeadlinePast,
                            supportingText = {
                                if (isDeadlinePast) {
                                    Text(
                                        "⚠ Deadline sudah lewat",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = dueTime ?: "--:--",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jam") },
                            trailingIcon = {
                                IconButton(onClick = { showTimePicker = true }) {
                                    Icon(Icons.Default.Schedule, contentDescription = "Pilih Jam")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        "Estimasi Waktu: $estimatedMinutes menit",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = estimatedMinutes.toFloat(),
                        onValueChange = { estimatedMinutes = it.toInt() },
                        valueRange = 0f..240f,
                        steps = 23,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    Spacer(Modifier.height(Spacing.large))

                    Button(
                        onClick = {
                            viewModel.saveTask(
                                taskId = taskId,
                                title = title,
                                description = description,
                                subject = selectedSubject,
                                priority = priority,
                                status = status,
                                dueDate = actualDeadline,
                                dueTime = dueTime,
                                estimatedMinutes = estimatedMinutes
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = title.isNotBlank() && (!isDeadlinePast || taskId != null),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            if (taskId == null) "Tambah Tugas" else "Simpan Perubahan",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dueDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val initialHour = dueTime?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 12
        val initialMinute = dueTime?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueTime = "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Batal") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
