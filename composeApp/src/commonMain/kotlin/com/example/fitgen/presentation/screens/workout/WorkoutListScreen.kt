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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.SportsGymnastics
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
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
    viewModel: WorkoutListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sesi Latihan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            Icons.Outlined.Sort,
                            contentDescription = "Urutkan"
                        )
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
            FloatingActionButton(
                onClick = onNavigateToAddWorkout,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Tambah Sesi Latihan",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is WorkoutListUiState.Loading -> {
                    LoadingIndicator()
                }

                is WorkoutListUiState.Success -> {
                    WorkoutLogList(
                        workouts = state.workouts,
                        onItemClick = onNavigateToDetail,
                        onDeleteClick = viewModel::deleteWorkout
                    )
                }

                is WorkoutListUiState.Empty -> {
                    EmptyState(
                        title = "Belum Ada Sesi Latihan",
                        message = "Tap + untuk mencatat sesi latihanmu",
                        icon = {
                            Icon(
                                Icons.Outlined.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        }
                    )
                }

                is WorkoutListUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.onSortByChanged(WorkoutSortBy.TANGGAL_TERBARU) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WorkoutLogList(
    workouts: List<WorkoutLog>,
    onItemClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = workouts,
            key = { _, workout -> workout.id }
        ) { index, workout ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(200 + index * 40)) +
                        slideInVertically(tween(200 + index * 40)) { it / 2 }
            ) {
                WorkoutLogCard(
                    workout = workout,
                    onClick = { onItemClick(workout.id) },
                    onDeleteClick = { onDeleteClick(workout.id) }
                )
            }
        }
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
                        label = "${"%.1f".format(workout.totalVolume)} kg"
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
