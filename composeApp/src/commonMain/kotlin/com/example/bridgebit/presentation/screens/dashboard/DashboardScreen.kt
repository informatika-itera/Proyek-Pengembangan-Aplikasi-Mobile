package com.example.bridgebit.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            FloatingActionButton(onClick = onNavigateToWorkspace) {
                Icon(Icons.Default.Add, contentDescription = "Terjemahan Baru")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is DashboardUiState.Loading -> CircularProgressIndicator()
                is DashboardUiState.Empty -> Text("Belum ada riwayat terjemahan.")
                is DashboardUiState.Success -> Text("Halaman Dashboard: Siap menampilkan list terjemahan!")
                is DashboardUiState.Error -> Text("Terjadi kesalahan memuat data.")
            }
        }
    }
}