package com.example.foodsaver.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.presentation.components.FoodItemCard
import com.example.foodsaver.presentation.theme.DangerRed
import com.example.foodsaver.presentation.theme.WarningOrange
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddFoodClick: () -> Unit,
    onFoodClick: (Long) -> Unit,
    onAIClick: () -> Unit,
    onMealPlannerClick: () -> Unit,
    onRecommendClick: (List<String>) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isMultiSelectMode = state.selectedIds.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("FoodSaver Inventory", fontWeight = FontWeight.Bold)
                        if (!isMultiSelectMode) {
                            Text(
                                "Kelola stok makananmu sebelum kedaluwarsa",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    if (isMultiSelectMode) {
                        IconButton(onClick = viewModel::clearSelection) {
                            Icon(Icons.Default.Close, contentDescription = "Batal")
                        }
                    } else {
                        IconButton(onClick = onMealPlannerClick) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Meal Planner")
                        }
                        IconButton(onClick = onAIClick) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isMultiSelectMode) {
                FloatingActionButton(
                    onClick = onAddFoodClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Makanan")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari makanan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            if (!isMultiSelectMode && state.items.isNotEmpty()) {
                SummaryCards(state)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.error != null) {
                    ErrorState(
                        message = state.error ?: "Terjadi kesalahan database. Mohon Rebuild atau Reinstall aplikasi.", 
                        onRetry = { viewModel.loadItems() }
                    )
                } else if (state.items.isEmpty()) {
                    EmptyState(onAddFoodClick)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.filteredItems, key = { it.id }) { item ->
                            val isSelected = state.selectedIds.contains(item.id)
                            
                            FoodItemCard(
                                name = item.name,
                                expiryDate = item.expiryDate.toString().substringBefore("T"),
                                quantity = "${item.quantity} ${item.unit}",
                                category = item.category,
                                status = item.getStatus(),
                                daysRemaining = item.getDaysRemaining(),
                                modifier = Modifier
                                    .clickable { 
                                        if (isMultiSelectMode) {
                                            viewModel.toggleSelection(item.id)
                                        } else {
                                            onFoodClick(item.id)
                                        }
                                    }
                                    .then(
                                        if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        else Modifier
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCards(state: HomeUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryItem(
            label = "Total",
            count = state.totalItems.toString(),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        SummaryItem(
            label = "Hampir Expired",
            count = state.nearlyExpiredCount.toString(),
            color = WarningOrange,
            modifier = Modifier.weight(1f)
        )
        SummaryItem(
            label = "Expired",
            count = state.expiredCount.toString(),
            color = DangerRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryItem(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
fun EmptyState(onAddFoodClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Inventory2,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Belum ada makanan tersimpan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tambahkan makanan pertama kamu agar FoodSaver bisa membantu mengingatkan tanggal kedaluwarsa.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddFoodClick) {
            Text("Tambah Makanan")
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message, 
            color = MaterialTheme.colorScheme.error,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Coba Lagi")
        }
    }
}
