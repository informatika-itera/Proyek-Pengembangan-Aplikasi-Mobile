package com.example.musickeep.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musickeep.domain.model.Music
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddMusic: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var musicToDelete by remember { mutableStateOf<Music?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Katalog Musik") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddMusic) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari lagu atau artis...") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.genres) { genre ->
                    FilterChip(
                        selected = uiState.selectedGenre == genre,
                        onClick = { viewModel.onGenreSelect(genre) },
                        label = { Text(genre) }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.musicList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tidak ada musik ditemukan", style = MaterialTheme.typography.bodyLarge)
                        Text("Coba ubah filter atau tambah lagu baru", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.musicList) { music ->
                        MusicItem(
                            music = music,
                            onClick = { music.id?.let { onNavigateToDetail(it) } },
                            onDelete = { musicToDelete = music }
                        )
                    }
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus (UI Polish - Edge Case)
    if (musicToDelete != null) {
        AlertDialog(
            onDismissRequest = { musicToDelete = null },
            title = { Text("Hapus Lagu?") },
            text = { Text("Apakah kamu yakin ingin menghapus '${musicToDelete?.title}' dari katalog?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        musicToDelete?.id?.let { 
                            viewModel.deleteMusic(it)
                            scope.launch {
                                snackbarHostState.showSnackbar("Lagu berhasil dihapus")
                            }
                        }
                        musicToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { musicToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun MusicItem(
    music: Music,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(music.title) },
        supportingContent = { Text("${music.artist} • ${music.genre ?: "Tanpa Genre"}") },
        leadingContent = { Icon(Icons.Default.MusicNote, contentDescription = null) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    )
}
