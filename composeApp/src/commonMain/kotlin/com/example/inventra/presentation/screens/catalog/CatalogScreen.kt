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
import com.example.inventra.core.localization.AppStrings
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.presentation.components.CategoryChip
import com.example.inventra.presentation.components.EmptyState
import com.example.inventra.presentation.components.InventRaBottomNav
import com.example.inventra.presentation.components.ItemCard
import com.example.inventra.presentation.components.LoadingIndicator
import com.example.inventra.presentation.util.getDisplayName
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddItem: () -> Unit
) {
    val viewModel: CatalogViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == com.example.inventra.domain.model.UserRole.ADMIN
    val strings = AppStrings.current

    val categories = listOf(
        strings.catAll to ItemCategory.ALL,
        strings.catMedical to ItemCategory.MEDICAL,
        strings.catElectronics to ItemCategory.ELECTRONICS,
        strings.catFlag to ItemCategory.FLAG,
        strings.catFood to ItemCategory.FOOD,
        strings.catOther to ItemCategory.OTHER
    )

    var selectedCategoryLabel by remember { mutableStateOf(strings.catAll) }
    
    // Update label if language changes or state changes
    val currentSelectedCategory = (uiState as? CatalogUiState.Success)?.selectedCategory ?: ItemCategory.ALL
    LaunchedEffect(strings, currentSelectedCategory) {
        selectedCategoryLabel = categories.find { it.second == currentSelectedCategory }?.first ?: strings.catAll
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.catalog,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onNavigateToAddItem,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        },
        bottomBar = {
            InventRaBottomNav(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text(strings.searchItem) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            )

            // Filter Chips
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

            when (val state = uiState) {
                is CatalogUiState.Loading -> LoadingIndicator()

                is CatalogUiState.Empty -> {
                    EmptyState(
                        title = strings.noItemFound,
                        description = strings.noItemInKategory
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
                                category = item.category.getDisplayName(strings),
                                description = item.description,
                                stock = item.availableStock,
                                isAvailable = item.isBorrowable,
                                imageUrl = item.imageUrl,
                                onClick = {
                                    onNavigateToDetail(item.id)
                                },
                                onBorrowClick = {
                                    onNavigateToDetail(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
