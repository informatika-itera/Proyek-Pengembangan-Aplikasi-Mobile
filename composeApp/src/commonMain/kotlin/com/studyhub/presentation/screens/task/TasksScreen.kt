package com.studyhub.presentation.screens.task

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyhub.core.util.capitalizeFirst
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
import com.studyhub.presentation.components.GlassIconButton
import com.studyhub.presentation.components.ScreenHeader
import com.studyhub.presentation.components.TaskCard
import com.studyhub.presentation.components.TaskGridCard
import com.studyhub.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToSmartPriority: () -> Unit
) {
    val viewModel: TasksViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.NavigateBack -> { /* Handle if needed */ }
            }
        }
    }

    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    editingTaskId = null
                    showAddBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(56.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah tugas")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            val state = uiState
            
            // Header Section
            ScreenHeader {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "My Tasks",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (state is TasksUiState.Success) {
                                Text(
                                    "${state.filteredTasks.size} tasks found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                Spacing.small
                            )
                        ) {
                            IconButton(
                                onClick = onNavigateToSmartPriority,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "AI Priority",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (state is TasksUiState.Success) {
                                IconButton(
                                    onClick = { viewModel.toggleViewMode() },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        if (state.viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.List,
                                        contentDescription = "Toggle view",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(Spacing.normal))

                    // Search bar inside header
                    if (state is TasksUiState.Success) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = viewModel::setSearchQuery,
                            placeholder = {
                                Text(
                                    "Search tasks or subjects...",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search, null,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (state.searchQuery.isNotBlank()) {
                                    IconButton(onClick = {
                                        viewModel.setSearchQuery("")
                                    }) {
                                        Icon(
                                            Icons.Default.Close, null,
                                            tint = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White.copy(alpha = 0.8f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                        )
                    }
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 180.dp)
            ) {
                when (state) {
                    is TasksUiState.Loading -> LoadingView()
                    is TasksUiState.Error -> ErrorView(
                        message = state.message,
                        onRetry = { viewModel.setFilter(null, null, null) }
                    )
                    is TasksUiState.Success -> {
                        // Filters Section
                        Column(modifier = Modifier.padding(top = Spacing.normal)) {
                            // Status Tabs
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                                contentPadding = PaddingValues(horizontal = Spacing.normal),
                            ) {
                                item {
                                    FilterTab(
                                        selected = state.filterStatus == null,
                                        onClick = { viewModel.setFilter(null, state.filterPriority, state.filterSubject) },
                                        label = "All",
                                        count = state.taskCounts["all"] ?: 0
                                    )
                                }
                                items(TaskStatus.entries, key = { it.value }, contentType = { "status_filter" }) { status ->
                                    val currentPriority = state.filterPriority
                                    val currentSubject = state.filterSubject
                                    FilterTab(
                                        selected = state.filterStatus == status,
                                        onClick = { viewModel.setFilter(status, currentPriority, currentSubject) },
                                        label = status.value.replace("_", " ").capitalizeFirst(),
                                        count = state.taskCounts[status.value] ?: 0
                                    )
                                }
                            }

                            // Subject filter row
                            if (state.availableSubjects.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                                    contentPadding = PaddingValues(horizontal = Spacing.normal),
                                    modifier = Modifier.padding(vertical = Spacing.extraSmall)
                                ) {
                                    item {
                                        FilterChip(
                                            selected = state.filterSubject == null,
                                            onClick = { viewModel.setSubjectFilter(null) },
                                            label = { Text("Semua Matkul") },
                                            shape = MaterialTheme.shapes.small
                                        )
                                    }
                                    items(state.availableSubjects, key = { it }, contentType = { "subject_filter" }) { subject ->
                                        FilterChip(
                                            selected = state.filterSubject == subject,
                                            onClick = {
                                                viewModel.setSubjectFilter(
                                                    if (state.filterSubject == subject)
                                                        null else subject
                                                )
                                            },
                                            label = { Text(subject) },
                                            shape = MaterialTheme.shapes.small
                                        )
                                    }
                                }
                            }

                            // Priority Chips
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.normal),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val currentStatus = state.filterStatus
                                val currentSubject = state.filterSubject
                                
                                PriorityChip(
                                    selected = state.filterPriority == null,
                                    onClick = { viewModel.setFilter(currentStatus, null, currentSubject) },
                                    label = "All Priority"
                                )
                                Priority.entries.forEach { priority ->
                                    PriorityChip(
                                        selected = state.filterPriority == priority,
                                        onClick = { viewModel.setFilter(currentStatus, priority, currentSubject) },
                                        label = priority.name.capitalizeFirst()
                                    )
                                }
                            }

                            // Sort + Show Completed row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.normal),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Sort dropdown
                                var showSortMenu by remember { mutableStateOf(false) }
                                TextButton(
                                    onClick = { showSortMenu = true }
                                ) {
                                    Icon(Icons.Default.Sort, contentDescription = "Urutkan", Modifier.size(16.dp))
                                    Spacer(Modifier.width(Spacing.extraSmall))
                                    Text(
                                        "Urutkan: ${state.sortBy.name.lowercase().replace("_", " ").capitalizeFirst()}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    DropdownMenu(
                                        expanded = showSortMenu,
                                        onDismissRequest = { showSortMenu = false }
                                    ) {
                                        SortBy.entries.forEach { sort ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(sort.name.lowercase().replace("_", " ").capitalizeFirst())
                                                },
                                                onClick = {
                                                    viewModel.setSortBy(sort)
                                                    showSortMenu = false
                                                },
                                                leadingIcon = {
                                                    if (state.sortBy == sort)
                                                        Icon(Icons.Default.Check, contentDescription = "Terpilih",
                                                            Modifier.size(16.dp))
                                                }
                                            )
                                        }
                                    }
                                }

                                // Show completed toggle
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Selesai",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Switch(
                                        checked = state.showCompleted,
                                        onCheckedChange = { viewModel.toggleShowCompleted() },
                                        modifier = Modifier.scale(0.8f)
                                    )
                                }
                            }
                        }

                        // Tasks List/Grid
                        if (state.filteredTasks.isEmpty()) {
                            EmptyStateView(
                                message = "No tasks found",
                                actionLabel = "Add New Task",
                                onAction = {
                                    editingTaskId = null
                                    showAddBottomSheet = true
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            if (state.viewMode == ViewMode.LIST) {
                                LazyColumn(
                                    state = listState,
                                    contentPadding = PaddingValues(top = Spacing.normal, bottom = 100.dp, start = Spacing.normal, end = Spacing.normal),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = state.filteredTasks,
                                        key = { it.id },
                                        contentType = { "task_list_item" }
                                    ) { task ->
                                        TaskCard(
                                            task = task,
                                            onEdit = {
                                                editingTaskId = task.id
                                                showAddBottomSheet = true
                                            },
                                            onDelete = { viewModel.showDeleteConfirm(task.id) },
                                            onClick = { onNavigateToTaskDetail(task.id) }
                                        )
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(top = Spacing.normal, bottom = 100.dp, start = Spacing.normal, end = Spacing.normal),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = state.filteredTasks,
                                        key = { it.id },
                                        contentType = { "task_grid_item" }
                                    ) { task ->
                                        val taskId = task.id
                                        TaskGridCard(
                                            task = task,
                                            onEdit = {
                                                editingTaskId = taskId
                                                showAddBottomSheet = true
                                            },
                                            onDelete = { viewModel.showDeleteConfirm(taskId) },
                                            onClick = { onNavigateToTaskDetail(taskId) }
                                        )
                                    }
                                }
                            }
                        }

                        // Delete Confirmation Dialog
                        if (state.deleteConfirmTaskId != null) {
                            AlertDialog(
                                onDismissRequest = { viewModel.showDeleteConfirm(null) },
                                title = { Text("Hapus Tugas") },
                                text = { Text("Apakah Anda yakin ingin menghapus tugas ini? Tindakan ini tidak dapat dibatalkan.") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        viewModel.deleteTask(state.deleteConfirmTaskId)
                                        viewModel.showDeleteConfirm(null)
                                    }) { 
                                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { viewModel.showDeleteConfirm(null) }) {
                                        Text("Batal")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Bottom Sheet for Add/Edit
    if (showAddBottomSheet) {
        AddEditTaskBottomSheet(
            taskId = editingTaskId,
            initialDate = null,
            onDismiss = { 
                showAddBottomSheet = false
                editingTaskId = null
            },
            onSuccess = {
                showAddBottomSheet = false
                editingTaskId = null
            }
        )
    }
}

@Composable
fun FilterTab(selected: Boolean, onClick: () -> Unit, label: String, count: Int) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.normal, vertical = Spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(Modifier.width(Spacing.extraSmall))
            Surface(
                color = if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            ) {
                Text(
                    count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PriorityChip(selected: Boolean, onClick: () -> Unit, label: String) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
