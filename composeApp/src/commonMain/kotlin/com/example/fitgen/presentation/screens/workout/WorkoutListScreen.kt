package com.example.fitgen.presentation.screens.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.SportsGymnastics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.usecase.WorkoutSortBy
import com.example.fitgen.presentation.components.EmptyState
import com.example.fitgen.presentation.components.ErrorState
import com.example.fitgen.presentation.components.LoadingIndicator
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToApiExerciseDetail: (name: String, bodyPart: String, gifUrl: String, instructions: String) -> Unit,
    viewModel: WorkoutListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val apiSearchResults by viewModel.apiSearchResults.collectAsStateWithLifecycle()
    val customRoutines by viewModel.customRoutines.collectAsStateWithLifecycle()
    val currentSortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val timeFilter by viewModel.timeFilter.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Riwayat Latihan", "Rutinitas Custom")
    var showCreateRoutineDialog by remember { mutableStateOf(false) }
    var newRoutineName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                title = {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sesi Latihan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.padding(bottom = 20.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Sort,
                                contentDescription = "Urutkan"
                            )
                        }
                    }
                    WorkoutSortDropdownMenu(
                        expanded = showSortMenu,
                        currentSortBy = currentSortBy,
                        onSortSelected = {
                            viewModel.onSortByChanged(it)
                            showSortMenu = false
                        },
                        onDismiss = { showSortMenu = false }
                    )
                }
            )
        },
        floatingActionButton = {
            if (searchQuery.isBlank()) {
                FloatingActionButton(
                    onClick = {
                        if (selectedTabIndex == 0) {
                            onNavigateToAddWorkout()
                        } else {
                            showCreateRoutineDialog = true
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // -- Search Bar --
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                placeholder = { Text("Cari sesi atau gerakan...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(50),
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF800000),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            if (searchQuery.isNotBlank()) {
                // Tampilkan hasil pencarian API
                Text(
                    text = "Hasil Pencarian API (Global)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apiSearchResults) { exercise ->
                        Card(
                            onClick = {
                                onNavigateToApiExerciseDetail(
                                    exercise.name ?: "Unknown",
                                    exercise.bodyPart ?: "",
                                    exercise.gifUrl ?: "",
                                    exercise.instructions?.joinToString("\n") ?: ""
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(exercise.name ?: "Unknown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(exercise.bodyPart ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else {
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = Color(0xFF800000)
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    title, 
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) Color(0xFF800000) else MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            }
                        )
                    }
                }

                if (selectedTabIndex == 0) {
                    // -- Pill Tabs (Time Filter) --
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WorkoutTimeFilter.entries.forEach { filter ->
                            val isSelected = timeFilter == filter
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) Color(0xFF800000) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                onClick = { viewModel.onTimeFilterChanged(filter) },
                                modifier = Modifier.height(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = filter.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val state = uiState) {
                            is WorkoutListUiState.Loading -> LoadingIndicator()
                            is WorkoutListUiState.Empty -> {
                                EmptyState(
                                    title = "Belum Ada Sesi Latihan",
                                    message = "Tap + untuk mencatat sesi latihanmu",
                                    icon = { Icon(Icons.Outlined.FitnessCenter, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) }
                                )
                            }
                            is WorkoutListUiState.Success -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    item { SummaryStatsCard(stats = state.summaryStats) }
                                    if (state.isEmptyResult) {
                                        item { Text("Tidak ada sesi yang cocok dengan filter", modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    } else {
                                        itemsIndexed(items = state.workouts, key = { _, workout -> workout.id }) { index, workout ->
                                            Column {
                                                AnimatedVisibility(visible = true, enter = fadeIn(tween(200 + index * 40)) + slideInVertically(tween(200 + index * 40)) { it / 2 }) {
                                                    WorkoutLogCard(workout = workout, onClick = { onNavigateToDetail(workout.id) }, onDeleteClick = { viewModel.deleteWorkout(workout.id) })
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            is WorkoutListUiState.Error -> {
                                ErrorState(message = state.message, onRetry = { viewModel.onSortByChanged(WorkoutSortBy.TANGGAL_TERBARU) })
                            }
                        }
                    }
                } else {
                    // Rutinitas Custom Tab
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (customRoutines.isEmpty()) {
                            EmptyState(
                                title = "Belum Ada Rutinitas Custom",
                                message = "Cari gerakan di kolom pencarian, lalu tambahkan ke Rutinitas Baru",
                                icon = { Icon(Icons.Outlined.FitnessCenter, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) }
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(customRoutines) { routine ->
                                    var expanded by remember { mutableStateOf(false) }

                                    Card(
                                        onClick = { expanded = !expanded },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text(routine.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                                IconButton(onClick = { viewModel.deleteCustomRoutine(routine.id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                            Text("${routine.exercises.size} Gerakan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            
                                            AnimatedVisibility(visible = expanded) {
                                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                                                    routine.exercises.forEach { exercise ->
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(exercise.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                                                Text(exercise.bodyPart, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            }
                                                            IconButton(
                                                                onClick = { viewModel.removeExerciseFromRoutine(exercise.id) },
                                                                modifier = Modifier.size(32.dp)
                                                            ) {
                                                                Icon(Icons.Default.Close, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
                                                            }
                                                        }
                                                    }
                                                    if (routine.exercises.isEmpty()) {
                                                        Text("Belum ada gerakan. Tambahkan dari pencarian API!", style = MaterialTheme.typography.labelMedium, fontStyle = FontStyle.Italic)
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

        if (showCreateRoutineDialog) {
            AlertDialog(
                onDismissRequest = { showCreateRoutineDialog = false },
                title = { Text("Buat Rutinitas Baru") },
                text = {
                    OutlinedTextField(
                        value = newRoutineName,
                        onValueChange = { newRoutineName = it },
                        label = { Text("Nama Rutinitas (cth: Latihan Pagi)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newRoutineName.isNotBlank()) {
                                viewModel.createCustomRoutine(newRoutineName)
                                newRoutineName = ""
                                showCreateRoutineDialog = false
                            }
                        }
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    Button(onClick = { showCreateRoutineDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SummaryStatsCard(stats: WorkoutSummaryStats) {
    // Desain lebih clean tanpa background ngejreng
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatColumnClean(value = "${stats.totalSessions}", label = "Sesi")
        Box(modifier = Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outlineVariant))
        StatColumnClean(value = "${kotlin.math.round(stats.totalVolume * 10) / 10.0}", label = "Vol (kg)")
        Box(modifier = Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outlineVariant))
        StatColumnClean(value = "${kotlin.math.round(stats.avgExercises * 10) / 10.0}", label = "Rata Gerakan")
    }
}

@Composable
private fun StatColumnClean(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WorkoutLogCard(
    workout: WorkoutLog,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ── Header: tanggal + tombol delete ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTanggal(workout.tanggal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // ── Stats chips ──
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip(
                    icon = Icons.Default.FitnessCenter,
                    label = "${workout.jumlahGerakan} gerakan"
                )
                StatChip(
                    icon = Icons.Outlined.SportsGymnastics,
                    label = "${workout.totalSets} sets"
                )
                if (workout.totalVolume > 0.0) {
                    StatChip(
                        icon = Icons.Default.FitnessCenter,
                        label = "${kotlin.math.round(workout.totalVolume * 10) / 10.0} kg"
                    )
                }
            }

            // ── Daftar gerakan (preview 3 teratas) ──
            if (workout.gerakan.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                workout.gerakan.take(3).forEach { exercise ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = exercise.nama,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = exercise.ringkasan,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                if (workout.gerakan.size > 3) {
                    Text(
                        text = "+${workout.gerakan.size - 3} gerakan lainnya",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 4.dp, start = 14.dp)
                    )
                }
            }

            // ── Catatan (jika ada) ──
            if (workout.catatan.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = workout.catatan,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun WorkoutSortDropdownMenu(
    expanded: Boolean,
    currentSortBy: WorkoutSortBy,
    onSortSelected: (WorkoutSortBy) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        WorkoutSortBy.entries.forEach { sortBy ->
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sortBy.displayName)
                        if (sortBy == currentSortBy) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                onClick = { onSortSelected(sortBy) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun formatTanggal(date: LocalDate): String {
    val bulan = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    )
    return "${date.dayOfMonth} ${bulan[date.monthNumber - 1]} ${date.year}"
}
