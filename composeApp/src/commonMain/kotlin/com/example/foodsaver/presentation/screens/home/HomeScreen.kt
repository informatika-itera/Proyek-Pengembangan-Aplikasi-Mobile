package com.example.foodsaver.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.presentation.components.AITipsSection
import com.example.foodsaver.presentation.components.FoodItemCard
import com.example.foodsaver.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddFoodClick: () -> Unit,
    onFoodClick: (Long) -> Unit,
    onCalendarClick: () -> Unit,
    onAIClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("FoodSaver Inventory", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(
                            "Kelola stok makananmu sebelum kedaluwarsa",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCalendarClick) {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendar", tint = PrimaryGreen)
                    }
                    IconButton(onClick = onAIClick) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Assistant", tint = PrimaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFoodClick,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Makanan")
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryGreen)
                } else if (state.error != null) {
                    ErrorState(
                        message = state.error ?: "Terjadi kesalahan. Silakan coba lagi.", 
                        onRetry = { viewModel.loadItems() }
                    )
                } else if (state.items.isEmpty()) {
                    EmptyState(onAddFoodClick)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Summary Cards
                        item {
                            SummarySection(state)
                        }

                        // AI Tips Card
                        if (state.searchQuery.isEmpty()) {
                            item {
                                AITipsSection(items = state.items)
                            }
                        }

                        // Priority Section
                        if (state.priorityItems.isNotEmpty() && state.searchQuery.isEmpty()) {
                            item {
                                Text(
                                    "Save Before Waste 🔥",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    items(state.priorityItems) { item ->
                                        PriorityCard(item = item, onClick = { onFoodClick(item.id) })
                                    }
                                }
                            }
                        }

                        // All Food List
                        item {
                            Text(
                                if (state.searchQuery.isEmpty()) "Semua Makanan" else "Hasil Pencarian",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                        }

                        items(state.filteredItems, key = { it.id }) { item ->
                            FoodItemCard(
                                item = item,
                                modifier = Modifier.clickable { onFoodClick(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Cari nama, kategori, atau lokasi...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen) },
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CardWhite,
            unfocusedContainerColor = CardWhite,
            focusedIndicatorColor = PrimaryGreen,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun SummarySection(state: HomeUiState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryItem(
                label = "Total",
                count = state.totalItems.toString(),
                color = TextMain,
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                label = "Aman",
                count = state.safeCount.toString(),
                color = PrimaryGreen,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryItem(
                label = "Hampir Expired",
                count = state.nearlyExpiredCount.toString(),
                color = WarningOrange,
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                label = "Expired",
                count = state.expiredCount.toString(),
                color = ExpiredRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryItem(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun PriorityCard(item: com.example.foodsaver.domain.model.FoodItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (item.getStatus() == com.example.foodsaver.domain.model.FoodStatus.EXPIRED) ExpiredRed.copy(alpha = 0.1f) else WarningOrange.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(com.example.foodsaver.presentation.components.getEmojiForCategory(item.category), fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
            Text(item.getStatusLabel(), style = MaterialTheme.typography.labelSmall, color = if (item.getStatus() == com.example.foodsaver.domain.model.FoodStatus.EXPIRED) ExpiredRed else WarningOrange)
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
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(32.dp),
            color = PrimaryGreen.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = PrimaryGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Belum ada makanan tersimpan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tambahkan makanan pertama kamu agar FoodSaver bisa membantu mengingatkan tanggal kedaluwarsa.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddFoodClick,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text("Tambah Makanan Pertama")
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = ExpiredRed)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message, 
            color = ExpiredRed,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = ExpiredRed)) {
            Text("Coba Lagi")
        }
    }
}
