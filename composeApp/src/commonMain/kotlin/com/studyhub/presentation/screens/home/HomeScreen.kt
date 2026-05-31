package com.studyhub.presentation.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.SystemAppearance
import com.studyhub.presentation.components.GlassIconButton
import com.studyhub.presentation.components.LiquidGlassCard
import com.studyhub.presentation.components.TaskCard
import com.studyhub.presentation.components.StudyHubHeader
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.screens.home.components.FocusTimerBottomSheet
import com.studyhub.presentation.screens.task.AddEditTaskBottomSheet
import com.studyhub.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollState = rememberLazyListState()

    var showFocusTimer by remember { mutableStateOf(false) }
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            item {
                StudyHubHeader(
                    title = "Good morning, ${uiState.userName}! 👋",
                    dateText = "Sunday, May 17",
                    subtitle = {
                        Row {
                            Text("You have ", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                            Text(
                                "${uiState.todayTasksCount} tasks",
                                color = HighlightGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(" to tackle today", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        GlassIconButton(
                            icon = Icons.Outlined.Notifications,
                            onClick = { /* Notifications */ },
                            hasNotification = true
                        )
                    },
                    content = {
                        LiquidGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderAlpha = 0.3f
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Canvas Progress Ring
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(54.dp)) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawArc(
                                            color = Color.White.copy(alpha = 0.15f),
                                            startAngle = 0f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                        drawArc(
                                            color = Color(0xFFFBBF24),
                                            startAngle = -90f,
                                            sweepAngle = (uiState.completionPercentage / 100f) * 360f,
                                            useCenter = false,
                                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }
                                    Text(
                                        "${uiState.completionPercentage}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Overall Progress",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { uiState.completionPercentage / 100f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                        color = Color(0xFFFBBF24),
                                        trackColor = Color.White.copy(alpha = 0.2f)
                                    )
                                    Text(
                                        "${uiState.doneTasks} of ${uiState.totalTasks} tasks completed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                )
            }

            // Stat Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBox(Modifier.weight(1f), "Total", uiState.totalTasks.toString(), Icons.Default.Book, Color(0xFFF1EBE0), Color(0xFF8B7355))
                    StatBox(Modifier.weight(1f), "Done", uiState.doneTasks.toString(), Icons.Default.CheckCircle, Color(0xFFE1F5EE), Color(0xFF10B981))
                    StatBox(Modifier.weight(1f), "Active", uiState.activeTasks.toString(), Icons.Default.Schedule, Color(0xFFE6F1FB), Color(0xFF3B82F6))
                    StatBox(Modifier.weight(1f), "Due Today", uiState.dueTodayTasks.toString(), Icons.Default.Bolt, Color(0xFFFAEEDA), Color(0xFFFBBF24))
                }
            }

            // Start Focus Session Button
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    FocusSessionButton(
                        durationMins = uiState.pomodoroWorkDuration,
                        onClick = { showFocusTimer = true }
                    )
                }
            }

            // Upcoming Tasks Section
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Upcoming Tasks",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { navController.navigate(Screen.Tasks.route) }) {
                            Text("See all", color = GoldenSuedeDark)
                        }
                    }
                    
                    if (uiState.upcomingTasks.isEmpty()) {
                        Text(
                            "No upcoming tasks today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.upcomingTasks.forEach { task ->
                                TaskCard(
                                    task = task,
                                    onEdit = { 
                                        editingTaskId = task.id
                                        showAddBottomSheet = true 
                                    },
                                    onDelete = { viewModel.deleteTask(task.id) },
                                    onClick = { navController.navigate(Screen.TaskDetail.createRoute(task.id)) }
                                )
                            }
                        }
                    }
                }
            }

            // Subject Progress Section
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Subject Progress",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DarkText
                        )
                        TextButton(onClick = { /* Analytics */ }) {
                            Text("Analytics", color = GoldenSuedeDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            uiState.subjectStats.forEach { stat ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(stat.color)
                                    )
                                    Text(
                                        stat.name, 
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        modifier = Modifier.width(80.dp),
                                        color = DarkText
                                    )
                                    LinearProgressIndicator(
                                        progress = { stat.completionRate / 100f },
                                        modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                                        color = stat.color,
                                        trackColor = Color(0xFFF2EDE4)
                                    )
                                    Text(
                                        "${stat.doneCount}/${stat.totalCount}", 
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = DarkText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Smart Priority Shortcut
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.normal),
                    onClick = {
                        navController.navigate(Screen.SmartPriority.route)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.normal),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome, null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    "Smart Priority",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    "AI akan urutkan tugasmu",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            Icons.Default.ChevronRight, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Streak Card
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = HighlightGold.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = HighlightGold.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🔥", fontSize = 20.sp)
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("7-Day Study Streak!", fontWeight = FontWeight.Bold, color = GoldenSuedeDark)
                                Text("Keep it up — you're on a roll!", style = MaterialTheme.typography.bodySmall, color = GoldenSuedeDark.copy(alpha = 0.8f))
                            }
                            Icon(Icons.Default.AutoGraph, null, tint = Color(0xFFE85D35))
                        }
                    }
                }
            }
        }
    }

    if (showFocusTimer) {
        FocusTimerBottomSheet(
            initialWorkMinutes = uiState.pomodoroWorkDuration,
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
}

@Composable
fun StatBox(modifier: Modifier, label: String, value: String, icon: ImageVector, bgColor: Color, iconColor: Color) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(10.dp),
                color = bgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                value, 
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp), 
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2416)
            )
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF888888))
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
                spotColor = Color(0xFFE85D35),
                ambientColor = Color(0xFFE85D35)
            ),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFF6B4A), Color(0xFFE85D35))
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Timer, null, tint = Color.White)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Start Focus Session", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Pomodoro • $durationMins min work", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color.White)
            }
        }
    }
}
