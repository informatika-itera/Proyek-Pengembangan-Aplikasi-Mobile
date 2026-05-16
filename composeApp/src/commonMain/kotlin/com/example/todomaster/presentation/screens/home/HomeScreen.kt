package com.example.todomaster.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todomaster.domain.model.Task
import com.example.todomaster.presentation.components.TaskItem
import com.example.todomaster.presentation.theme.ColorDelegate
import com.example.todomaster.presentation.theme.ColorDoFirst
import com.example.todomaster.presentation.theme.ColorDontDo
import com.example.todomaster.presentation.theme.ColorSchedule
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TodoMaster Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { QuadrantHeader("Do First (Penting & Genting)", ColorDoFirst) }
            items(uiState.doFirstTasks) { task ->
                TaskItem(task, { onNavigateToTaskDetail(task.id) }, { viewModel.toggleTaskCompletion(task) })
                Spacer(modifier = Modifier.height(8.dp))
            }

            item { QuadrantHeader("Schedule (Penting & Tak Genting)", ColorSchedule) }
            items(uiState.scheduleTasks) { task ->
                TaskItem(task, { onNavigateToTaskDetail(task.id) }, { viewModel.toggleTaskCompletion(task) })
                Spacer(modifier = Modifier.height(8.dp))
            }

            item { QuadrantHeader("Delegate (Tak Penting & Genting)", ColorDelegate) }
            items(uiState.delegateTasks) { task ->
                TaskItem(task, { onNavigateToTaskDetail(task.id) }, { viewModel.toggleTaskCompletion(task) })
                Spacer(modifier = Modifier.height(8.dp))
            }

            item { QuadrantHeader("Don't Do (Tak Penting & Tak Genting)", ColorDontDo) }
            items(uiState.dontDoTasks) { task ->
                TaskItem(task, { onNavigateToTaskDetail(task.id) }, { viewModel.toggleTaskCompletion(task) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun QuadrantHeader(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}