package com.example.bridgebit.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("BridgeBit History") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToWorkspace) {
                Icon(Icons.Default.Add, contentDescription = "Terjemahan Baru")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                placeholder = { Text("Cari kata atau frasa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)

                val filters = listOf("Semua", "Vault", "Indonesia", "Inggris")
                filters.forEach { filter ->
                    FilterChip(
                        selected = activeFilter == filter,
                        onClick = { viewModel.onFilterChange(filter) },
                        label = { Text(filter) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                when (state) {
                    is DashboardUiState.Loading -> CircularProgressIndicator()
                    is DashboardUiState.Empty -> Text(
                        text = if (searchQuery.isNotBlank()) "Pencarian tidak ditemukan" else "Belum ada riwayat terjemahan.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    is DashboardUiState.Success -> {
                        val historyList = (state as DashboardUiState.Success).history
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = historyList, key = { it.id }) { item ->
                                TranslationCard(
                                    translation = item,
                                    onClick = { onNavigateToDetail(item.id) },
                                    onVaultClick = { viewModel.toggleVaultStatus(item.id) }, // <-- SUDAH DISAMBUNGKAN
                                    onDeleteClick = { viewModel.deleteTranslation(item.id) },
                                    modifier = Modifier.animateItem()
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