package com.example.inventra.presentation.screens.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.presentation.components.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: CatalogViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog Barang", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = when (val state = uiState) {
                    is CatalogUiState.Success -> state.query
                    else -> ""
                },
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari barang...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ItemCategory.entries) { category ->
                    val isSelected = when (val state = uiState) {
                        is CatalogUiState.Success -> state.selectedCategory == category
                        else -> category == ItemCategory.ALL
                    }
                    CategoryChip(
                        label = category.displayName,
                        selected = isSelected,
                        onClick = { viewModel.onCategorySelected(category) }
                    )
                }
            }

            when (val state = uiState) {
                is CatalogUiState.Loading -> LoadingIndicator()
                is CatalogUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items) { item ->
                            ItemCard(
                                item = item,
                                onClick = { onNavigateToDetail(item.id) }
                            )
                        }
                    }
                }
                is CatalogUiState.Empty -> {
                    EmptyState(
                        title = "Barang tidak ditemukan",
                        message = "Coba cari dengan kata kunci lain"
                    )
                }
                is CatalogUiState.Error -> {
                    ErrorState(message = state.message, onRetry = {})
                }
            }
        }
    }
}
