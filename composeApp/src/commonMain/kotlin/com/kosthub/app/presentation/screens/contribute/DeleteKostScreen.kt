package com.kosthub.app.presentation.screens.contribute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import kotlinx.coroutines.delay

@Composable
fun DeleteKostScreen(
    kostId: Long,
    uiState: UiState<List<Kost>>,
    operationState: OperationState,
    onClearOperation: () -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit
) {
    val kost = if (uiState is UiState.Success) {
        uiState.data.firstOrNull { it.id == kostId }
    } else {
        null
    }

    LaunchedEffect(operationState) {
        if (operationState is OperationState.Success) {
            delay(1000)
            onClearOperation()
            onBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Hapus Kost", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = uiState.message)
            is UiState.Empty -> EmptyState(text = "Belum ada data kost")
            is UiState.Success -> {
                if (kost == null) {
                    EmptyState(text = "Kost tidak ditemukan")
                } else {
                    Text(
                        text = "Yakin ingin menghapus ${kost.namaKos}?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val isLoading = operationState is OperationState.Loading
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onDelete(kost.id) }, enabled = !isLoading) {
                            Text(text = "Hapus")
                        }
                        Button(onClick = onBack, enabled = !isLoading) {
                            Text(text = "Batal")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when (operationState) {
                        is OperationState.Loading -> Text(
                            text = "Menghapus...",
                            style = MaterialTheme.typography.bodySmall
                        )
                        is OperationState.Success -> Text(
                            text = operationState.message,
                            style = MaterialTheme.typography.bodySmall
                        )
                        is OperationState.Error -> Text(
                            text = operationState.message,
                            style = MaterialTheme.typography.bodySmall
                        )
                        OperationState.Idle -> Unit
                    }
                }
            }
        }
    }
}
