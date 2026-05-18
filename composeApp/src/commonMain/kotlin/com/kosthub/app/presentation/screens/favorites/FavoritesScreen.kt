package com.kosthub.app.presentation.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.KostCard
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.UiState

@Composable
fun FavoritesScreen(
    uiState: UiState<List<Kost>>,
    onNavigateDetail: (Long) -> Unit,
    onToggleFavorite: (Kost) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Favorit Saya", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = uiState.message)
            is UiState.Empty -> EmptyState(text = "Belum ada data kost")
            is UiState.Success -> {
                val favorites = uiState.data.filter { it.isFavorite }
                if (favorites.isEmpty()) {
                    EmptyState(text = "Belum ada kost favorit")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(favorites) { kost ->
                            KostCard(
                                kost = kost,
                                onClick = { onNavigateDetail(kost.id) },
                                onToggleFavorite = onToggleFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}
