package com.studyhub.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.components.QuickStatCard
import com.studyhub.presentation.components.TaskCard
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.screens.task.AddEditTaskBottomSheet
import com.studyhub.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }
    var deleteTaskConfirmId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadData() }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadData() },
        state = pullToRefreshState
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.normal),
            verticalArrangement = Arrangement.spacedBy(Spacing.normal)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Halo! 👋",
                            style = MaterialTheme.typography.headlineMedium)
                        Text("Semangat belajar hari ini!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickStatCard(
                        "Hari Ini", "${uiState.todayTasks.size}",
                        "tugas", Icons.Default.Today,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        "Terlambat", "${uiState.overdueCount}",
                        "tugas", Icons.Default.Warning,
                        containerColor = if (uiState.overdueCount > 0)
                            MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        "Selesai", "${uiState.completedThisWeek}",
                        "minggu ini", Icons.Default.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Text("Tugas Hari Ini",
                    style = MaterialTheme.typography.titleLarge)
            }
            if (uiState.todayTasks.isEmpty()) {
                item {
                    EmptyStateView(
                        message = "Tidak ada tugas hari ini",
                        actionLabel = "Tambah Tugas",
                        onAction = { 
                            editingTaskId = null
                            showAddBottomSheet = true 
                        }
                    )
                }
            } else {
                items(
                    items = uiState.todayTasks,
                    key = { it.id },
                    contentType = { "task_card" }
                ) { task ->
                    TaskCard(
                        task = task,
                        onEdit = { 
                            editingTaskId = task.id
                            showAddBottomSheet = true 
                        },
                        onDelete = { deleteTaskConfirmId = task.id }
                    )
                }
            }
            if (uiState.upcomingTasks.isNotEmpty()) {
                item {
                    Text("Akan Datang",
                        style = MaterialTheme.typography.titleLarge)
                }
                items(
                    items = uiState.upcomingTasks,
                    key = { it.id },
                    contentType = { "task_card" }
                ) { task ->
                    TaskCard(
                        task = task,
                        onEdit = { 
                            editingTaskId = task.id
                            showAddBottomSheet = true 
                        },
                        onDelete = { deleteTaskConfirmId = task.id }
                    )
                }
            }
        }
    }
    
    if (showAddBottomSheet) {
        AddEditTaskBottomSheet(
            taskId = editingTaskId,
            onDismiss = { showAddBottomSheet = false },
            onSuccess = {
                showAddBottomSheet = false
                viewModel.loadData()
            }
        )
    }

    if (deleteTaskConfirmId != null) {
        AlertDialog(
            onDismissRequest = { deleteTaskConfirmId = null },
            title = { Text("Hapus Tugas") },
            text = { Text("Yakin ingin menghapus tugas ini?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(deleteTaskConfirmId!!)
                    deleteTaskConfirmId = null
                }) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTaskConfirmId = null }) { Text("Batal") }
            }
        )
    }
}
