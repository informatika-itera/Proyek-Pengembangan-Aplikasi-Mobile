package com.studyhub.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyhub.domain.model.TaskStatus
import com.studyhub.presentation.components.*
import com.studyhub.presentation.screens.notification.NotifHistoryViewModel
import com.studyhub.presentation.theme.*
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToSmartPriority: () -> Unit,
    onNavigateToNotifHistory: () -> Unit,
    onNavigateToProgress: () -> Unit
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val scrollState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    fun getGreeting(): String {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..14 -> "Good afternoon"
            in 15..20 -> "Good evening"
            else -> "Good night"
        }
    }

    fun getGreetingEmoji(): String {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when (hour) {
            in 5..11 -> "☀️"
            in 12..14 -> "🌤️"
            in 15..20 -> "🌆"
            else -> "🌙"
        }
    }

    val greeting = remember { getGreeting() }
    val emoji = remember { getGreetingEmoji() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Error -> {
                    EmptyStateView(
                        message = state.message,
                        actionLabel = "Coba Lagi",
                        onAction = { viewModel.loadUnreadCount() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is HomeUiState.Success -> {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        // Header
                        item {
                            StudyHubHeader(
                                title = "$greeting, ${state.userName}! $emoji",
                                subtitle = {
                                    Text("You have ${state.todayTasksCount} tasks for today", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                actions = {
                                    GlassIconButton(
                                        icon = Icons.Default.Notifications,
                                        onClick = onNavigateToNotifHistory,
                                        hasNotification = state.unreadNotifCount > 0,
                                        contentDescription = "Riwayat Notifikasi"
                                    )
                                }
                            )
                        }

                        // Progress Overview
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Progress Overview", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold)
                                    TextButton(onClick = onNavigateToProgress) {
                                        Text("View Details", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                                
                                Spacer(Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatBox(Modifier.weight(1f), "Total", state.totalTasks.toString(), Icons.AutoMirrored.Filled.Assignment, Color(0xFFF1EBE0), Color(0xFF8B7355))
                                    StatBox(Modifier.weight(1f), "Done", state.doneTasks.toString(), Icons.Default.CheckCircle, Color(0xFFE1F5EE), Color(0xFF10B981))
                                    StatBox(Modifier.weight(1f), "Active", state.activeTasks.toString(), Icons.AutoMirrored.Filled.TrendingUp, Color(0xFFE6F1FB), Color(0xFF3B82F6))
                                    StatBox(Modifier.weight(1f), "Due Today", state.dueTodayTasks.toString(), Icons.Default.Error, Color(0xFFFEECEC), Color(0xFFE24B4A))
                                }
                                
                                Spacer(Modifier.height(20.dp))
                                
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(
                                                progress = { state.completionPercentage / 100f },
                                                modifier = Modifier.size(54.dp),
                                                strokeWidth = 6.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = Color(0xFFF2EDE4)
                                            )
                                            Text("${state.completionPercentage}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        }
                                        Column {
                                            Text("Keep it up!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Text("You've completed ${state.doneTasks} tasks this week", style = MaterialTheme.typography.bodySmall, color = Color(0xFF888888))
                                        }
                                    }
                                }
                            }
                        }

                        // Smart Priority AI
                        item {
                            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                LiquidGlassCard(
                                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToSmartPriority() },
                                    cornerRadius = 24.dp
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(48.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.AutoAwesome, "Prioritas AI", tint = Color.White, modifier = Modifier.size(24.dp))
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Smart Priority AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("AI recommends which task to do first", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                                        }
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Buka", tint = Color.White.copy(alpha = 0.7f))
                                    }
                                }
                            }
                        }
                        
                        item { Spacer(Modifier.height(16.dp)) }

                        // Progress Shortcut
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.normal),
                                onClick = onNavigateToProgress,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                                            Icons.Default.Insights, "Statistik Progress",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(
                                                "Progress",
                                                style = MaterialTheme.typography.titleSmall
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

                        // Upcoming Tasks
                        item {
                            Column(modifier = Modifier.padding(top = 32.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Upcoming Tasks", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold)
                                    TextButton(onClick = onNavigateToTasks) {
                                        Text("View All", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }

                        if (state.upcomingTasks.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.AssignmentTurnedIn, "Tugas kosong", modifier = Modifier.size(32.dp), tint = Color(0xFFBBBBBB))
                                        Spacer(Modifier.height(12.dp))
                                        Text("No upcoming tasks", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFAAAAAA))
                                    }
                                }
                            }
                        } else {
                            items(
                                items = state.upcomingTasks,
                                key = { it.id },
                                contentType = { "task" }
                            ) { task ->
                                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                                    TaskCard(
                                        task = task,
                                        onEdit = { onNavigateToTaskDetail(task.id) },
                                        onDelete = { viewModel.deleteTask(task.id) },
                                        onStatusChange = { newStatus ->
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.updateStatus(task.id, newStatus)
                                        },
                                        onClick = { onNavigateToTaskDetail(task.id) }
                                    )
                                }
                            }
                        }

                        // Subject Stats
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                                Text("Subjects", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(16.dp))
                                
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(end = 20.dp)
                                ) {
                                    items(state.subjectStats, key = { it.name }) { stat ->
                                        Card(
                                            modifier = Modifier.width(140.dp),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Box(
                                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(stat.color.copy(alpha = 0.1f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(stat.name.take(1).uppercase(), style = MaterialTheme.typography.titleMedium, color = stat.color, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(Modifier.height(12.dp))
                                                Text(stat.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                                                Text("${stat.doneCount}/${stat.totalCount} Done", style = MaterialTheme.typography.labelSmall, color = Color(0xFF888888))
                                                
                                                Spacer(Modifier.height(12.dp))
                                                LinearProgressIndicator(
                                                    progress = { stat.completionRate / 100f },
                                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                                    color = stat.color,
                                                    trackColor = Color(0xFFF2EDE4)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(modifier: Modifier, label: String, value: String, icon: ImageVector, bgColor: Color, iconColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, label, tint = iconColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = iconColor)
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = iconColor.copy(alpha = 0.7f))
        }
    }
}
