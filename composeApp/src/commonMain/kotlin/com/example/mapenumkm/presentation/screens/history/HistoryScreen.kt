package com.example.mapenumkm.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapenumkm.presentation.screens.home.DashboardBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProduct: () -> Unit,
    onNavigateToTransaksi: () -> Unit,
    onNavigateToLaporan: () -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Semua", "Hari ini", "Minggu ini", "Bulan ini")
    
    val greenPrimary = Color(0xFF16A34A)
    val greenStatus = Color(0xFF16A34A)
    val backgroundGray = Color(0xFFFBFBFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter action */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = greenPrimary
                )
            )
        },
        bottomBar = {
            DashboardBottomNavigation(
                selectedItem = 3,
                onDashboardClick = onNavigateToDashboard,
                onProdukClick = onNavigateToProduct,
                onTransaksiClick = onNavigateToTransaksi,
                onRiwayatClick = {},
                onLaporanClick = onNavigateToLaporan
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundGray)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = greenPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = greenPrimary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) greenPrimary else Color.Gray
                                )
                            )
                        }
                    )
                }
            }

            // Transaction List
            val transactions = remember {
                listOf(
                    TransactionItemData("INV-20250520-001", "20 Mei 2025 • 10:30", 3, "Selesai", 28000.0),
                    TransactionItemData("INV-20250520-002", "20 Mei 2025 • 11:15", 2, "Selesai", 16000.0),
                    TransactionItemData("INV-20250520-003", "20 Mei 2025 • 12:45", 4, "Selesai", 42000.0),
                    TransactionItemData("INV-20250520-004", "20 Mei 2025 • 13:30", 1, "Selesai", 8000.0),
                    TransactionItemData("INV-20250520-005", "20 Mei 2025 • 14:20", 5, "Selesai", 55000.0)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionCard(transaction, greenStatus)
                }
            }
        }
    }
}

data class TransactionItemData(
    val id: String,
    val dateTime: String,
    val itemCount: Int,
    val status: String,
    val amount: Double
)

@Composable
fun TransactionCard(transaction: TransactionItemData, statusColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.id,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = transaction.status,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = transaction.dateTime,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transaction.itemCount} item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Rp ${formatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

fun formatAmount(amount: Double): String {
    val integerPart = amount.toLong().toString()
    val result = StringBuilder()
    var count = 0
    for (i in integerPart.length - 1 downTo 0) {
        result.append(integerPart[i])
        count++
        if (count == 3 && i != 0) {
            result.append(".")
            count = 0
        }
    }
    return result.reverse().toString()
}
