package com.example.pocketguard.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.usecase.TransactionSortBy
import com.example.pocketguard.presentation.components.EmptyState
import com.example.pocketguard.presentation.components.ErrorState
import com.example.pocketguard.presentation.components.LoadingIndicator
import com.example.pocketguard.presentation.components.TransactionCard
import com.example.pocketguard.presentation.theme.PgDanger
import com.example.pocketguard.presentation.theme.PgPrimary
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    var showSearch by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        SearchField(
                            query = when (val state = uiState) {
                                is HomeUiState.Success -> state.query
                                is HomeUiState.Empty -> state.query
                                else -> ""
                            },
                            onQueryChange = viewModel::onSearchQueryChange,
                            onClear = {
                                viewModel.clearSearch()
                                showSearch = false
                            }
                        )
                    } else {
                        Text("PocketGuard", fontWeight = FontWeight.SemiBold)
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Cari")
                        }

                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Outlined.Sort, contentDescription = "Urutkan")
                        }

                        SortDropdownMenu(
                            expanded = showSortMenu,
                            currentSortBy = currentSortBy,
                            onSortSelected = {
                                viewModel.onSortByChanged(it)
                                showSortMenu = false
                            },
                            onDismiss = { showSortMenu = false }
                        )
                    }

                    IconButton(onClick = onNavigateToAI) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Analyzer")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Pengaturan")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Baris Filter Kategori bawaan
            CategoryFilterRow(
                selectedCategory = when (val state = uiState) {
                    is HomeUiState.Success -> state.category
                    is HomeUiState.Empty -> state.category
                    else -> null
                },
                onCategorySelected = viewModel::onCategorySelected
            )

            // Hitung variabel finansial berdasarkan isi State UI secara dinamis
            val (totalBalance, totalIncome, totalExpense) = remember(uiState) {
                when (val state = uiState) {
                    is HomeUiState.Success -> {
                        val inc = state.transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        val exp = state.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                        Triple(inc - exp, inc, exp)
                    }
                    else -> Triple(0.0, 0.0, 0.0)
                }
            }

            // Tampilkan komponen visual Dashboard Finansial di bagian atas list
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                BalanceCard(balance = totalBalance)
                Spacer(modifier = Modifier.height(8.dp))
                MiniStatRow(income = totalIncome, expense = totalExpense)
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState is HomeUiState.Success) {
                    Text(
                        text = "Transaksi terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // Penanganan State Pemuatan Data List
            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingIndicator()

                is HomeUiState.Success -> {
                    TransactionsList(
                        transactions = state.transactions,
                        onTransactionClick = onNavigateToDetail,
                        onDeleteClick = viewModel::deleteTransaction
                    )
                }

                is HomeUiState.Empty -> {
                    EmptyState(
                        title = if (state.query.isNotBlank() || state.category != null) "Tidak Ditemukan" else "Belum Ada Transaksi",
                        message = if (state.query.isNotBlank() || state.category != null) "Coba ubah kata kunci atau filter" else "Mulai catat keuanganmu sekarang",
                        icon = {
                            Icon(
                                Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        }
                    )
                }

                is HomeUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.clearSearch() }
                    )
                }
            }
        }
    }
}

// ==================== DESIGN SYSTEM COMPONENTS ====================

@Composable
private fun BalanceCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PgPrimary),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Total Saldo",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.75f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rp ${formatCurrency(balance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Diperbarui barusan",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun MiniStatRow(income: Double, expense: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Kotak Statistik Pemasukan
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Pemasukan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "+${formatCurrency(income)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = PgPrimary
                )
            }
        }

        // Kotak Statistik Pengeluaran
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Pengeluaran",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "-${formatCurrency(expense)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = PgDanger
                )
            }
        }
    }
}

// Fungsi pembantu murni (KMP compatible) untuk memformat ribuan menggunakan titik (e.g. 1.000.000)
private fun formatCurrency(amount: Double): String {
    val isNegative = amount < 0
    val absAmount = if (isNegative) -amount else amount
    val str = absAmount.toLong().toString().reversed().chunked(3).joinToString(".").reversed()
    return if (isNegative) "-$str" else str
}

// ==================== COMPONENT BAWAAN ====================

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari deskripsi transaksi...") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus")
                }
            }
        }
    )
}

@Composable
private fun SortDropdownMenu(
    expanded: Boolean,
    currentSortBy: TransactionSortBy,
    onSortSelected: (TransactionSortBy) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        TransactionSortBy.entries.forEach { sortBy ->
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sortBy.displayName)
                        if (sortBy == currentSortBy) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                onClick = { onSortSelected(sortBy) }
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: TransactionCategory?,
    onCategorySelected: (TransactionCategory?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Semua") }
            )
        }
        items(TransactionCategory.entries) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(if (selectedCategory == category) null else category) },
                label = { Text(category.displayName) }
            )
        }
    }
}

@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = transactions, key = { it.id }) { transaction ->
            TransactionCard(
                transaction = transaction,
                onClick = { onTransactionClick(transaction.id) },
                onDeleteClick = { onDeleteClick(transaction.id) }
            )
        }
    }
}