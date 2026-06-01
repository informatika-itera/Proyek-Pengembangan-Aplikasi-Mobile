package com.mywallet.presentation.screens.history

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mywallet.presentation.components.LoadingIndicator
import com.mywallet.presentation.components.TransactionItem
import com.mywallet.presentation.screens.home.HomeUiState
import com.mywallet.presentation.screens.home.HomeViewModel
import com.mywallet.theme.Spacing
import com.mywallet.utils.formatIsoDateToDisplay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val isDark = isSystemInDarkTheme()
    val headerTextColor = if (isDark) Color.Black else Color.White

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = headerTextColor,
                            fontSize = 28.sp
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar and Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Cari...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(Spacing.md),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(Spacing.sm))
                
                IconButton(
                    onClick = { viewModel.toggleSortOrder() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Sort",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                FilterChip(
                    selected = filterType == null,
                    onClick = { viewModel.onFilterTypeChange(null) },
                    label = { Text("Semua") }
                )
                FilterChip(
                    selected = filterType == "INCOME",
                    onClick = { viewModel.onFilterTypeChange("INCOME") },
                    label = { Text("Pemasukan") }
                )
                FilterChip(
                    selected = filterType == "EXPENSE",
                    onClick = { viewModel.onFilterTypeChange("EXPENSE") },
                    label = { Text("Pengeluaran") }
                )
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingIndicator()
                }
                is HomeUiState.Success -> {
                    if (state.transactions.isNotEmpty()) {
                        val groupedTransactions = state.transactions.groupBy { it.date }
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = Spacing.md, end = Spacing.md, bottom = Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm + Spacing.xs)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = Spacing.sm),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (searchQuery.isEmpty()) "Semua Catatan" else "Hasil Pencarian",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "${state.transactions.size} transaksi",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            // Summary Card for History
                            if (searchQuery.isEmpty()) {
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(Spacing.md),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(Spacing.md).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Pemasukan", style = MaterialTheme.typography.labelMedium)
                                                Text("Rp ${com.mywallet.utils.formatCurrency(state.totalIncome)}", 
                                                    color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Pengeluaran", style = MaterialTheme.typography.labelMedium)
                                                Text("Rp ${com.mywallet.utils.formatCurrency(state.totalExpense)}", 
                                                    color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            groupedTransactions.forEach { (date, transactions) ->
                                item {
                                    Text(
                                        text = formatIsoDateToDisplay(date),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(top = Spacing.sm)
                                    )
                                }
                                items(transactions) { transaction ->
                                    TransactionItem(
                                        transaction = transaction,
                                        onClick = { onNavigateToDetail(transaction.id) }
                                    )
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (searchQuery.isEmpty()) "Belum ada transaksi" else "Tidak ada transaksi ditemukan",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
