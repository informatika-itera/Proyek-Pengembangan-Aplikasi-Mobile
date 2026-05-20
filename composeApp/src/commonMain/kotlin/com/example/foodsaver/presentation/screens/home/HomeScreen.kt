package com.example.foodsaver.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.presentation.components.FoodItemCard
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
                    if (isMultiSelectMode) {
                        Text("${state.selectedIds.size} dipilih")
                    } else {
                        Text("FoodSaver Inventory") 
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
                FloatingActionButton(onClick = onAddFoodClick) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Makanan")
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isMultiSelectMode,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Gunakan bahan terpilih?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = {
                                val selectedNames = state.items
                                    .filter { it.id in state.selectedIds }
                                    .map { it.name }
                                onRecommendClick(selectedNames)
                                viewModel.clearSelection()
                            }
                        ) {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Dapatkan Resep")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error ?: "Terjadi kesalahan",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (state.items.isEmpty()) {
                Text(
                    text = "Belum ada data makanan.\nKlik + untuk menambah.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        val isSelected = state.selectedIds.contains(item.id)
                        
                        Box {
                            FoodItemCard(
                                name = item.name,
                                expiryDate = item.expiryDate.toString().substringBefore("T"),
                                quantity = "${item.quantity} ${item.unit}",
                                modifier = Modifier
                                    .clickable { 
                                        if (isMultiSelectMode) {
                                            viewModel.toggleSelection(item.id)
                                        } else {
                                            onFoodClick(item.id)
                                        }
                                    }
                                    .then(
                                        if (isSelected) Modifier.background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                                        else Modifier
                                    )
                            )
                            
                            if (isMultiSelectMode) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { viewModel.toggleSelection(item.id) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                )
                            } else {
                                IconButton(
                                    onClick = { viewModel.toggleSelection(item.id) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircleOutline,
                                        contentDescription = "Pilih",
                                        tint = Color.Gray.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
