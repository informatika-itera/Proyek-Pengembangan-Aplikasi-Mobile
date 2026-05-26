package com.example.foodsaver.presentation.screens.expiry

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.presentation.components.FoodItemCard
import com.example.foodsaver.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpiryScreen(
    onFoodClick: (Long) -> Unit,
    viewModel: ExpiryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf("Semua", "Hampir Expired", "Expired")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expiry Alert", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextMain) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Summary Card
            ExpirySummaryCard(
                nearlyExpired = state.nearlyExpiredCount,
                expired = state.expiredCount
            )

            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = BackgroundLight,
                contentColor = PrimaryGreen,
                divider = {},
                indicator = { tabPositions ->
                    if (state.selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                            color = PrimaryGreen
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.onTabSelected(index) },
                        text = {
                            Text(
                                title,
                                fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (state.selectedTab == index) PrimaryGreen else TextSecondary
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryGreen)
                } else if (state.filteredItems.isEmpty()) {
                    EmptyExpiryState(state.selectedTab)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
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
fun ExpirySummaryCard(nearlyExpired: Int, expired: Int) {
    val totalUrgent = nearlyExpired + expired
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (totalUrgent > 0) WarningBg else SafeBg
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (totalUrgent > 0) Icons.Default.Info else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (totalUrgent > 0) WarningOrange else PrimaryGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (totalUrgent > 0) "$totalUrgent Makanan Perlu Perhatian" else "Semua Stok Aman",
                    fontWeight = FontWeight.Bold,
                    color = if (totalUrgent > 0) WarningOrange else PrimaryGreen,
                    fontSize = 16.sp
                )
                Text(
                    text = if (totalUrgent > 0) 
                        "$nearlyExpired hampir expired, $expired sudah expired." 
                        else "Tidak ada makanan yang akan segera kedaluwarsa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun EmptyExpiryState(tabIndex: Int) {
    val (message, subMessage, icon) = when (tabIndex) {
        1 -> Triple(
            "Tidak ada makanan hampir expired 🎉",
            "Semua stok makananmu masih aman.",
            Icons.Default.CheckCircle
        )
        2 -> Triple(
            "Tidak ada makanan expired",
            "Bagus! Kamu mengelola makanan dengan sangat baik.",
            Icons.Default.CheckCircle
        )
        else -> Triple(
            "Belum ada data makanan",
            "Tambahkan stok makananmu di halaman Home.",
            Icons.Default.Info
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = PrimaryGreen.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextMain,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
