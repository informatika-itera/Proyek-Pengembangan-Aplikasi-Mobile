package com.example.foodsaver.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.presentation.components.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddFoodClick: () -> Unit,
    onFoodClick: (Long) -> Unit,
    onCalendarClick: () -> Unit,
    onAIClick: () -> Unit,
    onCookFromStockClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val categories = remember { listOf("Semua") + FoodItem.CATEGORIES }

    Scaffold(
        modifier = Modifier.testTag("home_screen"),
        topBar = {
            HomeTopBar(
                onAIClick = onAIClick,
                onCalendarClick = onCalendarClick
            )
        },
        floatingActionButton = {
            HomeFab(onClick = onAddFoodClick)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HomeSearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            CategoryFilterRow(
                categories = categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = viewModel::onCategoryChange
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center), 
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    state.error != null -> {
                        ErrorState(
                            message = state.error ?: "Waduh, ada kendala teknis. Coba lagi yuk!", 
                            onRetry = { viewModel.loadItems() }
                        )
                    }
                    state.items.isEmpty() && state.searchQuery.isEmpty() -> {
                        EmptyFoodState(onActionClick = onAddFoodClick)
                    }
                    else -> {
                        HomeContentList(
                            state = state,
                            onFoodClick = onFoodClick,
                            onDeleteFood = viewModel::deleteItem,
                            onCookFromStockClick = onCookFromStockClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onAIClick: () -> Unit, onCalendarClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Stok Makananmu", 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 20.sp, 
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Yuk cek apa yang harus segera dimasak!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(onClick = onAIClick, modifier = Modifier.testTag("btn_ai_assistant")) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = "Asisten AI", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onCalendarClick, modifier = Modifier.testTag("btn_calendar_nav")) {
                Icon(Icons.Outlined.Event, contentDescription = "Kalender", tint = MaterialTheme.colorScheme.primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
private fun HomeFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("add_food_fab")
    ) {
        Icon(Icons.Default.Add, contentDescription = "Tambah Makanan")
    }
}

@Composable
private fun HomeSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("search_field"),
        placeholder = { Text("Cari bahan, kategori, atau lokasi...") },
        leadingIcon = { 
            Icon(
                Icons.Default.Search, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary
            ) 
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .testTag("category_filter_row"),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("chip_$category"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun HomeContentList(
    state: HomeUiState,
    onFoodClick: (Long) -> Unit,
    onDeleteFood: (Long) -> Unit,
    onCookFromStockClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("home_food_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (state.urgentReminders.isNotEmpty() && state.searchQuery.isEmpty() && state.selectedCategory == "Semua") {
            item {
                UrgentReminderBanner(
                    reminders = state.urgentReminders,
                    onFoodClick = onFoodClick
                )
            }
        }

        if (state.searchQuery.isEmpty() && state.selectedCategory == "Semua") {
            item { 
                InventorySummarySection(
                    totalCount = state.totalItems,
                    safeCount = state.safeCount,
                    nearlyExpiredCount = state.nearlyExpiredCount,
                    expiredCount = state.expiredCount
                ) 
            }

            item {
                PrioritySection(
                    priorityItems = state.priorityItems,
                    onFoodClick = onFoodClick
                )
            }

            item { RecipeShortcutCard(onClick = onCookFromStockClick) }

            item { AITipsSection(items = state.activeItems) }
        }

        item {
            val headerTitle = when {
                state.searchQuery.isNotEmpty() -> "Hasil pencarianmu"
                state.selectedCategory != "Semua" -> "Koleksi ${state.selectedCategory}"
                else -> "Daftar Stok Makanan"
            }
            Text(
                headerTitle,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (state.filteredItems.isEmpty()) {
            item {
                EmptySearchState()
            }
        } else {
            items(state.filteredItems, key = { it.id }) { item ->
                SwipeableFoodItem(
                    item = item,
                    onDelete = { onDeleteFood(it) },
                    onClick = { onFoodClick(it) }
                )
            }
        }
    }
}

@Composable
private fun PrioritySection(
    priorityItems: List<FoodItem>,
    onFoodClick: (Long) -> Unit
) {
    Column {
        Text(
            "Segera Masak! 🔥",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        if (priorityItems.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 8.dp).testTag("priority_list")
            ) {
                items(priorityItems, key = { it.id }) { item ->
                    PriorityCard(item = item, onClick = { onFoodClick(item.id) })
                }
            }
        } else {
            AllSafePlaceholder()
        }
    }
}

@Composable
private fun AllSafePlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Hebat! Stok makananmu masih aman semua.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp).testTag("empty_search_result"), 
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Wah, yang kamu cari tidak ketemu nih", 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
