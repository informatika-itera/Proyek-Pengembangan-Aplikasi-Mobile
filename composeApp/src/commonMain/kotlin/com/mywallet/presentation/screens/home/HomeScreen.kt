package com.mywallet.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mywallet.presentation.components.TransactionItem
import com.mywallet.presentation.components.LoadingIndicator
import com.mywallet.theme.Spacing
import com.mywallet.utils.formatCurrency
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isDark = isSystemInDarkTheme()
    val headerTextColor = Color.White

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    if (exchangeRate != null) {
                        Text(
                            text = "1 USD = Rp ${(1.0 / (exchangeRate ?: 1.0)).toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = headerTextColor.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = Spacing.md)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = headerTextColor,
                            modifier = Modifier.size(Spacing.lg)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            "MyWallet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = headerTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(Spacing.md)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Dark Header Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = Spacing.lg)
                ) {
                    Column(
                        modifier = Modifier.offset(y = Spacing.xs)
                    ) {
                        Text(
                            text = "Halo, $userName!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = headerTextColor,
                            fontSize = 36.sp
                        )
                        Text(
                            text = "Kelola keuanganmu dengan bijak hari ini",
                            style = MaterialTheme.typography.bodyLarge,
                            color = headerTextColor.copy(alpha = 0.8f)
                        )
                    }
                }

                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-50).dp)
                        .padding(horizontal = 20.dp)
                ) {
                    when (val state = uiState) {
                        is HomeUiState.Success -> {
                            BalanceCard(
                                balance = state.balance,
                                income = state.totalIncome,
                                expense = state.totalExpense
                            )

                            Spacer(modifier = Modifier.height(Spacing.md))

                            if (state.transactions.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Transaksi Terakhir",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    TextButton(onClick = onNavigateToHistory) {
                                        Text("Lihat Semua", color = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(Spacing.sm + Spacing.xs),
                                    contentPadding = PaddingValues(bottom = Spacing.md)
                                ) {
                                    items(state.transactions) { transaction ->
                                        TransactionItem(
                                            transaction = transaction,
                                            onClick = { onNavigateToDetail(transaction.id) }
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Belum ada transaksi",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                        is HomeUiState.Loading -> {
                            LoadingIndicator()
                        }
                        is HomeUiState.Empty -> {
                            BalanceCard(balance = 0.0, income = 0.0, expense = 0.0)
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Belum ada transaksi",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap + untuk menambah transaksi pertama",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                        else -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Belum ada data", color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, income: Double, expense: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Spacing.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "SISA SALDO",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Rp. ${formatCurrency(balance)}",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Spacing.md))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(Spacing.sm + Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pemasukan",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Rp. ${formatCurrency(income)}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Box(modifier = Modifier.height(30.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                
                Column(modifier = Modifier.weight(1f).padding(start = Spacing.md)) {
                    Text(
                        text = "Pengeluaran",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Rp. ${formatCurrency(expense)}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
