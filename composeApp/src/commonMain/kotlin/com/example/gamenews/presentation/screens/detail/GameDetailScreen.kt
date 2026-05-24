package com.example.gamenews.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameDetailScreen(
    gameId: Long,
    onNavigateBack: () -> Unit,
    viewModel: GameDetailViewModel = koinViewModel()
) {
    val game by viewModel.game.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val aiDescription by viewModel.aiDescription.collectAsState()
    val isGeneratingDescription by viewModel.isGeneratingDescription.collectAsState()
    val isWishlisted by viewModel.isWishlisted.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Game", fontWeight = FontWeight.Bold) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (game != null) {
                        IconButton(onClick = { viewModel.toggleWishlist() }) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) Color.Red else Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                game == null -> Text(
                    "Game tidak ditemukan",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
                else -> {
                    val g = game!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = g.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoChip(label = "Genre", value = g.genre)
                            InfoChip(label = "Rating", value = "⭐ ${g.rating}")
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            g.developer?.let {
                                InfoChip(label = "Developer", value = it)
                            }
                            g.releaseYear?.let {
                                InfoChip(label = "Tahun", value = it.toString())
                            }
                        }

                        Divider()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "About",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colors.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "✨ AI",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        when {
                            isGeneratingDescription -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Sedang membuat deskripsi dengan AI...",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                            aiDescription != null -> {
                                Text(
                                    text = aiDescription!!,
                                    style = MaterialTheme.typography.body1,
                                    color = Color.DarkGray,
                                    lineHeight = 24.sp
                                )
                            }
                            else -> {
                                Text(
                                    text = g.description.ifEmpty {
                                        "Deskripsi tidak tersedia."
                                    },
                                    style = MaterialTheme.typography.body1,
                                    color = Color.DarkGray,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Card(
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.caption, color = Color.Gray)
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 13.sp
            )
        }
    }
}