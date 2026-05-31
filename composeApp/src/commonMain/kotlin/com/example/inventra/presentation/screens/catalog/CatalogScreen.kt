package com.example.inventra.presentation.screens.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.presentation.components.CategoryChip
import com.example.inventra.presentation.components.EmptyState
import com.example.inventra.presentation.components.InventRaBottomNav
import com.example.inventra.presentation.components.ItemCard
import com.example.inventra.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAddItem: () -> Unit
) {
    val viewModel: CatalogViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val categories = listOf(
        "Semua" to ItemCategory.ALL,
        "Medis" to ItemCategory.MEDICAL,
        "Elektronik" to ItemCategory.ELECTRONICS,
        "Bendera" to ItemCategory.FLAG,
        "Konsumsi" to ItemCategory.FOOD,
        "Lainnya" to ItemCategory.OTHER
    )

    var selectedCategoryLabel by remember { mutableStateOf("Semua") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Katalog Barang",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddItem,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        bottomBar = {
            InventRaBottomNav(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = when (val s = uiState) {
                    is CatalogUiState.Success -> s.query
                    else -> ""
                },
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Cari barang...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            )

            // Filter Chips kategori
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (label, category) ->
                    CategoryChip(
                        category = label,
                        isSelected = selectedCategoryLabel == label,
                        onClick = {
                            selectedCategoryLabel = label
                            viewModel.onCategorySelected(category)
                        }
                    )
                }
            }

            // Konten
            when (val state = uiState) {
                is CatalogUiState.Loading -> {
                    LoadingIndicator()
                }

                is CatalogUiState.Empty -> {
                    EmptyState(
                        title = "Tidak Ada Barang",
                        description = "Belum ada barang dalam kategori ini"
                    )
                }

                is CatalogUiState.Error -> {
                    EmptyState(
                        title = "Terjadi Kesalahan",
                        description = state.message
                    )
                }

                is CatalogUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
                    ) {
                        items(state.items) { item ->
                            ItemCard(
                                title = item.name,
                                category = item.category.displayName,
                                description = item.description,
                                stock = item.availableStock,
                                isAvailable = item.isBorrowable,
                                onClick = { onNavigateToDetail(item.name) },
                                onBorrowClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}