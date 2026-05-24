package com.example.gamenews.presentation.screens.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gamenews.presentation.screens.home.GameItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WishlistScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: WishlistViewModel = koinViewModel()
) {
    val games by viewModel.wishlistGames.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWishlist()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wishlist Offline", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (games.isEmpty()) {
                Text(
                    text = "Belum ada game di wishlist permanen",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(games) { game ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            GameItem(
                                game = game,
                                onClick = { game.id?.let { onNavigateToDetail(it.toLong()) } }
                            )
                        }
                    }
                }
            }
        }
    }
}