package com.example.foodsaver.presentation.screens.expiry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.presentation.components.FoodItemCard
import com.example.foodsaver.presentation.theme.BackgroundLight
import com.example.foodsaver.presentation.theme.PrimaryGreen
import com.example.foodsaver.presentation.theme.TextMain
import com.example.foodsaver.presentation.theme.TextSecondary
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
                title = { Text("Expiry Alert", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = BackgroundLight,
                contentColor = PrimaryGreen,
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
                                fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal
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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
fun EmptyExpiryState(tabIndex: Int) {
    val message = when (tabIndex) {
        1 -> "Wah, semua makananmu masih segar! Tidak ada yang hampir expired."
        2 -> "Hebat! Tidak ada makanan yang terbuang (expired)."
        else -> "Belum ada data makanan."
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.WarningAmber,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = PrimaryGreen.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
