package com.example.todomaster.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todomaster.presentation.components.TaskItem
import com.example.todomaster.presentation.theme.ColorDelegate
import com.example.todomaster.presentation.theme.ColorDoFirst
import com.example.todomaster.presentation.theme.ColorDontDo
import com.example.todomaster.presentation.theme.ColorSchedule
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    onNavigateToQuadrantDetail: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val maxDoFirstQuota = 5
    val currentDoFirstCount = uiState.doFirstTasks.filter { !it.isCompleted }.size
    val quotaProgress = if (maxDoFirstQuota > 0) currentDoFirstCount.toFloat() / maxDoFirstQuota else 0f

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val dateString = "${today.dayOfMonth}/${today.monthNumber}/${today.year}"

    Scaffold(
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas", modifier = Modifier.size(32.dp))
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Aktif */ },
                    icon = { Icon(Icons.Default.GridView, contentDescription = "Matriks") },
                    label = { Text("Matriks") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO: Pindah ke Layar Semua (Sprint Berikutnya) */ },
                    icon = { Icon(Icons.Default.List, contentDescription = "Semua") },
                    label = { Text("Semua") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO: Pindah ke Layar Pengaturan */ },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Pengaturan") },
                    label = { Text("Pengaturan") }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TodoMaster",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { /* TODO: Notifikasi */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifikasi")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kuota Do first", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("$currentDoFirstCount / $maxDoFirstQuota", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { quotaProgress },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuadrantCard(
                            modifier = Modifier.weight(1f),
                            title = "Do first",
                            count = uiState.doFirstTasks.size,
                            color = ColorDoFirst,
                            onClick = { onNavigateToQuadrantDetail(1L) }
                        )
                        QuadrantCard(
                            modifier = Modifier.weight(1f),
                            title = "Schedule",
                            count = uiState.scheduleTasks.size,
                            color = ColorSchedule,
                            onClick = { onNavigateToQuadrantDetail(2L) }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuadrantCard(
                            modifier = Modifier.weight(1f),
                            title = "Delegate",
                            count = uiState.delegateTasks.size,
                            color = ColorDelegate,
                            onClick = { onNavigateToQuadrantDetail(3L) }
                        )
                        QuadrantCard(
                            modifier = Modifier.weight(1f),
                            title = "Don't do",
                            count = uiState.dontDoTasks.size,
                            color = ColorDontDo,
                            onClick = { onNavigateToQuadrantDetail(4L) }
                        )
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Hari ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(uiState.doFirstTasks) { task ->
                TaskItem(
                    task = task,
                    onClick = { onNavigateToTaskDetail(task.id) },
                    onToggleComplete = { viewModel.toggleTaskCompletion(task) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                if (uiState.doFirstTasks.isEmpty()) {
                    Text(
                        text = "Belum ada tugas prioritas hari ini.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuadrantCard(
    title: String,
    count: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "tugas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            LinearProgressIndicator(
                progress = { if (count > 0) 0.6f else 0f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}