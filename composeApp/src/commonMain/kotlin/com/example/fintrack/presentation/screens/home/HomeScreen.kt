package com.example.fintrack.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.presentation.theme.*
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlin.math.roundToLong
import com.example.fintrack.core.util.CurrencyFormatter

// ==================== HOME SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToExchange: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val monthlyIncome by viewModel.monthlyIncome.collectAsState()
    val monthlyExpense by viewModel.monthlyExpense.collectAsState()
    val totalBalanceIdr by viewModel.totalBalanceIdr.collectAsState()
    val monthlyIncomeIdr by viewModel.monthlyIncomeIdr.collectAsState()
    val monthlyExpenseIdr by viewModel.monthlyExpenseIdr.collectAsState()
    val lastIdrRate by viewModel.lastIdrRate.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { FinTrackTopBar(onSettingsClick = onNavigateToSettings) },
        bottomBar = {
            FinTrackBottomBar(
                onNavigateToExchange = onNavigateToExchange,
                onNavigateToHistory = onNavigateToHistory
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = FinTrackGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                BalanceCard(
                    balanceUsd = totalBalance,
                    balanceIdr = totalBalanceIdr
                )
                Spacer(modifier = Modifier.height(16.dp))
                MonthlyOverviewRow(
                    monthlyIncomeUsd = monthlyIncome,
                    monthlyExpenseUsd = monthlyExpense,
                    monthlyIncomeIdr = monthlyIncomeIdr,
                    monthlyExpenseIdr = monthlyExpenseIdr
                )
                Spacer(modifier = Modifier.height(24.dp))
                RecentTransactionsHeader(
                    onViewAllClick = onNavigateToHistory
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                if (recentTransactions.isEmpty()) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        EmptyTransactionsState(onAddClick = onNavigateToAdd)
                    }
                }
            }

            if (recentTransactions.isNotEmpty()) {
                items(recentTransactions, key = { it.id }) { transaction ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
                        TransactionItem(
                            transaction = transaction,
                            idrRate = lastIdrRate,
                            onClick = { onNavigateToDetail(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}

// ==================== TOP BAR ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinTrackTopBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextWhite
                )
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(FinTrackGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "F",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "FinTrack",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite
                )
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = TextGray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground
        )
    )
}

// ==================== BALANCE CARD ====================

@Composable
private fun BalanceCard(
    balanceUsd: Double = 0.0,
    balanceIdr: Double = 0.0
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "TOTAL BALANCE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = CurrencyFormatter.formatCurrency(balanceUsd, "USD"),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "≈ ${CurrencyFormatter.formatCurrency(balanceIdr, "IDR")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==================== MONTHLY OVERVIEW ====================

@Composable
private fun MonthlyOverviewRow(
    monthlyIncomeUsd: Double,
    monthlyExpenseUsd: Double,
    monthlyIncomeIdr: Double,
    monthlyExpenseIdr: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MonthlyCard(
            modifier = Modifier.weight(1f),
            label = "Monthly Income",
            amountUsd = monthlyIncomeUsd,
            amountIdr = monthlyIncomeIdr,
            iconColor = FinTrackGreen,
            isIncome = true
        )
        MonthlyCard(
            modifier = Modifier.weight(1f),
            label = "Monthly Expenses",
            amountUsd = monthlyExpenseUsd,
            amountIdr = monthlyExpenseIdr,
            iconColor = ErrorRed,
            isIncome = false
        )
    }
}

@Composable
private fun MonthlyCard(
    modifier: Modifier = Modifier,
    label: String,
    amountUsd: Double,
    amountIdr: Double,
    iconColor: Color,
    isIncome: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isIncome) "↓" else "↑",
                        color = iconColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CurrencyFormatter.formatCurrency(amountUsd, "USD"),
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "≈ ${CurrencyFormatter.formatCurrency(amountIdr, "IDR")}",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ==================== RECENT TRANSACTIONS ====================

@Composable
private fun RecentTransactionsHeader(
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Recent Transactions",
            style = MaterialTheme.typography.titleMedium,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "View All >",
            style = MaterialTheme.typography.labelMedium,
            color = FinTrackGreen,
            modifier = Modifier.clickable { onViewAllClick() }
        )
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    idrRate: Double,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (transaction.type == TransactionType.INCOME) Icons.Default.TrendingUp else Icons.Default.Receipt,
                contentDescription = null,
                tint = if (transaction.type == TransactionType.INCOME) FinTrackGreen else Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                transaction.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                transaction.category,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
        val prefix = if (transaction.type == TransactionType.INCOME) "+" else "-"
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$prefix${CurrencyFormatter.formatCurrency(transaction.amount, "USD").removePrefix("$")}",
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.type == TransactionType.INCOME) FinTrackGreen else TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "≈ ${CurrencyFormatter.formatCurrency(transaction.amount * idrRate, "IDR")}",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun EmptyTransactionsState(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Sparkle badge
            Box(
                modifier = Modifier
                    .offset(x = 20.dp, y = (-16).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(FinTrackGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }

            Text(
                "No Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "It looks like you haven't made any\ntransactions recently. Track your\nspending by adding your first\ntransaction now.",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
            ) {
                Text("Add First Transaction", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// ==================== BOTTOM NAVIGATION ====================

@Composable
private fun FinTrackBottomBar(
    onNavigateToExchange: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextGray,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FinTrackGreen,
                selectedTextColor = FinTrackGreen,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = FinTrackGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToExchange,
            icon = { Icon(Icons.Default.CurrencyExchange, contentDescription = "Exchange") },
            label = { Text("Exchange", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FinTrackGreen,
                selectedTextColor = FinTrackGreen,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = FinTrackGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHistory,
            icon = { Icon(Icons.Default.Receipt, contentDescription = "Transactions") },
            label = { Text("Transactions", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FinTrackGreen,
                selectedTextColor = FinTrackGreen,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = FinTrackGreen.copy(alpha = 0.1f)
            )
        )
    }
}