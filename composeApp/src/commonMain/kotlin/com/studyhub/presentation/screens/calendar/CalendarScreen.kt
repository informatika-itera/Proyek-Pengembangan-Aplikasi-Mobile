package com.studyhub.presentation.screens.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.SystemAppearance
import com.studyhub.core.util.atStartOfDayMillis
import com.studyhub.core.util.capitalizeFirst
import com.studyhub.core.util.toLocalDate
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.CalendarDayCell
import com.studyhub.presentation.components.EmptyStateView
import com.studyhub.presentation.components.LiquidGlassCard
import com.studyhub.presentation.components.PillBadge
import com.studyhub.presentation.components.StudyHubHeader
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.screens.task.AddEditTaskBottomSheet
import com.studyhub.presentation.theme.*
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

    // Removed hardcoded SystemAppearance, handled in App.kt

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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            // Header Section
            StudyHubHeader(
                title = "Calendar",
                subtitle = {
                    Text("${uiState.upcomingDeadlinesCount} upcoming deadlines", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    Surface(
                        color = Color.White,
                        shape = CircleShape,
                    ) {
                        Text(
                            text = "${currentMonth.month.name.lowercase().capitalizeFirst()} ${currentMonth.year}",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            color = Color(0xFF5F5E5A),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 110.dp, bottom = 100.dp)
            ) {
                // Calendar Grid Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Month Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    currentMonth = if (currentMonth.monthNumber == 1)
                                        LocalDate(currentMonth.year - 1, 12, 1)
                                    else
                                        LocalDate(currentMonth.year, currentMonth.monthNumber - 1, 1)
                                    viewModel.updateMonthOverview(currentMonth)
                                },
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = Color(0xFFF2EDE4)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.ChevronLeft, "Prev", modifier = Modifier.size(18.dp))
                                }
                            }
                            
                            Text(
                                text = "${currentMonth.month.name.lowercase().capitalizeFirst()} ${currentMonth.year}",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2416)
                            )
                            
                            Surface(
                                onClick = {
                                    currentMonth = if (currentMonth.monthNumber == 12)
                                        LocalDate(currentMonth.year + 1, 1, 1)
                                    else
                                        LocalDate(currentMonth.year, currentMonth.monthNumber + 1, 1)
                                    viewModel.updateMonthOverview(currentMonth)
                                },
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = Color(0xFFF2EDE4)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.ChevronRight, "Next", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Week Header
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFFBBBBBB)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Days Grid
                        val totalCells = firstDayOfWeek + daysInMonth
                        val rows = (totalCells + 6) / 7
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(rows) { row ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    repeat(7) { col ->
                                        val index = row * 7 + col
                                        val day = index - firstDayOfWeek + 1
                                        
                                        Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                                            if (day in 1..daysInMonth) {
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
                                    }
                                }
                            }
                        }
                    }
                }

                // Selected Day Tasks Section
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val headerText = if (uiState.selectedDate == today) "Today's Tasks" else {
                        uiState.selectedDate.run {
                            "${dayOfWeek.name.lowercase().capitalizeFirst()}, ${month.name.lowercase().capitalizeFirst().take(3)} $dayOfMonth"
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = headerText,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold
                        )
                        PillBadge(
                            text = {
                                Text(
                                    "${uiState.tasksOnSelectedDate.size} tasks",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            containerColor = Color(0xFFF2EDE4),
                            contentColor = Color(0xFF888888)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    if (uiState.tasksOnSelectedDate.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday, 
                                    null, 
                                    modifier = Modifier.size(32.dp),
                                    tint = Color(0xFFBBBBBB)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "No tasks for this day", 
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFAAAAAA)
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.tasksOnSelectedDate.forEach { task ->
                                CalendarTaskCard(
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

                // Month Overview Section
                if (uiState.upcomingMonthTasks.isNotEmpty()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text("Month Overview", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.upcomingMonthTasks.take(5).forEach { task ->
                                OverviewTaskCard(task)
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showAddBottomSheet) {
        AddEditTaskBottomSheet(
            taskId = editingTaskId,
            initialDate = uiState.selectedDate.atStartOfDayMillis(),
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

    if (deleteTaskConfirmId != null) {
        AlertDialog(
            onDismissRequest = { deleteTaskConfirmId = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(deleteTaskConfirmId!!)
                    deleteTaskConfirmId = null
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTaskConfirmId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CalendarTaskCard(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDone = task.status == TaskStatus.DONE
    val subjectAccentColor = when (task.subject.lowercase()) {
        "mathematics", "calculus" -> Color(0xFF3B82F6)
        "chemistry", "code" -> Color(0xFF10B981)
        else -> Color(0xFF8B7355)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subject Initial Box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(subjectAccentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    task.subject.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = subjectAccentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2C2416),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        task.subject,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF888888)
                    )
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFBBBBBB))
            }
        }
    }
}

@Composable
fun OverviewTaskCard(task: Task) {
    val subjectAccentColor = when (task.subject.lowercase()) {
        "mathematics", "calculus" -> Color(0xFF3B82F6)
        "chemistry", "code" -> Color(0xFF10B981)
        else -> Color(0xFF8B7355)
    }
    val date = task.dueDate.toLocalDate()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Date Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(subjectAccentColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp
                    )
                    Text(
                        date.month.name.take(3).capitalizeFirst(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 8.sp
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2C2416),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    task.subject,
                    style = MaterialTheme.typography.labelSmall,
                    color = subjectAccentColor
                )
            }
            
            // Priority Badge
            if (task.priority == Priority.HIGH) {
                Surface(
                    color = Color(0xFFFEECEC),
                    shape = CircleShape
                ) {
                    Text(
                        "high",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFFA32D2D),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (task.priority == Priority.MEDIUM) {
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = CircleShape
                ) {
                    Text(
                        "medium",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun getSubjectColor(subject: String): Color {
    val hash = subject.hashCode()
    val colors = listOf(
        Color(0xFF7B6FA0), // Purple
        Color(0xFF6B8F71), // Green
        Color(0xFF8B7355), // Brown
        Color(0xFFC06C84), // Rose
        Color(0xFF355C7D), // Dark Blue
        Color(0xFFF67280), // Salmon
        Color(0xFF45B7D1)  // Light Blue
    )
    return colors[kotlin.math.abs(hash) % colors.size]
}

private fun formatDeadline(epochMillis: Long): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val target = epochMillis.toLocalDate()
    val days = target.toEpochDays() - now.toEpochDays()
    
    return when {
        days == 0 -> "Today"
        days == 1 -> "Tomorrow"
        days > 1 -> "${days}d left"
        else -> "Overdue"
    }
}
