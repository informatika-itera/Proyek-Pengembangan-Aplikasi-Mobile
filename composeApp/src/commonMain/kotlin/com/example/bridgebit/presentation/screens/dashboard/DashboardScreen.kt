package com.example.bridgebit.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    // State UI untuk Simulasi Pencarian dan Filter
    var searchText by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("Semua") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BridgeBit History") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToWorkspace) {
                Icon(Icons.Default.Add, contentDescription = "Terjemahan Baru")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 🔎 FITUR NYARI (Search Bar Skeleton)
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                placeholder = { Text("Cari kata atau frasa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // 🎛️ FITUR FILTER UI (Bahasa & Tema)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)

                InputChip(
                    selected = activeFilter == "Semua",
                    onClick = { activeFilter = "Semua" },
                    label = { Text("Semua") }
                )
                InputChip(
                    selected = activeFilter == "Bahasa",
                    onClick = { activeFilter = "Bahasa" },
                    label = { Text("Bahasa") }
                )
                InputChip(
                    selected = activeFilter == "Tema",
                    onClick = { activeFilter = "Tema" },
                    label = { Text("Tema") }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            // Konten Riwayat List
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is DashboardUiState.Loading -> CircularProgressIndicator()
                    is DashboardUiState.Empty -> Text("Belum ada riwayat terjemahan.")
                    is DashboardUiState.Success -> {
                        val historyList = (state as DashboardUiState.Success).history
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(historyList) { item ->
                                // Menggunakan Komponen Custom yang sudah dibuat
                                com.example.bridgebit.presentation.components.TranslationCard(
                                    translation = item,
                                    onClick = { onNavigateToDetail(item.id) },
                                    onVaultClick = { /* Nanti ditambahkan UseCase Toggle Vault */ },
                                    onDeleteClick = { viewModel.deleteTranslation(item.id) } // Delete UI terhubung!
                                )
                            }
                        }
                    }
                    is DashboardUiState.Error -> Text("Terjadi kesalahan memuat data.")
                }
            }
        }
    }
}