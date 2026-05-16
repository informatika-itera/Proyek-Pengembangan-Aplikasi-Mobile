package com.example.bridgebit.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bridgebit.presentation.components.EmptyState
import com.example.bridgebit.presentation.components.ErrorState
import com.example.bridgebit.presentation.components.LoadingIndicator
import com.example.bridgebit.presentation.components.TranslationCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToWorkspace: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BridgeBit Dashboard") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWorkspace,
                containerColor = MaterialTheme.colorScheme.primary, // Warna aksen merah dominan
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = "Terjemahan Baru"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is DashboardUiState.Loading -> {
                    LoadingIndicator()
                }

                is DashboardUiState.Empty -> {
                    EmptyState(
                        title = "Belum Ada Riwayat",
                        message = "Mulai terjemahkan kata pertamamu sekarang!",
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Translate,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        }
                    )
                }

                is DashboardUiState.Error -> {
                    ErrorState(
                        message = currentState.message,
                        onRetry = { /* Aksi opsional untuk memuat ulang data */ }
                    )
                }

                is DashboardUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Menggunakan currentState.history yang sinkron dengan data class dari ViewModel
                        items(currentState.history, key = { it.id }) { translation ->
                            TranslationCard(
                                translation = translation,
                                onClick = { onNavigateToDetail(translation.id) },
                                onVaultClick = { /* Bagian Vault akan ditangani oleh rekan tim di Data Layer */ },
                                onDeleteClick = { viewModel.deleteTranslation(translation.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}