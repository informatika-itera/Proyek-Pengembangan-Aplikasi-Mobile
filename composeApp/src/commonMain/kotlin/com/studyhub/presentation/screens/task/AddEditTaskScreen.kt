package com.studyhub.presentation.screens.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.capitalizeFirst
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.theme.Spacing
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: String? = null,
    initialDate: String? = null,
    navController: NavController
) {
    val viewModel: AddEditTaskViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaving by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var status by remember { mutableStateOf(TaskStatus.TODO) }
    var dueDate by remember { mutableStateOf(initialDate?.toLongOrNull() ?: Clock.System.now().toEpochMilliseconds()) }
    var estimatedMinutes by remember { mutableIntStateOf(25) }
    
    var titleError by remember { mutableStateOf<String?>(null) }
    var expandedSubjects by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isDeadlinePast = dueDate > 0 && dueDate < Clock.System.now().toEpochMilliseconds()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddEditTaskUiEvent.SaveSuccess -> navController.popBackStack()
                is AddEditTaskUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(uiState) {
        (uiState as? AddEditTaskUiState.Success)?.existingTask?.let { task ->
            title = task.title
            description = task.description
            selectedSubject = task.subject
            priority = task.priority
            status = task.status
            dueDate = task.dueDate
            estimatedMinutes = task.estimatedMinutes
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Tambah Tugas" else "Edit Tugas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is AddEditTaskUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AddEditTaskUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is AddEditTaskUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.normal),
                        verticalArrangement = Arrangement.spacedBy(Spacing.normal)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { 
                                if (it.length <= 100) title = it
                                titleError = null
                            },
                            label = { Text("Judul *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = titleError != null,
                            supportingText = {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(titleError ?: "")
                                    Text("${title.length}/100")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi (Opsional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Default
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
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedSubjects,
                                onDismissRequest = { expandedSubjects = false }
                            ) {
                                state.subjects.forEach { subject ->
                                    DropdownMenuItem(
                                        text = { Text(subject.name) },
                                        onClick = {
                                            selectedSubject = subject.name
                                            expandedSubjects = false
                                        }
                                    )
                                }
                            }
                        }

                        Text("Prioritas", style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            Priority.entries.forEach { p ->
                                FilterChip(
                                    selected = priority == p,
                                    onClick = { priority = p },
                                    label = { Text(p.name.capitalizeFirst()) }
                                )
                            }
                        }

                        Text("Status", style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            TaskStatus.entries.forEach { s ->
                                FilterChip(
                                    selected = status == s,
                                    onClick = { status = s },
                                    label = { Text(s.value.replace("_", " ").capitalizeFirst()) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = Instant.fromEpochMilliseconds(dueDate)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tanggal Deadline") },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.CalendarToday, "Pilih tanggal")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                if (isDeadlinePast) {
                                    Text("⚠ Deadline sudah lewat", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )

                        Text("Estimasi Waktu: $estimatedMinutes menit", style = MaterialTheme.typography.titleSmall)
                        Slider(
                            value = estimatedMinutes.toFloat(),
                            onValueChange = { estimatedMinutes = it.toInt() },
                            valueRange = 5f..240f,
                            steps = 22
                        )

                        Spacer(Modifier.height(Spacing.large))

                        Button(
                            onClick = {
                                if (title.isBlank()) {
                                    titleError = "Judul tidak boleh kosong"
                                } else {
                                    viewModel.saveTask(
                                        taskId = taskId,
                                        title = title,
                                        description = description,
                                        subject = selectedSubject,
                                        priority = priority,
                                        status = status,
                                        dueDate = dueDate,
                                        estimatedMinutes = estimatedMinutes
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = title.isNotBlank() && !isSaving,
                            shape = RoundedCornerShape(Spacing.small)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text(if (taskId == null) "Tambah Tugas" else "Simpan Perubahan")
                            }
                        }
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
}
