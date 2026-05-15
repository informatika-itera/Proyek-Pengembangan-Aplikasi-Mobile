package com.example.bridgebit.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bridgebit.presentation.components.EmptyState
import com.example.bridgebit.presentation.components.ErrorState
import com.example.bridgebit.presentation.components.LoadingIndicator
import com.example.bridgebit.presentation.components.NoteCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddNote: () -> Unit, // Ini akan kita pakai untuk buka Workspace
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BridgeBit Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToAI) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Context Assistant")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddNote) {
                Icon(Icons.Outlined.Translate, contentDescription = "Terjemahan Baru")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingIndicator()
                }
                is HomeUiState.Empty -> {
                    EmptyState(
                        title = "Belum Ada Riwayat",
                        message = "Mulai terjemahkan kata pertamamu sekarang!",
                        icon = {
                            Icon(
                                Icons.Outlined.Translate,
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
                is HomeUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Untuk sementara kita pakai NoteCard bawaan template
                        // Judul = Teks Asli, Content = Hasil Terjemahan
                        items(state.notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { onNavigateToDetail(note.id) },
                                onPinClick = { viewModel.togglePin(note.id) },
                                onDeleteClick = { viewModel.deleteNote(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}