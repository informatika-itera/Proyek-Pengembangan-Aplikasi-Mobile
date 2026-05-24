package com.studyhub.presentation.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.atStartOfDayMillis
import com.studyhub.presentation.components.CalendarDayCell
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.components.TaskCard
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.screens.task.AddEditTaskBottomSheet
import com.studyhub.presentation.theme.Spacing
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val viewModel: CalendarViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }
    var deleteTaskConfirmId by remember { mutableStateOf<String?>(null) }

    var currentMonth by remember {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        mutableStateOf(LocalDate(today.year, today.month, 1))
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllTaskDates()
        viewModel.selectDate(uiState.selectedDate)
    }

    val daysInMonth = remember(currentMonth) {
        val firstDayOfNextMonth = if (currentMonth.monthNumber == 12)
            LocalDate(currentMonth.year + 1, 1, 1)
        else
            LocalDate(currentMonth.year, currentMonth.monthNumber + 1, 1)
        val lastDayOfMonth = firstDayOfNextMonth.minus(1, DateTimeUnit.DAY)
        lastDayOfMonth.dayOfMonth
    }

    val firstDayOfWeek = remember(currentMonth) {
        currentMonth.dayOfWeek.isoDayNumber % 7 // 0 for Sunday
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalender Tugas") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Month Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.normal, vertical = Spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = {
                        currentMonth = if (currentMonth.monthNumber == 1)
                            LocalDate(currentMonth.year - 1, 12, 1)
                        else
                            LocalDate(currentMonth.year, currentMonth.monthNumber - 1, 1)
                    }) {
                        Icon(Icons.Default.ChevronLeft, "Bulan sebelumnya")
                    }
                    IconButton(onClick = {
                        currentMonth = if (currentMonth.monthNumber == 12)
                            LocalDate(currentMonth.year + 1, 1, 1)
                        else
                            LocalDate(currentMonth.year, currentMonth.monthNumber + 1, 1)
                    }) {
                        Icon(Icons.Default.ChevronRight, "Bulan berikutnya")
                    }
                }
            }

            // WeekDays Header
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.small)) {
                listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Calendar Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .padding(Spacing.small)
                    .heightIn(max = 300.dp),
                contentPadding = PaddingValues(Spacing.extraSmall)
            ) {
                // Empty cells
                items(
                    count = firstDayOfWeek,
                    contentType = { "empty_cell" }
                ) {
                    Box(modifier = Modifier.aspectRatio(1f))
                }

                // Days
                items(
                    count = daysInMonth,
                    key = { index -> 
                        val day = index + 1
                        "day_${currentMonth.year}_${currentMonth.monthNumber}_$day"
                    },
                    contentType = { "day_cell" }
                ) { index ->
                    val day = index + 1
                    val date = LocalDate(currentMonth.year, currentMonth.month, day)
                    val isSelected = date == uiState.selectedDate
                    val isToday = date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val hasTask = uiState.taskDates.contains(date)

                    CalendarDayCell(
                        dayOfMonth = day,
                        isSelected = isSelected,
                        isToday = isToday,
                        hasTask = hasTask,
                        onSelect = { viewModel.selectDate(date) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.small))

            // Tasks List
            Text(
                text = "Tugas pada ${uiState.selectedDate}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = Spacing.normal, vertical = Spacing.small),
                fontWeight = FontWeight.Bold
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.tasksOnSelectedDate.isEmpty()) {
                EmptyStateView(
                    message = "Tidak ada tugas untuk tanggal ini",
                    actionLabel = "Tambah Tugas",
                    onAction = { 
                        editingTaskId = null
                        showAddBottomSheet = true 
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    items(
                        items = uiState.tasksOnSelectedDate,
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
    }
    
    if (showAddBottomSheet) {
        AddEditTaskBottomSheet(
            taskId = editingTaskId,
            initialDate = uiState.selectedDate.atStartOfDayMillis(),
            onDismiss = { showAddBottomSheet = false },
            onSuccess = {
                showAddBottomSheet = false
                viewModel.loadAllTaskDates()
                viewModel.selectDate(uiState.selectedDate)
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
