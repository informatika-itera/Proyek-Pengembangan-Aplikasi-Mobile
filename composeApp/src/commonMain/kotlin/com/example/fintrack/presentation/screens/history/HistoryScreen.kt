package com.example.fintrack.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToLong

import com.example.fintrack.core.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToExchange: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val lastIdrRate by viewModel.lastIdrRate.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Income", "Expense"

    val displayedTransactions = remember(filteredTransactions, selectedFilter) {
        filteredTransactions.filter { tx ->
            when (selectedFilter) {
                "Income" -> tx.type == TransactionType.INCOME
                "Expense" -> tx.type == TransactionType.EXPENSE
                else -> true
            }
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = { HistoryTopBar() },
        bottomBar = {
            HistoryBottomBar(
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateToExchange = onNavigateToExchange
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header with Back button and Title
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextGray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onNavigateToDashboard() }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "All Transactions",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Search Bar — bound to ViewModel
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search by title or category...", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = TextGray
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FinTrackGreen,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            cursorColor = FinTrackGreen,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true
                    )

                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filters = listOf("All", "Income", "Expense")
                        filters.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Surface(
                                modifier = Modifier.clickable { selectedFilter = filter },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) FinTrackGreen.copy(alpha = 0.15f) else DarkCard,
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        if (isSelected) listOf(FinTrackGreen, FinTrackGreen)
                                        else listOf(DarkSurfaceVariant, DarkSurfaceVariant)
                                    )
                                )
                            ) {
                                Text(
                                    text = filter,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) FinTrackGreen else TextGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (displayedTransactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No Transactions Found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Try adjusting your search query or filters.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            if (displayedTransactions.isNotEmpty()) {
                items(displayedTransactions, key = { it.id }) { transaction ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(FinTrackGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("F", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("FinTrack", style = MaterialTheme.typography.titleLarge, color = TextWhite)
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextGray)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
    )
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
private fun HistoryBottomBar(
    onNavigateToDashboard: () -> Unit,
    onNavigateToExchange: () -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextGray,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToDashboard,
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
            selected = true,
            onClick = { },
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
