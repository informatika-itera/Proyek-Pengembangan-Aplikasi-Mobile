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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.SystemAppearance
import com.studyhub.core.util.capitalizeFirst
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.components.GlassIconButton
import com.studyhub.presentation.components.LiquidGlassCard
import com.studyhub.presentation.components.StudyHubHeader
import com.studyhub.presentation.components.TaskCard
import com.studyhub.presentation.components.TaskGridCard
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(navController: NavController) {
    val viewModel: TasksViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val listState = rememberLazyListState()

    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.SmartPriority.route)
                    },
                    icon = {
                        Icon(Icons.Default.AutoAwesome, null)
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
            // Header Section
            StudyHubHeader(
                title = "My Tasks",
                subtitle = {
                    Text("${uiState.filteredTasks.size} tasks found", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    GlassIconButton(
                        icon = Icons.Default.AutoAwesome,
                        onClick = { navController.navigate(Screen.SmartPriority.route) }
                    )
                    GlassIconButton(
                        icon = if (uiState.viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.List,
                        onClick = { viewModel.toggleViewMode() }
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
                            value = uiState.searchQuery,
                            onValueChange = viewModel::setSearchQuery,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = { 
                                Text("Search tasks or subjects...", color = Color.White.copy(alpha = 0.60f), fontSize = 14.sp) 
                            },
                            leadingIcon = { 
                                Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.8f)) 
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
                            singleLine = true
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
                                selected = uiState.filterStatus == null,
                                onClick = { viewModel.setFilter(null, uiState.filterPriority, uiState.filterSubject) },
                                label = "All",
                                count = uiState.taskCounts["all"] ?: 0
                            )
                        }
                        items(TaskStatus.entries) { status ->
                            FilterTab(
                                selected = uiState.filterStatus == status,
                                onClick = { viewModel.setFilter(status, uiState.filterPriority, uiState.filterSubject) },
                                label = status.value.replace("_", " ").capitalizeFirst(),
                                count = uiState.taskCounts[status.value] ?: 0
                            )
                        }
                    }

                    // Subject filter row
                    if (uiState.availableSubjects.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            contentPadding = PaddingValues(horizontal = Spacing.normal),
                            modifier = Modifier.padding(vertical = Spacing.extraSmall)
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.filterSubject == null,
                                    onClick = { viewModel.setSubjectFilter(null) },
                                    label = { Text("Semua Matkul") },
                                    shape = MaterialTheme.shapes.small
                                )
                            }
                            items(uiState.availableSubjects) { subject ->
                                FilterChip(
                                    selected = uiState.filterSubject == subject,
                                    onClick = {
                                        viewModel.setSubjectFilter(
                                            if (uiState.filterSubject == subject)
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
                            selected = uiState.filterPriority == null,
                            onClick = { viewModel.setFilter(uiState.filterStatus, null, uiState.filterSubject) },
                            label = "All Priority"
                        )
                        Priority.entries.forEach { priority ->
                            PriorityChip(
                                selected = uiState.filterPriority == priority,
                                onClick = { viewModel.setFilter(uiState.filterStatus, priority, uiState.filterSubject) },
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
                            Icon(Icons.Default.Sort, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Urutkan: ${uiState.sortBy.name.lowercase().replace("_", " ").capitalizeFirst()}",
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
                                            if (uiState.sortBy == sort)
                                                Icon(Icons.Default.Check, null,
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
                                checked = uiState.showCompleted,
                                onCheckedChange = { viewModel.toggleShowCompleted() },
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                }

                // Tasks List/Grid
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.filteredTasks.isEmpty()) {
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
                    if (uiState.viewMode == ViewMode.LIST) {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.filteredTasks,
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
                                    onClick = { navController.navigate(Screen.TaskDetail.createRoute(task.id)) }
                                )
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
                                items = uiState.filteredTasks,
                                key = { it.id },
                                contentType = { "task_grid_item" }
                            ) { task ->
                                TaskGridCard(
                                    task = task,
                                    onEdit = {
                                        editingTaskId = task.id
                                        showAddBottomSheet = true
                                    },
                                    onDelete = { viewModel.showDeleteConfirm(task.id) },
                                    onClick = { navController.navigate(Screen.TaskDetail.createRoute(task.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (uiState.deleteConfirmTaskId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteConfirm(null) },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(uiState.deleteConfirmTaskId!!)
                    viewModel.showDeleteConfirm(null)
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteConfirm(null) }) {
                    Text("Cancel")
                }
            }
        )
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
