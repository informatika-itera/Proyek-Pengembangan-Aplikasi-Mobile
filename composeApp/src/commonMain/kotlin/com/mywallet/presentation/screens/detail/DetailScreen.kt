package com.mywallet.presentation.screens.detail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mywallet.domain.model.TransactionType
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import com.mywallet.presentation.components.LoadingIndicator
import com.mywallet.theme.DarkNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    transactionId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit = {},
    viewModel: DetailViewModel = koinViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val headerTextColor = if (isDark) Color.Black else Color.White

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detail Transaksi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = headerTextColor
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = headerTextColor)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Implement Share Logic */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = headerTextColor)
                    }
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = headerTextColor)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = headerTextColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                LoadingIndicator()
            }
            is DetailUiState.Success -> {
                val transaction = state.transaction
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetailCard(label = "Keterangan", value = transaction.title)
                    
                    DetailCard(
                        label = "Nominal", 
                        value = "Rp. ${formatCurrency(transaction.amount)}",
                        valueColor = if (transaction.type == TransactionType.INCOME)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        isBoldValue = true
                    )
                    
                    DetailCard(
                        label = "Jenis", 
                        value = if (transaction.type == TransactionType.INCOME) "Pemasukan" else "Pengeluaran"
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DetailCard(label = "Tanggal", value = transaction.date)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DetailCard(label = "Waktu", value = transaction.time)
                        }
                    }
                }
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi") },
            text = { Text("Yakin ingin menghapus transaksi ini?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    if (uiState is DetailUiState.Success) {
                        viewModel.deleteTransaction((uiState as DetailUiState.Success).transaction.id, onNavigateBack)
                    }
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun DetailCard(
    label: String, 
    value: String, 
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBoldValue: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = label, 
                style = MaterialTheme.typography.labelMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value, 
                style = if (isBoldValue) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black) 
                        else MaterialTheme.typography.bodyLarge,
                color = valueColor,
                fontWeight = if (isBoldValue) FontWeight.Black else FontWeight.Medium
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val longAmount = amount.toLong()
    val str = longAmount.toString()
    val result = StringBuilder()
    var count = 0
    for (i in str.length - 1 downTo 0) {
        result.append(str[i])
        count++
        if (count == 3 && i != 0) {
            result.append('.')
            count = 0
        }
    }
    return result.reverse().toString()
}
