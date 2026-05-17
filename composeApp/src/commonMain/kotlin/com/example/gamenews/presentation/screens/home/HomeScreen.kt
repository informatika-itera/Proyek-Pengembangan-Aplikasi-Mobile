package com.example.gamenews.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gamenews.domain.model.Game
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val games by viewModel.games.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val availableGenres by viewModel.availableGenres.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GameBrain News", fontWeight = FontWeight.Bold) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                elevation = 4.dp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari game...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (availableGenres.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GenreChip(
                        genre = "Semua",
                        isSelected = selectedGenre == null,
                        onClick = { viewModel.onGenreSelected(null) }
                    )
                    availableGenres.forEach { genre ->
                        GenreChip(
                            genre = genre,
                            isSelected = selectedGenre == genre,
                            onClick = { viewModel.onGenreSelected(genre) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Mengambil data game...", color = Color.Gray)
                        }
                    }
                    games.isEmpty() -> {
                        Text(
                            text = "Tidak ada game ditemukan",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(games) { game ->
                                GameItem(game = game)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenreChip(genre: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colors.primary
        else MaterialTheme.colors.surface,
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = genre,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isSelected) Color.White else MaterialTheme.colors.primary,
            style = MaterialTheme.typography.caption,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun GameItem(game: Game) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = game.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = game.genre,
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )
                Text(
                    text = "⭐ ${game.rating}",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium
                )
            }
            if (game.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.caption,
                    maxLines = 2,
                    color = Color.Gray
                )
            }
        }
    }
}