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
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.components.AITipsSection
import com.example.foodsaver.presentation.components.FoodItemCard
import com.example.foodsaver.presentation.components.getEmojiForCategory
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
                        Text("FoodSaver Inventory", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextMain)
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
                                AITipsSection(items = state.activeItems)
                            }
                        }

                        // Priority Section (Save Before Waste)
                        if (state.searchQuery.isEmpty()) {
                            item {
                                Text(
                                    "Save Before Waste 🔥",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                                
                                if (state.priorityItems.isNotEmpty()) {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    ) {
                                        items(state.priorityItems) { item ->
                                            PriorityCard(item = item, onClick = { onFoodClick(item.id) })
                                        }
                                    }
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = SafeBg.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                "Tidak ada makanan yang perlu segera dikonsumsi.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = DarkGreen
                                            )
                                        }
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

                        if (state.filteredItems.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Tidak ada makanan ditemukan", color = TextSecondary)
                                }
                            }
                        } else {
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
                icon = Icons.Outlined.Inventory,
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                label = "Aman",
                count = state.safeCount.toString(),
                color = PrimaryGreen,
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryItem(
                label = "Hampir",
                count = state.nearlyExpiredCount.toString(),
                color = WarningOrange,
                icon = Icons.Outlined.Warning,
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                label = "Expired",
                count = state.expiredCount.toString(),
                color = ExpiredRed,
                icon = Icons.Outlined.ErrorOutline,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryItem(label: String, count: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1)
            }
        }
    }
}

@Composable
fun PriorityCard(item: FoodItem, onClick: () -> Unit) {
    val status = item.getStatus()
    val statusColor = when (status) {
        FoodStatus.EXPIRED, FoodStatus.EXPIRED_TODAY -> ExpiredRed
        else -> WarningOrange
    }
    val bgColor = when (status) {
        FoodStatus.EXPIRED, FoodStatus.EXPIRED_TODAY -> ExpiredBg
        else -> WarningBg
    }

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.5f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(getEmojiForCategory(item.category), fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    item.category, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = statusColor.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                item.name, 
                fontWeight = FontWeight.Bold, 
                maxLines = 1, 
                style = MaterialTheme.typography.bodyMedium,
                color = TextMain
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                item.getStatusLabel(), 
                style = MaterialTheme.typography.labelSmall, 
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
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
            Text("Tambah Makanan Pertama", fontWeight = FontWeight.Bold)
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
            color = TextMain,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = ExpiredRed)) {
            Text("Coba Lagi", fontWeight = FontWeight.Bold)
        }
    }
}
