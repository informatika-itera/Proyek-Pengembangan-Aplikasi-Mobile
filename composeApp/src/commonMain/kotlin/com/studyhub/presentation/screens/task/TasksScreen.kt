package com.studyhub.presentation.screens.task

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.components.TaskCard
import com.studyhub.presentation.components.TaskGridCard
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(navController: NavController) {
    val viewModel: TasksViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Bottom Sheet State
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadTasks() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingTaskId = null
                    showAddBottomSheet = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Tambah tugas")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                        )
                    )
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "My Tasks",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${uiState.filteredTasks.size} tasks found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledIconButton(
                                onClick = { viewModel.toggleViewMode() },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.List,
                                    contentDescription = "Switch View"
                                )
                            }
                            
                            FilledIconButton(
                                onClick = { 
                                    editingTaskId = null
                                    showAddBottomSheet = true 
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, "Add")
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Search Bar
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        placeholder = { Text("Search tasks or subjects...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.9f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                            disabledContainerColor = Color.White.copy(alpha = 0.9f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true
                    )
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                // Filters Section
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        item {
                            val allCount = uiState.taskCounts["all"] ?: 0
                            FilterChip(
                                selected = uiState.filterStatus == null,
                                onClick = { viewModel.setFilter(null, uiState.filterPriority, uiState.filterSubject) },
                                label = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("All", fontSize = 12.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Surface(
                                            color = if (uiState.filterStatus == null) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                allCount.toString(), 
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(TaskStatus.entries) { status ->
                            val count = uiState.taskCounts[status.value] ?: 0
                            FilterChip(
                                selected = uiState.filterStatus == status,
                                onClick = { viewModel.setFilter(status, uiState.filterPriority, uiState.filterSubject) },
                                label = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(status.value.replace("_", " ").replaceFirstChar { it.uppercase() }, fontSize = 12.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Surface(
                                            color = if (uiState.filterStatus == status) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                count.toString(), 
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            TextButton(
                                onClick = { viewModel.setFilter(uiState.filterStatus, null, uiState.filterSubject) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    "All Priority", 
                                    style = MaterialTheme.typography.labelMedium, 
                                    color = if (uiState.filterPriority == null) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                        }
                        items(Priority.entries) { priority ->
                            TextButton(
                                onClick = { viewModel.setFilter(uiState.filterStatus, priority, uiState.filterSubject) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    priority.name.lowercase().replaceFirstChar { it.uppercase() },
                                    color = if (uiState.filterPriority == priority) MaterialTheme.colorScheme.primary else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        item {
                            Spacer(Modifier.width(16.dp))
                            TextButton(onClick = { /* Sort Dialog */ }) {
                                Text("Sort", fontSize = 12.sp, color = Color.Gray)
                                Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(16.dp), tint = Color.Gray)
                            }
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
                            contentPadding = PaddingValues(16.dp),
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
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(250),
                                        fadeOutSpec = tween(250),
                                        placementSpec = tween(250)
                                    )
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
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
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(250),
                                        fadeOutSpec = tween(250),
                                        placementSpec = tween(250)
                                    )
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
            onDismiss = { showAddBottomSheet = false },
            onSuccess = {
                showAddBottomSheet = false
                viewModel.loadTasks()
            }
        )
    }
}
