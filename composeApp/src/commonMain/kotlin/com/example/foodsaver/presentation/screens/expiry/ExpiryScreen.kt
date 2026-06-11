package com.example.foodsaver.presentation.screens.expiry

import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.presentation.components.FoodItemCard
import com.example.foodsaver.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpiryScreen(
    onFoodClick: (Long) -> Unit,
    onAddFoodClick: () -> Unit = {},
    viewModel: ExpiryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf("Semua", "Segera", "Kadaluwarsa")

    Scaffold(
        modifier = Modifier.testTag("expiry_screen"),
        topBar = {
            ExpiryTopBar()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ExpirySummarySection(
                nearlyExpiredCount = state.nearlyExpiredCount,
                expiredCount = state.expiredCount
            )

            ExpiryTabRow(
                tabs = tabs,
                selectedTab = state.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    state.filteredItems.isEmpty() -> {
                        EmptyExpiryState(state.selectedTab, onAddFoodClick)
                    }
                    else -> {
                        ExpiryFoodList(
                            items = state.filteredItems,
                            onFoodClick = onFoodClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpiryTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Status Kedaluwarsa",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    "Pantau bahan yang harus segera digunakan.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun ExpirySummarySection(nearlyExpiredCount: Int, expiredCount: Int) {
    val totalUrgent = nearlyExpiredCount + expiredCount
    val isDark = isSystemInDarkTheme()
    
    val bgColor = if (totalUrgent > 0) {
        if (isDark) WarningBgDark else WarningBgLight
    } else {
        if (isDark) SafeBgDark else SafeBgLight
    }
    
    val contentColor = if (totalUrgent > 0) {
        if (isDark) WarningTextDark else WarningTextLight
    } else {
        if (isDark) SafeTextDark else SafeTextLight
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("expiry_summary_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (totalUrgent > 0) Icons.Default.Info else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (totalUrgent > 0) "$totalUrgent Stok Perlu Segera" else "Semua Stok Aman",
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor,
                    fontSize = 16.sp
                )
                Text(
                    text = if (totalUrgent > 0) 
                        "$nearlyExpiredCount masuk kategori segera, $expiredCount sudah kedaluwarsa."
                        else "Bagus! Tidak ada makanan yang akan segera mubazir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ExpiryTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) },
        indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier.testTag("expiry_tab_row")
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier.testTag("expiry_tab_$index"),
                text = {
                    Text(
                        title,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

@Composable
private fun ExpiryFoodList(
    items: List<FoodItem>,
    onFoodClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("expiry_food_list"),
        contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items, key = { it.id }) { item ->
            FoodItemCard(
                item = item,
                onClick = { onFoodClick(item.id) },
                modifier = Modifier.testTag("food_card_${item.id}")
            )
        }
    }
}

@Composable
private fun EmptyExpiryState(tabIndex: Int, onAddFoodClick: () -> Unit) {
    val (message, subMessage, icon) = when (tabIndex) {
        1 -> Triple(
            "Tidak ada stok mendesak 🎉",
            "Semua stok makananmu masih dalam kondisi aman.",
            Icons.Default.CheckCircle
        )
        2 -> Triple(
            "Tidak ada makanan kedaluwarsa",
            "Bagus! Kamu mengelola makanan dengan sangat baik.",
            Icons.Default.CheckCircle
        )
        else -> Triple(
            "Belum ada data makanan",
            "Tambahkan stok makananmu di halaman Beranda.",
            Icons.Default.Info
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).testTag("empty_state"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        
        if (tabIndex == 0) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAddFoodClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_empty_add_food")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Makanan", fontWeight = FontWeight.Bold)
            }
        }
    }
}
