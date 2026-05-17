package com.example.Feelia.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.usecase.NoteSortBy
import com.example.Feelia.presentation.components.EmotionBadge
import com.example.Feelia.presentation.components.EmptyState
import com.example.Feelia.presentation.components.ErrorState
import com.example.Feelia.presentation.components.LoadingIndicator
import com.example.Feelia.presentation.components.NoteCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddNote: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        SearchField(
                            query = when (val state = uiState) {
                                is HomeUiState.Success -> state.query
                                is HomeUiState.Empty -> state.query
                                else -> ""
                            },
                            onQueryChange = viewModel::onSearchQueryChange,
                            onClear = {
                                viewModel.clearSearch()
                                showSearch = false
                            }
                        )
                    } else {
                        Text("Feelia")
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Cari")
                        }

                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Outlined.Sort, contentDescription = "Urutkan")
                        }

                        SortDropdownMenu(
                            expanded = showSortMenu,
                            currentSortBy = currentSortBy,
                            onSortSelected = {
                                viewModel.onSortByChanged(it)
                                showSortMenu = false
                            },
                            onDismiss = { showSortMenu = false }
                        )
                    }

                    IconButton(onClick = onNavigateToAI) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Assistant")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Jurnal")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // DIPERBAIKI: pakai emotion dari HomeUiState yang sudah diupdate
            EmotionFilterRow(
                selectedEmotion = when (val state = uiState) {
                    is HomeUiState.Success -> state.emotion
                    is HomeUiState.Empty -> state.emotion
                    else -> null
                },
                onEmotionSelected = viewModel::onEmotionSelected
            )

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingIndicator()
                }

                is HomeUiState.Success -> {
                    NotesList(
                        notes = state.notes,
                        onNoteClick = onNavigateToDetail,
                        onPinClick = viewModel::togglePin,
                        onDeleteClick = viewModel::deleteNote
                    )
                }

                is HomeUiState.Empty -> {
                    EmptyState(
                        title = if (state.query.isNotBlank() || state.emotion != null) {
                            "Tidak Ditemukan"
                        } else {
                            "Belum Ada Jurnal"
                        },
                        message = if (state.query.isNotBlank() || state.emotion != null) {
                            "Coba ubah kata kunci atau filter emosi"
                        } else {
                            "Tap + untuk menulis jurnal pertamamu 🌸"
                        },
                        icon = {
                            Icon(
                                Icons.Outlined.NoteAlt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        }
                    )
                }

                is HomeUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.clearSearch() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari jurnal...") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus")
                }
            }
        }
    )
}

@Composable
private fun SortDropdownMenu(
    expanded: Boolean,
    currentSortBy: NoteSortBy,
    onSortSelected: (NoteSortBy) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        NoteSortBy.entries.forEach { sortBy ->
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sortBy.displayName)
                        if (sortBy == currentSortBy) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                onClick = { onSortSelected(sortBy) }
            )
        }
    }
}

// DIPERBAIKI: CategoryFilterRow → EmotionFilterRow, pakai Emotion enum bukan NoteCategory
@Composable
private fun EmotionFilterRow(
    selectedEmotion: Emotion?,
    onEmotionSelected: (Emotion?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedEmotion == null,
                onClick = { onEmotionSelected(null) },
                label = { Text("Semua") }
            )
        }
        items(Emotion.entries) { emotion ->
            FilterChip(
                selected = selectedEmotion == emotion,
                onClick = {
                    onEmotionSelected(if (selectedEmotion == emotion) null else emotion)
                },
                label = { Text("${emotion.emoji} ${emotion.displayName}") }
            )
        }
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onPinClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = notes,
            key = { it.id }
        ) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note.id) },
                onPinClick = { onPinClick(note.id) },
                onDeleteClick = { onDeleteClick(note.id) }
            )
        }
    }
}