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
import com.example.mapenumkm.domain.model.Transaction
import com.example.mapenumkm.presentation.screens.home.DashboardBottomNavigation
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProduct: () -> Unit,
    onNavigateToTransaction: () -> Unit,
    onNavigateToReport: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Semua", "Hari ini", "Minggu ini", "Bulan ini")
    
    val selectedTabIndex = when ((uiState as? HistoryUiState.Success)?.selectedFilter) {
        HistoryFilter.ALL -> 0
        HistoryFilter.TODAY -> 1
        HistoryFilter.THIS_WEEK -> 2
        HistoryFilter.THIS_MONTH -> 3
        else -> 0
    }
    
    val greenPrimary = Color(0xFF16A34A)
    val greenStatus = Color(0xFF16A34A)
    val backgroundGray = Color(0xFFFBFBFF)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(greenPrimary)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
        bottomBar = {
            DashboardBottomNavigation(
                selectedItem = 3,
                onDashboardClick = onNavigateToDashboard,
                onProdukClick = onNavigateToProduct,
                onTransaksiClick = onNavigateToTransaction,
                onRiwayatClick = {},
                onLaporanClick = onNavigateToReport
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
                            onClick = {
                                val filter = when (index) {
                                    0 -> HistoryFilter.ALL
                                    1 -> HistoryFilter.TODAY
                                    2 -> HistoryFilter.THIS_WEEK
                                    3 -> HistoryFilter.THIS_MONTH
                                    else -> HistoryFilter.ALL
                                }
                                viewModel.onFilterSelected(filter)
                            },
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

            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = greenPrimary)
                    }
                }
                is HistoryUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
                is HistoryUiState.Success -> {
                    if (state.transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Belum ada transaksi", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.transactions) { transaction ->
                                TransactionCard(transaction, greenStatus)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction, statusColor: Color) {
    val dateTime = transaction.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateStr = "${dateTime.dayOfMonth} ${getMonthName(dateTime.monthNumber)} ${dateTime.year} • ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    
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
                    text = "INV-${transaction.id.toString().padStart(6, '0')}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Selesai",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = dateStr,
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
                    text = "${transaction.items.sumOf { it.quantity }} item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Rp ${formatAmount(transaction.total)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "Mei"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Agu"
        9 -> "Sep"
        10 -> "Okt"
        11 -> "Nov"
        12 -> "Des"
        else -> ""
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
