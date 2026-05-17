package com.example.todomaster.presentation.screens.quadrantdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.presentation.components.TaskItem
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuadrantDetailScreen(
    initialQuadrantId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    onNavigateToAddTask: () -> Unit,
    viewModel: QuadrantDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val quadrants = Quadrant.entries
    var selectedQuadrant by remember { mutableStateOf(Quadrant.fromValue(initialQuadrantId)) }

    LaunchedEffect(selectedQuadrant) {
        viewModel.setQuadrant(selectedQuadrant)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matriks", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = onNavigateBack,
                    icon = { Icon(Icons.Default.GridView, contentDescription = "Matriks") },
                    label = { Text("Matriks") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Menuju layar Semua */ },
                    icon = { Icon(Icons.Default.List, contentDescription = "Semua") },
                    label = { Text("Semua") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Menuju layar Pengaturan */ },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Pengaturan") },
                    label = { Text("Pengaturan") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = quadrants.indexOf(selectedQuadrant)) {
                quadrants.forEach { quadrant ->
                    Tab(
                        selected = selectedQuadrant == quadrant,
                        onClick = { selectedQuadrant = quadrant },
                        text = {
                            Text(
                                text = when (quadrant) {
                                    Quadrant.DO_FIRST -> "Do first"
                                    Quadrant.SCHEDULE -> "Schedule"
                                    Quadrant.DELEGATE -> "Delegate"
                                    Quadrant.DONT_DO -> "Don't do"
                                },
                                fontWeight = if (selectedQuadrant == quadrant) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalTasks = uiState.tasks.size
                Text(
                    text = "$totalTasks tugas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = selectedQuadrant.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.tasks) { task ->
                    TaskItem(
                        task = task,
                        onClick = { onNavigateToTaskDetail(task.id) },
                        onToggleComplete = { viewModel.toggleTaskCompletion(task) }
                    )
                }

                if (uiState.tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada tugas di kuadran ini.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}