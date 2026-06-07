package com.example.gamenews.presentation.screens.ai

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gamenews.domain.model.Game
import com.example.gamenews.presentation.navigation.SheetContent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AIRecommendationSheet(
    content: SheetContent,
    wishlistGames: List<Game>,
    availableGenres: List<String>,
    onSelectWishlist: () -> Unit,
    onSelectGenre: () -> Unit,
    onSelectCustom: () -> Unit,
    onGenreChange: (String) -> Unit,
    onCustomInputChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val viewModel: AIRecommendationViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        when (content) {
            is SheetContent.Menu -> {
                Text(
                    text = "✨ Rekomendasi AI",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Apakah kamu mau rekomendasi game yang lain?",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.reset(); onSelectWishlist() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("❤️ Rekomendasikan game sesuai wishlist saya")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.reset(); onSelectGenre() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🎮 Rekomendasikan game dengan genre")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.reset(); onSelectCustom() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🔍 Cari game sesuai keinginan saya")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal", color = Color.Gray)
                }
            }

            is SheetContent.WishlistResult -> {
                Text(
                    text = "❤️ Rekomendasi dari Wishlist",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (wishlistGames.isEmpty()) {
                    Text(
                        text = "Wishlist kamu masih kosong. Tambahkan game dulu!",
                        color = Color.Gray,
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Tutup", color = Color.Gray)
                    }
                } else {
                    if (uiState.result == null && !uiState.isLoading) {
                        LaunchedEffect(Unit) {
                            viewModel.recommendByWishlist(wishlistGames)
                        }
                    }
                    AIResultContent(uiState = uiState, onDismiss = onDismiss)
                }
            }

            is SheetContent.GenreInput -> {
                Text(
                    text = "🎮 Rekomendasi by Genre",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 3 genre chip teratas
                val topGenres = availableGenres.take(3)
                if (topGenres.isNotEmpty()) {
                    Text(
                        text = "Genre populer:",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        topGenres.forEach { genre ->
                            FilterChip(
                                selected = content.genre == genre,
                                onClick = { onGenreChange(genre) },
                                colors = ChipDefaults.filterChipColors(
                                    selectedBackgroundColor = MaterialTheme.colors.primary,
                                    selectedContentColor = Color.White
                                )
                            ) {
                                Text(genre, style = MaterialTheme.typography.caption)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Search bar genre
                OutlinedTextField(
                    value = content.genre,
                    onValueChange = onGenreChange,
                    label = { Text("Atau ketik genre") },
                    placeholder = { Text("Contoh: RPG, casual, shooter...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Saran genre jika tidak exact match
                val genreQuery = content.genre.trim()
                val exactMatch = availableGenres.any {
                    it.equals(genreQuery, ignoreCase = true)
                }
                val similarGenres = if (genreQuery.isNotBlank() && !exactMatch) {
                    availableGenres.filter {
                        it.contains(genreQuery, ignoreCase = true)
                    }
                } else emptyList()

                if (genreQuery.isNotBlank() && !exactMatch) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (similarGenres.isNotEmpty()) {
                        Text(
                            text = "Maksud kamu:",
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            similarGenres.forEach { genre ->
                                OutlinedButton(
                                    onClick = { onGenreChange(genre) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(genre, style = MaterialTheme.typography.caption)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Genre yang kamu tulis tidak ada, coba cari yang lain.",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.recommendByGenre(content.genre) },
                    enabled = content.genre.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cari Rekomendasi")
                }
                Spacer(modifier = Modifier.height(8.dp))
                AIResultContent(uiState = uiState, onDismiss = onDismiss)
            }

            is SheetContent.CustomInput -> {
                Text(
                    text = "🔍 Cari Game Custom",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = content.text,
                    onValueChange = onCustomInputChange,
                    label = { Text("Deskripsikan game yang kamu mau") },
                    placeholder = { Text("Contoh: game shooter open world, game santai sambil rebahan...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.recommendByCustomInput(content.text) },
                    enabled = content.text.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cari Rekomendasi")
                }
                Spacer(modifier = Modifier.height(8.dp))
                AIResultContent(uiState = uiState, onDismiss = onDismiss)
            }
        }
    }
}

@Composable
private fun AIResultContent(
    uiState: AIRecommendationUiState,
    onDismiss: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("AI sedang berpikir...", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        uiState.error != null -> {
            Spacer(modifier = Modifier.height(8.dp))
            Text(uiState.error, color = MaterialTheme.colors.error)
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Tutup", color = Color.Gray)
            }
        }
        uiState.result != null -> {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                shape = RoundedCornerShape(12.dp),
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.05f),
                elevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    Text(
                        text = uiState.result,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Tutup", color = Color.Gray)
            }
        }
    }
}