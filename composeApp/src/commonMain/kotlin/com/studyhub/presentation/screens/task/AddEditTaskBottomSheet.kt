package com.studyhub.presentation.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus
import com.studyhub.core.util.toLocalMillisFromUtc
import com.studyhub.core.util.combineDateAndTime
import com.studyhub.presentation.theme.*
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskBottomSheet(
    taskId: String? = null,
    initialDate: Long? = null,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val viewModel: AddEditTaskViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Mathematics") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var status by remember { mutableStateOf(TaskStatus.TODO) }
    var dueDate by remember { mutableStateOf(initialDate ?: Clock.System.now().toEpochMilliseconds()) }
    var dueTime by remember { mutableStateOf<String?>(null) }
    var estimatedMinutes by remember { mutableStateOf(60) }
    var isInitialized by remember { mutableStateOf(false) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }

    val actualDeadline = remember(dueDate, dueTime) {
        combineDateAndTime(dueDate, dueTime)
    }
    val isDeadlinePast = actualDeadline < Clock.System.now().toEpochMilliseconds()

    LaunchedEffect(taskId) {
        isInitialized = false
        viewModel.resetState() 
        viewModel.loadSubjects()
        if (taskId != null) {
            viewModel.loadTask(taskId)
        } else {
            title = ""
            description = ""
            selectedSubject = "Mathematics"
            priority = Priority.MEDIUM
            status = TaskStatus.TODO
            dueDate = initialDate ?: Clock.System.now().toEpochMilliseconds()
            dueTime = null
            estimatedMinutes = 60
            isInitialized = true
        }
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (!isInitialized && state is AddEditTaskUiState.Success) {
            state.existingTask?.let { task ->
                title = task.title
                description = task.description
                selectedSubject = task.subject
                priority = task.priority
                status = task.status
                dueDate = task.dueDate
                dueTime = task.dueTime
                estimatedMinutes = task.estimatedMinutes
                isInitialized = true
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
                    onSuccess()
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        val state = uiState
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.large)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (taskId == null) "New Task" else "Edit Task",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Add a new study task",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Close, "Tutup", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(Modifier.height(Spacing.large))

                // Task Title
                Text(
                    "Task Title *", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(Spacing.small))
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        if (it.length <= 100) title = it
                        titleError = if (it.isBlank()) "Title is required" else null
                    },
                    placeholder = { Text("e.g. Complete Math Assignment...", color = MaterialTheme.colorScheme.outline) },
                    isError = titleError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(titleError ?: "")
                            Text("${title.length}/100")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(Spacing.normal))

                // Subject
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Subject", 
                        style = MaterialTheme.typography.titleSmall, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { showAddSubjectDialog = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Add, "Tambah Mata Kuliah", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(Spacing.small))
                
                val subjects = if (state is AddEditTaskUiState.Success) state.subjects.map { it.name } else emptyList()
                val allSubjects = remember(subjects) {
                    listOf("Mathematics", "Physics", "English", "History", "Chemistry") + 
                        subjects.filter { it !in listOf("Mathematics", "Physics", "English", "History", "Chemistry") }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    allSubjects.forEach { sub ->
                        val isSelected = selectedSubject == sub
                        SuggestionChip(
                            onClick = { selectedSubject = sub },
                            label = { Text(sub, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.normal))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.normal)) {
                    // Due Date
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Due Date *", 
                            style = MaterialTheme.typography.titleSmall, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(Spacing.small))
                        OutlinedTextField(
                            value = Instant.fromEpochMilliseconds(dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                            onValueChange = {},
                            readOnly = true,
                            isError = isDeadlinePast,
                            supportingText = {
                                if (isDeadlinePast) {
                                    Text("⚠ Overdue", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.CalendarToday, "Pilih Tanggal", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        )
                    }

                    // Due Time
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Due Time", 
                            style = MaterialTheme.typography.titleSmall, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(Spacing.small))
                        OutlinedTextField(
                            value = dueTime ?: "--:--",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            trailingIcon = {
                                IconButton(onClick = { showTimePicker = true }) {
                                    Icon(Icons.Default.Schedule, "Pilih Jam", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.normal))
                
                // Est Time Slider
                Text(
                    "Est. Time (min): $estimatedMinutes", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = estimatedMinutes.toFloat(),
                    onValueChange = { estimatedMinutes = it.toInt() },
                    valueRange = 0f..240f,
                    steps = 23,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(Spacing.normal))

                // Priority
                Text(
                    "Priority", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(Spacing.small))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Priority.entries.forEach { p ->
                        val isSelected = priority == p
                        val pColor = when (p) {
                            Priority.LOW -> PriorityLow
                            Priority.MEDIUM -> PriorityMedium
                            Priority.HIGH -> PriorityHigh
                        }
                        
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { priority = p },
                            shape = MaterialTheme.shapes.medium,
                            color = if (isSelected) pColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) pColor else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(pColor))
                                Spacer(Modifier.width(Spacing.small))
                                Text(
                                    p.name.lowercase().replaceFirstChar { it.uppercase() }, 
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) pColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.normal))

                // Status
                Text(
                    "Status", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(Spacing.small))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    TaskStatus.entries.forEach { s ->
                        val isSelected = status == s
                        InputChip(
                            selected = isSelected,
                            onClick = { status = s },
                            label = { Text(s.value.replace("_", " ").replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f),
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = InputChipDefaults.inputChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant,
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            leadingIcon = {
                            val icon = when(s) {
                                TaskStatus.TODO -> Icons.Default.Description
                                TaskStatus.IN_PROGRESS -> Icons.Default.HourglassEmpty
                                TaskStatus.DONE -> Icons.Default.Check
                            }
                            Icon(icon, null, Modifier.size(14.dp), tint = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.normal))

                // Description
                Text(
                    "Description", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(Spacing.small))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Add details about this task...", color = MaterialTheme.colorScheme.outline) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                Spacer(Modifier.height(Spacing.large))

                // Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.4f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant, 
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Cancel")
                    }
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
                        modifier = Modifier.weight(0.6f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        enabled = title.isNotBlank() && (!isDeadlinePast || taskId != null) &&
                                (state !is AddEditTaskUiState.Loading)
                    ) {
                        if (state is AddEditTaskUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Icon(Icons.Default.Save, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(Spacing.small))
                            Text(if (taskId == null) "Add Task" else "Save Changes")
                        }
                    }
                }
            }
            
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis -> 
                        dueDate = utcMillis.toLocalMillisFromUtc() 
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
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
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
    
    // Add Subject Dialog
    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject", color = MaterialTheme.colorScheme.onSurface) },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                OutlinedTextField(
                    value = newSubjectName,
                    onValueChange = { newSubjectName = it },
                    label = { Text("Subject Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newSubjectName.isNotBlank()) {
                                viewModel.addSubject(newSubjectName)
                                selectedSubject = newSubjectName
                                newSubjectName = ""
                                showAddSubjectDialog = false
                            }
                        }
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newSubjectName.isNotBlank()) {
                        viewModel.addSubject(newSubjectName)
                        selectedSubject = newSubjectName
                        newSubjectName = ""
                        showAddSubjectDialog = false
                    }
                }) { Text("Add", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel", color = MaterialTheme.colorScheme.outline) }
            }
        )
    }
}
