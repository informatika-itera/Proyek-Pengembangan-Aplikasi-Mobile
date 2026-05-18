package com.kosthub.app.presentation.screens.contribute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.KostManageCard
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.UiState

@Composable
fun ContributeScreen(
    uiState: UiState<List<Kost>>,
    onAddKost: () -> Unit,
    onUpdateKost: (Kost) -> Unit,
    onDeleteKost: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Kontribusi Kost", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Di sprint berikutnya data akan datang dari API. Saat ini, kontribusi menyimpan data lokal.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onAddKost) {
            Text(text = "Tambah Kost Baru")
        }
        Spacer(modifier = Modifier.height(12.dp))
        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = uiState.message)
            is UiState.Empty -> EmptyState(text = "Belum ada kost yang kamu tambahkan")
            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.data) { kost ->
                        KostManageCard(
                            kost = kost,
                            onEdit = onUpdateKost,
                            onDelete = onDeleteKost
                        )
                    }
                }
            }
        }
    }
}
