package com.example.todomaster.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todomaster.presentation.components.TaskItem
import com.example.todomaster.presentation.theme.ColorDelegate
import com.example.todomaster.presentation.theme.ColorDoFirst
import com.example.todomaster.presentation.theme.ColorDontDo
import com.example.todomaster.presentation.theme.ColorSchedule
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    onNavigateToQuadrantDetail: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredTasks by viewModel.filteredTasks.collectAsState()

    val maxDoFirstQuota = 5
    val currentDoFirstCount = uiState.doFirstTasks.filter { !it.isCompleted }.size
    val quotaProgress = if (maxDoFirstQuota > 0) currentDoFirstCount.toFloat() / maxDoFirstQuota else 0f

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val dateString = "${today.dayOfMonth}/${today.monthNumber}/${today.year}"

    Scaffold(
        floatingActionButton = {
            if (currentTab != 2) {
                LargeFloatingActionButton(
                    onClick = onNavigateToAddTask,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Tugas", modifier = Modifier.size(32.dp))
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = (currentTab == 0),
                    onClick = { viewModel.changeTab(0) },
                    icon = { Icon(Icons.Default.GridView, contentDescription = "Matriks") },
                    label = { Text("Matriks") }
                )
                NavigationBarItem(
                    selected = (currentTab == 1),
                    onClick = { viewModel.changeTab(1) },
                    icon = { Icon(Icons.Default.List, contentDescription = "Semua") },
                    label = { Text("Semua") }
                )
                NavigationBarItem(
                    selected = (currentTab == 2),
                    onClick = { viewModel.changeTab(2) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Pengaturan") },
                    label = { Text("Pengaturan") }
                )
            }
        }
    ) { paddingValues ->

        when (currentTab) {
            0 -> {
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
                            color = ColorDoFirst,
                            trackColor = ColorDoFirst.copy(alpha = 0.2f),
                            strokeCap = StrokeCap.Round,
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }

                    item {
                        val allTasks = uiState.doFirstTasks + uiState.scheduleTasks + uiState.delegateTasks + uiState.dontDoTasks
                        val tasksDueToday = allTasks.filter { !it.isCompleted && isDueToday(it.dueDate) }

                        if (tasksDueToday.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Peringatan Deadline",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${tasksDueToday.size} tugas jatuh tempo hari ini",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = tasksDueToday.joinToString(", ") { it.title },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                QuadrantCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Do first",
                                    activeCount = uiState.doFirstTasks.count { !it.isCompleted },
                                    totalCount = uiState.doFirstTasks.size,
                                    dueTodayCount = uiState.doFirstTasks.count { !it.isCompleted && isDueToday(it.dueDate) },
                                    color = ColorDoFirst,
                                    onClick = { onNavigateToQuadrantDetail(1L) }
                                )
                                QuadrantCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Schedule",
                                    activeCount = uiState.scheduleTasks.count { !it.isCompleted },
                                    totalCount = uiState.scheduleTasks.size,
                                    dueTodayCount = uiState.scheduleTasks.count { !it.isCompleted && isDueToday(it.dueDate) },
                                    color = ColorSchedule,
                                    onClick = { onNavigateToQuadrantDetail(2L) }
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                QuadrantCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Delegate",
                                    activeCount = uiState.delegateTasks.count { !it.isCompleted },
                                    totalCount = uiState.delegateTasks.size,
                                    dueTodayCount = uiState.delegateTasks.count { !it.isCompleted && isDueToday(it.dueDate) },
                                    color = ColorDelegate,
                                    onClick = { onNavigateToQuadrantDetail(3L) }
                                )
                                QuadrantCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Don't do",
                                    activeCount = uiState.dontDoTasks.count { !it.isCompleted },
                                    totalCount = uiState.dontDoTasks.size,
                                    dueTodayCount = uiState.dontDoTasks.count { !it.isCompleted && isDueToday(it.dueDate) },
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
            1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Semua Tugas Kuliah",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Cari tugas berdasarkan judul/deskripsi...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                }
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (filteredTasks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "Belum ada tugas yang dicatat." else "Tidak ada tugas kuliah yang cocok.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredTasks) { task ->
                                TaskItem(
                                    task = task,
                                    onClick = { onNavigateToTaskDetail(task.id) },
                                    onToggleComplete = { viewModel.toggleTaskCompletion(task) }
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pengaturan Profil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Mode Gelap (Dark Theme)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Ubah tema aplikasi secara manual",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = com.example.todomaster.ThemeConfig.isDarkTheme,
                                onCheckedChange = { isChecked ->
                                    com.example.todomaster.ThemeConfig.isDarkTheme = isChecked
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Aplikasi Dikembangkan Oleh Kelompok:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Cikal (123140109) & Ragil (123140128)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun QuadrantCard(
    title: String,
    activeCount: Int,
    totalCount: Int,
    dueTodayCount: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
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

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = activeCount.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp,
                        color = color
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "tugas",
                        style = MaterialTheme.typography.bodySmall,
                        color = color.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                if (dueTodayCount > 0) {
                    Text(
                        text = "$dueTodayCount due hari ini",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "tidak ada deadline",
                        style = MaterialTheme.typography.labelSmall,
                        color = color.copy(alpha = 0.6f)
                    )
                }
            }

            val progress = if (totalCount > 0) ((totalCount - activeCount).toFloat() / totalCount.toFloat()) else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

fun isDueToday(dueDateMillis: Long?): Boolean {
    if (dueDateMillis == null) return false
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dueDate = Instant.fromEpochMilliseconds(dueDateMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
    return today == dueDate
}