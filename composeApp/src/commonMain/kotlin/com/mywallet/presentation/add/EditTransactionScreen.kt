package com.mywallet.presentation.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mywallet.domain.model.TransactionType
import com.mywallet.presentation.detail.DetailUiState
import com.mywallet.presentation.detail.DetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    transactionId: Int,
    onNavigateBack: () -> Unit,
    detailViewModel: DetailViewModel = koinViewModel(),
    addViewModel: AddTransactionViewModel = koinViewModel()
) {
    val detailState by detailViewModel.uiState.collectAsState()
    val addState by addViewModel.uiState.collectAsState()
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        detailViewModel.loadTransaction(transactionId)
    }

    LaunchedEffect(detailState) {
        if (!initialized && detailState is DetailUiState.Success) {
            val t = (detailState as DetailUiState.Success).transaction
            addViewModel.onTitleChange(t.title)
            addViewModel.onAmountChange(t.amount.toString())
            addViewModel.onTypeChange(t.type)
            addViewModel.onDateChange(t.date)
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = addState.title,
                onValueChange = addViewModel::onTitleChange,
                label = { Text("Keterangan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = addState.amount,
                onValueChange = addViewModel::onAmountChange,
                label = { Text("Nominal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp ") }
            )
            OutlinedTextField(
                value = addState.date,
                onValueChange = addViewModel::onDateChange,
                label = { Text("Tanggal (contoh: 15 May 2026)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text("Jenis Transaksi", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = addState.type == TransactionType.EXPENSE,
                    onClick = { addViewModel.onTypeChange(TransactionType.EXPENSE) },
                    label = { Text("Pengeluaran") }
                )
                FilterChip(
                    selected = addState.type == TransactionType.INCOME,
                    onClick = { addViewModel.onTypeChange(TransactionType.INCOME) },
                    label = { Text("Pemasukan") }
                )
            }
            addState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { addViewModel.updateTransaction(transactionId, onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !addState.isLoading
            ) {
                if (addState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}