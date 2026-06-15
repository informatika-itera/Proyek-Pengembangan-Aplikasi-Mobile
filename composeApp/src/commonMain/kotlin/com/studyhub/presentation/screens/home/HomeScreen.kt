package com.studyhub.presentation.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.LiquidGlassCard
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
import com.studyhub.presentation.components.TaskCard
import com.studyhub.presentation.components.ScreenHeader
import com.studyhub.presentation.navigation.Screen
import com.studyhub.core.util.toTaskColor
import com.studyhub.presentation.screens.home.components.FocusTimerBottomSheet
import com.studyhub.presentation.screens.task.AddEditTaskBottomSheet
import com.studyhub.presentation.util.GreetingUtils
import com.studyhub.presentation.theme.*
import com.studyhub.presentation.screens.home.HomeUiEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun HomeProgressInsideHeader(
    completionPercentage: Int,
    doneTasks: Int,
    totalTasks: Int
) {
    if (totalTasks == 0) return
    val progress = completionPercentage / 100f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                Spacing.normal
            )
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 4.dp,
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Text(
                    "${completionPercentage}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Column {
                Text(
                    "Overall Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    "$doneTasks of $totalTasks tasks completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = Color(0xFFF59E0B),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToSmartPriority: () -> Unit,
    onNavigateToNotifHistory: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToReport: () -> Unit,
    openPomodoro: Boolean = false
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showStreakDialog by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowStreakPopup -> {
                    showStreakDialog = event.streak
                }
            }
        }
    }

    val overdueCount by remember(uiState) {
        derivedStateOf {
            if (uiState is HomeUiState.Success) {
                val now = Clock.System.now().toEpochMilliseconds()
                (uiState as HomeUiState.Success).upcomingTasks.count {
                    it.dueDate < now && it.status != TaskStatus.DONE
                }
            } else 0
        }
    }

    val subjectList by remember(uiState) {
        derivedStateOf {
            if (uiState is HomeUiState.Success) {
                (uiState as HomeUiState.Success).subjectStats.map { it.name }
            } else emptyList()
        }
    }

    val scrollState = rememberLazyListState()

    var showFocusTimer by remember { mutableStateOf(false) }
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }
    var deleteConfirmTaskId by remember { mutableStateOf<String?>(null) }

    // Automatically open Pomodoro timer if requested
    LaunchedEffect(openPomodoro) {
        if (openPomodoro) {
            showFocusTimer = true
        }
    }

    val todayDateText = remember {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        "${now.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${now.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${now.dayOfMonth}"
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingView()
            is HomeUiState.Error -> ErrorView(
                message = state.message,
                onRetry = { /* Reactive: will retry on state change or manual refresh if needed */ }
            )
            is HomeUiState.Success -> {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    // Header Section
                    item {
                        ScreenHeader(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Date
                                Text(
                                    todayDateText,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            "${GreetingUtils.getGreeting()}, ${state.userName}! ${GreetingUtils.getGreetingEmoji()}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Row {
                                            Text("You have ", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                "${state.todayTasksCount} tasks",
                                                color = HighlightGold,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(" to tackle today", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }

                                    // Bell icon
                                    Box {
                                        IconButton(
                                            onClick = onNavigateToNotifHistory,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(
                                                    Color.White.copy(alpha = 0.2f),
                                                    CircleShape
                                                )
                                        ) {
                                            Icon(
                                                Icons.Default.Notifications,
                                                contentDescription = "Notifikasi",
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        if (state.unreadNotifCount > 0) {
                                            Badge(
                                                containerColor = Color(0xFFF59E0B),
                                                modifier = Modifier.align(Alignment.TopEnd)
                                            ) {
                                                Text(
                                                    if (state.unreadNotifCount > 9) "9+"
                                                    else state.unreadNotifCount.toString()
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(Spacing.normal))

                                // Overall progress card inside header
                                HomeProgressInsideHeader(
                                    completionPercentage = state.completionPercentage,
                                    doneTasks = state.doneTasks,
                                    totalTasks = state.totalTasks
                                )
                            }
                        }
                    }

                    // Stat Cards Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.normal),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            StatBox(Modifier.weight(1f), "Total", state.totalTasks.toString(), Icons.Default.Book, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
                            StatBox(Modifier.weight(1f), "Done", state.doneTasks.toString(), Icons.Default.CheckCircle, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary)
                            StatBox(Modifier.weight(1f), "Active", state.activeTasks.toString(), Icons.Default.Schedule, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.tertiary)
                            StatBox(Modifier.weight(1f), "Due", state.dueTodayTasks.toString(), Icons.Default.Bolt, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
                        }
                    }

                    // Pomodoro Shortcut Card
                    item {
                        PomodoroShortcutCard(
                            onClick = { showFocusTimer = true },
                            modifier = Modifier.padding(horizontal = Spacing.normal)
                        )
                    }

                    // Upcoming Tasks Section Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.normal),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Upcoming Tasks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onNavigateToTasks) {
                                Text("See all", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    if (state.upcomingTasks.isEmpty()) {
                        item {
                            Text(
                                "No upcoming tasks today",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = Spacing.normal)
                            )
                        }
                    } else {
                        items(
                            items = state.upcomingTasks,
                            key = { it.id },
                            contentType = { "upcoming_task" }
                        ) { task ->
                            val taskId = task.id
                            Box(modifier = Modifier.padding(horizontal = Spacing.normal)) {
                                TaskCard(
                                    task = task,
                                    onEdit = { 
                                        editingTaskId = taskId
                                        showAddBottomSheet = true 
                                    },
                                    onDelete = { deleteConfirmTaskId = taskId },
                                    onClick = { onNavigateToTaskDetail(taskId) }
                                )
                            }
                        }
                    }

                    // Subject Progress Section
                    item {
                        SubjectProgressSection(
                            subjectStats = state.subjectStats,
                            onDetailsClick = onNavigateToProgress,
                            modifier = Modifier.padding(horizontal = Spacing.normal)
                        )
                    }

                    // Smart Priority Shortcut
                    item {
                        SmartPriorityShortcut(
                            onClick = onNavigateToSmartPriority,
                            modifier = Modifier.padding(horizontal = Spacing.normal)
                        )
                    }

                    // Progress Shortcut Card (Secondary)
                    item {
                        ProgressShortcutCard(
                            onClick = { onNavigateToProgress() },
                            modifier = Modifier.padding(horizontal = Spacing.normal)
                        )
                    }

                    // Streak Card
                    item {
                        Box(modifier = Modifier.padding(horizontal = Spacing.normal)) {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToReport() },
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(Spacing.normal),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🔥", fontSize = 20.sp)
                                        }
                                    }
                                    Spacer(Modifier.width(Spacing.normal))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Study Streak!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text("Keep it up — you're on a roll!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Icon(Icons.Default.AutoGraph, "Grafik Streak", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFocusTimer) {
        FocusTimerBottomSheet(
            onDismiss = { 
                showFocusTimer = false 
            }
        )
    }

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

    if (deleteConfirmTaskId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmTaskId = null },
            title = { Text("Hapus Tugas") },
            text = { Text("Apakah Anda yakin ingin menghapus tugas ini?") },
            confirmButton = {
                TextButton(onClick = {
                    deleteConfirmTaskId?.let { viewModel.deleteTask(it) }
                    deleteConfirmTaskId = null
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmTaskId = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showStreakDialog != null) {
        StreakPopup(
            streakCount = showStreakDialog!!,
            onDismiss = { showStreakDialog = null }
        )
    }
}

@Composable
fun StreakPopup(streakCount: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Mantap!")
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🔥", fontSize = 40.sp)
                    }
                }
                Spacer(Modifier.height(Spacing.normal))
                Text(
                    "Study Streak!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                "Kamu sudah belajar selama $streakCount hari berturut-turut. Terus semangat!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun StatBox(modifier: Modifier, label: String, value: String, icon: ImageVector, bgColor: Color, iconColor: Color) {
    Card(
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.small).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = MaterialTheme.shapes.small,
                color = bgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(Spacing.extraSmall))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PomodoroShortcutCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF5C7A5C)  // dark olive green
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.normal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    Spacing.medium
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Timer, null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        "Start Focus Session",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Pomodoro · 25 min work",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight, null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SubjectProgressSection(
    subjectStats: List<SubjectHomeStat>,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Subject Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onDetailsClick) {
                Text("Details")
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.normal),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                if (subjectStats.isEmpty()) {
                    Text(
                        "Belum ada data mata kuliah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    subjectStats.take(3).forEach { stat ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                Spacing.small
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                stat.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(80.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            LinearProgressIndicator(
                                progress = { stat.completionRate / 100f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(
                                "${stat.doneCount}/${stat.totalCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(30.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartPriorityShortcut(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        shape = MaterialTheme.shapes.large,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(Spacing.normal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.normal)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AutoAwesome, "AI Priority",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Smart Priority",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "AI akan urutkan tugasmu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight, "Buka",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProgressShortcutCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        ),
        shape = MaterialTheme.shapes.large,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(Spacing.normal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.normal)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Insights, "Statistik",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Progress",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Lihat statistik belajarmu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight, "Buka",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FocusSessionButton(durationMins: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary,
                ambientColor = MaterialTheme.colorScheme.primary
            ),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.horizontalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Spacing.normal).fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.normal)
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Timer, contentDescription = "Fokus", tint = Color.White)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Start Focus Session", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Pomodoro • $durationMins min work", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Mulai", tint = Color.White)
            }
        }
    }
}
