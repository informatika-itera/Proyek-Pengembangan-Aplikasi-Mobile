package id.pusakakata.ui.screens.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.pusakakata.ui.components.EmptyState
import id.pusakakata.ui.components.LoadingIndicator
import id.pusakakata.ui.components.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    onWordClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusaka Favorit ❤️", style = MaterialTheme.typography.headlineMedium) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is FavoriteUiState.Loading -> LoadingIndicator()
                is FavoriteUiState.Empty -> EmptyState(message = "Belum ada pusaka favorit. Tandai kata yang kamu sukai!")
                is FavoriteUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.words, key = { it.id }) { word ->
                            ItemCard(
                                word = word, 
                                onClick = { onWordClick(word.id) }, 
                                onDelete = { viewModel.deleteWord(word.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(word.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
