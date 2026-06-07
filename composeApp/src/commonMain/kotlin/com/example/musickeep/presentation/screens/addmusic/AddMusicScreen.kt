package com.example.musickeep.presentation.screens.addmusic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMusicScreen(
    musicId: Long? = null,
    onBack: () -> Unit,
    viewModel: AddMusicViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(musicId) {
        if (musicId != null) {
            viewModel.loadMusic(musicId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (musicId == null) "Tambah Lagu" else "Edit Lagu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Judul Lagu") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.artist,
                onValueChange = viewModel::onArtistChange,
                label = { Text("Nama Artis") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Pilih Genre", style = MaterialTheme.typography.titleSmall)
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.presetGenres) { genre ->
                    FilterChip(
                        selected = uiState.genre == genre && !uiState.isCustomGenre,
                        onClick = { viewModel.onGenreSelect(genre) },
                        label = { Text(genre) }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.isCustomGenre,
                        onClick = { viewModel.onGenreSelect("Lainnya") },
                        label = { Text("Lainnya") }
                    )
                }
            }

            if (uiState.isCustomGenre) {
                OutlinedTextField(
                    value = uiState.genre,
                    onValueChange = viewModel::onCustomGenreChange,
                    label = { Text("Kategori Kustom") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ketik kategori di sini...") },
                    singleLine = true
                )
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::saveMusic,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.title.isNotBlank() && uiState.artist.isNotBlank() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (musicId == null) "Simpan ke Katalog" else "Perbarui Katalog")
                }
            }
        }
    }

    if (uiState.isSaved) {
        onBack()
    }
}
