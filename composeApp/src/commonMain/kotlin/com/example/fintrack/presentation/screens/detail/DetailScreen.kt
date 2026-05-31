package com.example.fintrack.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToLong

import com.example.fintrack.core.util.CurrencyFormatter

// Helper for formatting instants
private fun formatInstant(instant: kotlinx.datetime.Instant): String {
    val isoStr = instant.toString() // e.g. "2023-10-24T15:30:00Z"
    if (isoStr.length >= 10) {
        val parts = isoStr.substring(0, 10).split("-")
        if (parts.size == 3) {
            val year = parts[0]
            val month = when (parts[1]) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> parts[1]
            }
            val day = parts[2]
            return "$month $day, $year"
        }
    }
    return isoStr
}

// ==================== DETAIL SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    transactionId: Long,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = { DetailTopBar() },
        bottomBar = {
            if (state is DetailState.Success) {
                DetailBottomActions(
                    onDelete = { showDeleteDialog = true },
                    onEdit = { onNavigateToEdit(transactionId) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is DetailState.Loading -> {
                    CircularProgressIndicator(color = FinTrackGreen)
                }
                is DetailState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentState.message, color = ErrorRed, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = FinTrackGreen)) {
                            Text("Go Back")
                        }
                    }
                }
                is DetailState.Success -> {
                    val transaction = currentState.transaction
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Back link
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { onNavigateBack() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextGray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Back to Transactions",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Transaction header card
                        TransactionHeaderCard(transaction = transaction)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Transaction details
                        TransactionDetailsSection(transaction = transaction)
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && state is DetailState.Success) {
            val transaction = (state as DetailState.Success).transaction
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = DarkCard,
                titleContentColor = TextWhite,
                textContentColor = TextGray,
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteTransaction(transaction.id, onSuccess = onNavigateBack)
                        }
                    ) {
                        Text("Confirm", color = ErrorRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = TextGray)
                    }
                },
                title = { Text("Delete Transaction") },
                text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") }
            )
        }
    }
}

// ==================== TOP BAR ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar() {
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

// ==================== TRANSACTION HEADER CARD ====================

@Composable
private fun TransactionHeaderCard(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (transaction.type == TransactionType.INCOME) Icons.Default.TrendingUp else Icons.Default.Restaurant,
                    contentDescription = transaction.category,
                    tint = if (transaction.type == TransactionType.INCOME) FinTrackGreen else ErrorRed,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                transaction.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val prefix = if (transaction.type == TransactionType.INCOME) "+" else "-"
            val formatted = CurrencyFormatter.formatCurrency(transaction.amount, transaction.currency)
            Text(
                "$prefix${formatted.removePrefix("$").removePrefix("Rp ")}", // Custom clean formatting for hero card
                style = MaterialTheme.typography.headlineLarge,
                color = if (transaction.type == TransactionType.INCOME) FinTrackGreen else TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (transaction.type == TransactionType.INCOME) SuccessGreenBg else DarkSurfaceVariant
            ) {
                Text(
                    if (transaction.type == TransactionType.INCOME) "Received" else "Spent",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (transaction.type == TransactionType.INCOME) SuccessGreen else TextGray
                )
            }
        }
    }
}

// ==================== TRANSACTION DETAILS ====================

@Composable
private fun TransactionDetailsSection(transaction: Transaction) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Date
        DetailRow(label = "Date", value = formatInstant(transaction.date))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = DarkSurfaceVariant,
            thickness = 1.dp
        )

        // Category
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category", style = MaterialTheme.typography.bodyMedium, color = TextGray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (transaction.type == TransactionType.INCOME) FinTrackGreen else ErrorRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(transaction.category, style = MaterialTheme.typography.bodyLarge, color = TextWhite, fontWeight = FontWeight.Medium)
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = DarkSurfaceVariant,
            thickness = 1.dp
        )

        // Transaction note/info
        Text("Transaction Type", style = MaterialTheme.typography.bodyMedium, color = TextGray)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Text(
                "This is an ${transaction.type.name.lowercase()} transaction of ${CurrencyFormatter.formatCurrency(transaction.amount, transaction.currency)} categorized under '${transaction.category}'.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextGray)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = TextWhite, fontWeight = FontWeight.Medium)
    }
}

// ==================== BOTTOM ACTIONS ====================

@Composable
private fun DetailBottomActions(
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        color = DarkBackground,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Delete button
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(ErrorRed.copy(alpha = 0.5f), ErrorRed.copy(alpha = 0.5f))
                    )
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete", style = MaterialTheme.typography.labelLarge)
            }

            // Edit button
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinTrackGreen,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}