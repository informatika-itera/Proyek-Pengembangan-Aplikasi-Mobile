package com.kosthub.app.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.KostCard
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.components.SearchBar
import com.kosthub.app.presentation.state.UiState

@Composable
fun HomeScreen(
    uiState: UiState<List<Kost>>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedTipeKos: String?,
    onTipeKosChange: (String?) -> Unit,
    onNavigateDetail: (Long) -> Unit,
    onToggleFavorite: (Kost) -> Unit
) {
    val daftarTipe = listOf("Campur", "Perempuan", "Laki-laki")

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        SearchBar(query = searchQuery, onQueryChange = onQueryChange)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedTipeKos == null,
                    onClick = { onTipeKosChange(null) },
                    label = { Text("Semua") }
                )
            }
            items(daftarTipe) { tipe ->
                FilterChip(
                    selected = selectedTipeKos == tipe,
                    onClick = { onTipeKosChange(tipe) },
                    label = { Text(tipe) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = uiState.message)
            is UiState.Empty -> EmptyState(text = "Belum ada data kost")
            is UiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.data) { kost ->
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
