package com.example.pocketguard.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.usecase.TransactionSortBy
import com.example.pocketguard.presentation.components.EmptyState
import com.example.pocketguard.presentation.components.ErrorState
import com.example.pocketguard.presentation.components.LoadingIndicator
import com.example.pocketguard.presentation.components.TransactionCard
import com.example.pocketguard.presentation.components.BudgetProgressBar
import com.example.pocketguard.presentation.components.SetBudgetDialog
import org.koin.compose.viewmodel.koinViewModel

/* =====================================================================
 * COLORS & CONSTANTS
 * ===================================================================== */
private val GreenDark = Color(0xFF1B5E20)
private val GreenMid = Color(0xFF2E7D32)
private val GreenLight = Color(0xFF43A047)
private val IncomeGreen = Color(0xFF2E7D32)
private val ExpenseRed = Color(0xFFB71C1C)

/* =====================================================================
 * MAIN SCREEN COMPOSABLE
 * ===================================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: (type: String?, category: String?) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    var showSearch by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentFilteredTransactions = when (val state = uiState) {
        is HomeUiState.Success -> state.transactions
        else -> emptyList()
    }

    val absoluteBalance = when (val state = uiState) {
        is HomeUiState.Success -> state.totalBalance
        is HomeUiState.Empty -> state.totalBalance
        else -> 0.0
    }
    val budgetLimit = when (val state = uiState) {
        is HomeUiState.Success -> state.budgetLimit
        is HomeUiState.Empty -> state.budgetLimit
        else -> 0.0
    }
    val budgetExpense = when (val state = uiState) {
        is HomeUiState.Success -> state.budgetExpense
        is HomeUiState.Empty -> state.budgetExpense
        else -> 0.0
    }

    val activeBudgetMonth = when (val state = uiState) {
        is HomeUiState.Success -> state.activeBudgetMonth
        is HomeUiState.Empty -> state.activeBudgetMonth
        else -> ""
    }

    val availableMonths = when (val state = uiState) {
        is HomeUiState.Success -> state.availableMonths
        is HomeUiState.Empty -> state.availableMonths
        else -> emptyList()
    }

    val selectedMonth = when (val state = uiState) {
        is HomeUiState.Success -> state.selectedMonth
        is HomeUiState.Empty -> state.selectedMonth
        else -> null
    }

    if (showBudgetDialog) {
        SetBudgetDialog(
            currentBudget = budgetLimit,
            onDismiss = { showBudgetDialog = false },
            onSave = { newLimit ->
                viewModel.updateBudgetLimit(newLimit)
                showBudgetDialog = false
            }
        )
    }

    if (showAddBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            AddTransactionBottomSheet(
                onNavigateToAdd = { type, category ->
                    showAddBottomSheet = false
                    onNavigateToAdd(type, category)
                },
                onDismiss = { showAddBottomSheet = false }
            )
        }
    }

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
                        Text("PocketGuard", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddBottomSheet = true },
                containerColor = GreenMid,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
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
            // ===== SUMMARY CARD =====
            SummarySection(
                filteredTransactions = currentFilteredTransactions,
                absoluteBalance = absoluteBalance,
                budgetLimit = budgetLimit,
                budgetExpense = budgetExpense,
                activeBudgetMonth = activeBudgetMonth,
                onEditBudgetClick = { showBudgetDialog = true } // 🛠️ PERBAIKAN: Typo onBudgetClick diperbaiki
            )

            // ===== FILTER BULAN DINAMIS =====
            MonthFilterRow(
                availableMonths = availableMonths,
                selectedMonth = selectedMonth,
                onMonthSelected = { bulan ->
                    viewModel.onMonthSelected(bulan)
                }
            )

            // ===== LABEL RIWAYAT =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat Transaksi",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // ===== KONTEN UTAMA =====
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
                        title = if (state.query.isNotBlank() || state.selectedMonth != null)
                            "Tidak Ditemukan"
                        else
                            "Belum Ada Transaksi",
                        message = if (state.query.isNotBlank() || state.selectedMonth != null)
                            "Coba ubah kata kunci atau filter bulan"
                        else
                            "Mulai catat keuanganmu sekarang",
                        icon = {
                            Icon(
                                Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = GreenMid.copy(alpha = 0.5f)
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

/* =====================================================================
 * FILTER COMPONENTS
 * ===================================================================== */
@Composable
private fun MonthFilterRow(
    availableMonths: List<String>,
    selectedMonth: String?,
    onMonthSelected: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedMonth == null,
                onClick = { onMonthSelected(null) },
                label = { Text("Semua Waktu") }
            )
        }
        items(availableMonths) { month ->
            FilterChip(
                selected = selectedMonth == month,
                onClick = { onMonthSelected(if (selectedMonth == month) null else month) },
                label = { Text(month) }
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, onClear: () -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari deskripsi...") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = onClear) { Icon(Icons.Default.Close, contentDescription = "Hapus") }
            }
        }
    )
}

@Composable
private fun SortDropdownMenu(expanded: Boolean, currentSortBy: TransactionSortBy, onSortSelected: (TransactionSortBy) -> Unit, onDismiss: () -> Unit) {
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

/* =====================================================================
 * BOTTOM SHEET COMPONENTS
 * ===================================================================== */
@Composable
private fun AddTransactionBottomSheet(
    onNavigateToAdd: (type: String?, category: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    val isExpense = selectedType == TransactionType.EXPENSE
    val accentColor = if (isExpense) ExpenseRed else IncomeGreen

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tambah Transaksi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Tutup")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TransactionType.entries.forEach { type ->
                val isSelected = selectedType == type
                val btnColor = if (type == TransactionType.EXPENSE) ExpenseRed else IncomeGreen
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (isSelected) btnColor.copy(alpha = 0.15f)
                            else Color.Transparent
                        )
                        .clickable { selectedType = type }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (type == TransactionType.EXPENSE) "💸 Pengeluaran" else "💰 Pemasukan",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) btnColor
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Pilih Kategori",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        val categoryEmojis = mapOf(
            TransactionCategory.FOOD to "🍜",
            TransactionCategory.TRANSPORT to "🚗",
            TransactionCategory.BILLS to "🏠",
            TransactionCategory.SALARY to "💵",
            TransactionCategory.OTHER to "📦"
        )

        val categoryLabels = mapOf(
            TransactionCategory.FOOD to "Makanan",
            TransactionCategory.TRANSPORT to "Transport",
            TransactionCategory.BILLS to "Tagihan",
            TransactionCategory.SALARY to "Gaji",
            TransactionCategory.OTHER to "Lainnya"
        )

        val categories = if (selectedType == TransactionType.INCOME) {
            listOf(TransactionCategory.SALARY)
        } else {
            listOf(
                TransactionCategory.FOOD,
                TransactionCategory.TRANSPORT,
                TransactionCategory.BILLS,
                TransactionCategory.OTHER
            )
        }
        val chunked = categories.chunked(3)

        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { category ->
                    CategoryQuickButton(
                        modifier = Modifier.weight(1f),
                        emoji = categoryEmojis[category] ?: "📦",
                        label = categoryLabels[category] ?: category.name,
                        onClick = {
                            onNavigateToAdd(selectedType.name, category.name)
                        }
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onNavigateToAdd(selectedType.name, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Isi Detail Manual", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Composable
private fun CategoryQuickButton(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* =====================================================================
 * SUMMARY COMPONENTS
 * ===================================================================== */
@Composable
private fun SummarySection(
    filteredTransactions: List<Transaction>,
    absoluteBalance: Double,
    budgetLimit: Double,
    budgetExpense: Double,
    activeBudgetMonth: String,
    onEditBudgetClick: () -> Unit
) {
    val monthlyIncome = filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val monthlyExpense = filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(colors = listOf(GreenDark, GreenLight)))
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Total Saldo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${formatAmount(absoluteBalance)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Total keseluruhan saat ini",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IncomeGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.TrendingUp, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Pemasukan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("+${formatAmount(monthlyIncome)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = IncomeGreen)
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ExpenseRed.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.TrendingDown, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Pengeluaran", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("-${formatAmount(monthlyExpense)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExpenseRed)
                    }
                }
            }
        } // 🛠️ PERBAIKAN: Kurung tutup Row ada di sini

        Spacer(modifier = Modifier.height(16.dp))

        // 🛠️ PERBAIKAN: BudgetProgressBar sekarang berada di luar Row, langsung di dalam Column
        BudgetProgressBar(
            totalExpense = budgetExpense,
            budgetLimit = budgetLimit,
            monthLabel = activeBudgetMonth,
            onEditClick = onEditBudgetClick,

        )
    }
}

/* =====================================================================
 * LIST COMPONENTS
 * ===================================================================== */
@Composable
private fun TransactionsList(transactions: List<Transaction>, onTransactionClick: (Long) -> Unit, onDeleteClick: (Long) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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

/* =====================================================================
 * FORMATTER UTILS
 * ===================================================================== */
private fun formatAmount(amount: Double): String {
    val isNegative = amount < 0
    val absLong = kotlin.math.abs(amount).toLong()
    val str = absLong.toString()
    val result = StringBuilder()
    str.reversed().forEachIndexed { index, c ->
        if (index > 0 && index % 3 == 0) {
            result.append('.')
        }
        result.append(c)
    }
    val formatted = result.reversed().toString()

    return if (isNegative) "-$formatted" else formatted
}