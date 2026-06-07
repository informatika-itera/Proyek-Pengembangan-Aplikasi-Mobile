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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyhub.core.util.capitalizeFirst
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.components.GlassIconButton
import com.studyhub.presentation.components.StudyHubHeader
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
    
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TasksUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToSmartPriority,
                    icon = {
                        Icon(Icons.Default.AutoAwesome, "Prioritas AI")
                    },
                    text = { Text("AI Priority") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    expanded = !listState.isScrollInProgress
                )

                FloatingActionButton(
                    onClick = { 
                        editingTaskId = null
                        showAddBottomSheet = true 
                    },
                    containerColor = Color(0xFF5F5E5A),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, "Tambah tugas")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is TasksUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TasksUiState.Error -> {
                    EmptyStateView(
                        message = state.message,
                        actionLabel = "Coba Lagi",
                        onAction = { viewModel.clearFilters() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is TasksUiState.Success -> {
                    // Header Section
                    StudyHubHeader(
                        title = "My Tasks",
                        subtitle = {
                            Text("${state.filteredTasks.size} tasks found", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        actions = {
                            GlassIconButton(
                                icon = Icons.Default.AutoAwesome,
                                onClick = onNavigateToSmartPriority,
                                contentDescription = "Smart Priority"
                            )
                            GlassIconButton(
                                icon = if (state.viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.AutoMirrored.Filled.List,
                                onClick = { viewModel.toggleViewMode() },
                                contentDescription = "Ganti Tampilan"
                            )
                        },
                        content = {
                            // Glass Search Bar
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.22f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                            ) {
                                TextField(
                                    value = state.searchQuery,
                                    onValueChange = viewModel::setSearchQuery,
                                    modifier = Modifier.fillMaxSize(),
                                    placeholder = { 
                                        Text("Search tasks or subjects...", color = Color.White.copy(alpha = 0.60f), fontSize = 14.sp) 
                                    },
                                    leadingIcon = { 
                                        Icon(Icons.Default.Search, "Cari", tint = Color.White.copy(alpha = 0.8f))
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Search,
                                        capitalization = KeyboardCapitalization.Words
                                    )
                                )
                            }
                        }
                    )

                    // Body Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 160.dp)
                    ) {
                        // Filters Section
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            // Status Tabs
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                            ) {
                                item {
                                    FilterTab(
                                        selected = state.filterStatus == null,
                                        onClick = { viewModel.setFilter(null, state.filterPriority, state.filterSubject) },
                                        label = "All",
                                        count = state.taskCounts["all"] ?: 0
                                    )
                                }
                                items(TaskStatus.entries) { status ->
                                    FilterTab(
                                        selected = state.filterStatus == status,
                                        onClick = { viewModel.setFilter(status, state.filterPriority, state.filterSubject) },
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
                                    items(state.availableSubjects) { subject ->
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

                            Spacer(Modifier.height(8.dp))

                            // Priority Chips
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PriorityChip(
                                    selected = state.filterPriority == null,
                                    onClick = { viewModel.setFilter(state.filterStatus, null, state.filterSubject) },
                                    label = "All Priority"
                                )
                                Priority.entries.forEach { priority ->
                                    PriorityChip(
                                        selected = state.filterPriority == priority,
                                        onClick = { viewModel.setFilter(state.filterStatus, priority, state.filterSubject) },
                                        label = priority.name.capitalizeFirst()
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

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
                                    Icon(Icons.Default.Sort, "Urutkan", Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
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
                                                        Icon(Icons.Default.Check, "Terpilih",
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
                                        style = MaterialTheme.typography.labelMedium
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
                                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = state.filteredTasks,
                                        key = { it.id },
                                        contentType = { "task" }
                                    ) { task ->
                                        val dismissState = rememberSwipeToDismissBoxState(
                                            confirmValueChange = { value ->
                                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    viewModel.showDeleteConfirm(task.id)
                                                    false // Don't dismiss yet, wait for dialog
                                                } else false
                                            }
                                        )

                                        SwipeToDismissBox(
                                            state = dismissState,
                                            enableDismissFromStartToEnd = false,
                                            backgroundContent = {
                                                val color = when (dismissState.dismissDirection) {
                                                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                                    else -> Color.Transparent
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(color)
                                                        .padding(horizontal = 20.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Hapus",
                                                        tint = Color.White
                                                    )
                                                }
                                            }
                                        ) {
                                            TaskCard(
                                                task = task,
                                                onEdit = {
                                                    editingTaskId = task.id
                                                    showAddBottomSheet = true
                                                },
                                                onDelete = { viewModel.showDeleteConfirm(task.id) },
                                                onStatusChange = { newStatus ->
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    viewModel.updateStatus(task.id, newStatus)
                                                },
                                                onClick = { onNavigateToTaskDetail(task.id) }
                                            )
                                        }
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = state.filteredTasks,
                                        key = { it.id },
                                        contentType = { "task" }
                                    ) { task ->
                                        TaskGridCard(
                                            task = task,
                                            onEdit = {
                                                editingTaskId = task.id
                                                showAddBottomSheet = true
                                            },
                                            onDelete = { viewModel.showDeleteConfirm(task.id) },
                                            onStatusChange = { newStatus ->
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.updateStatus(task.id, newStatus)
                                            },
                                            onClick = { onNavigateToTaskDetail(task.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Delete Confirmation Dialog
                    if (state.deleteConfirmTaskId != null) {
                        val taskIdToDelete = state.deleteConfirmTaskId
                        AlertDialog(
                            onDismissRequest = { viewModel.showDeleteConfirm(null) },
                            title = { Text("Delete Task") },
                            text = { Text("Are you sure you want to delete this task?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.deleteTask(taskIdToDelete)
                                    viewModel.showDeleteConfirm(null)
                                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                            },
                            dismissButton = {
                                TextButton(onClick = { viewModel.showDeleteConfirm(null) }) {
                                    Text("Cancel")
                                }
                            }
                        )
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
        color = if (selected) Color(0xFF5F5E5A) else Color.White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE0D8CE))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = if (selected) Color.White else Color(0xFF888888),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(Modifier.width(6.dp))
            Surface(
                color = if (selected) Color.White.copy(alpha = 0.25f) else Color(0xFFF2EDE4),
                shape = CircleShape
            ) {
                Text(
                    count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (selected) Color.White else Color(0xFF888888),
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
        color = if (selected) Color(0xFF5F5E5A) else Color.White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0D8CE))
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else Color(0xFF888888),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
