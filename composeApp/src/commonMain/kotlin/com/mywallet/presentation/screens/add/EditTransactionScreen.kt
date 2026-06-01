package com.mywallet.presentation.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mywallet.domain.model.TransactionType
import com.mywallet.presentation.screens.detail.DetailUiState
import com.mywallet.presentation.screens.detail.DetailViewModel
import com.mywallet.utils.formatIsoDateToDisplay
import com.mywallet.utils.formatMillisToDate
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
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        addViewModel.onDateChange(formatMillisToDate(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(transactionId) {
        detailViewModel.loadTransaction(transactionId)
    }

    LaunchedEffect(detailState) {
        if (!initialized && detailState is DetailUiState.Success) {
            val t = (detailState as DetailUiState.Success).transaction
            addViewModel.onTitleChange(t.title)
            addViewModel.onAmountChange(t.amount.toString())
            addViewModel.onTypeChange(t.type)
            addViewModel.onCategoryChange(t.category)
            addViewModel.onDateChange(t.date)
            addViewModel.onTimeChange(t.time)
            addViewModel.onRecurringChange(t.isRecurring)
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                value = formatIsoDateToDisplay(addState.date),
                onValueChange = {},
                label = { Text("Tanggal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pilih Tanggal"
                        )
                    }
                }
            )
            OutlinedTextField(
                value = addState.time,
                onValueChange = addViewModel::onTimeChange,
                label = { Text("Waktu (contoh: 10:30)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category Selection
            var expanded by remember { mutableStateOf(false) }
            val categories = if (addState.type == TransactionType.EXPENSE) {
                listOf("Makanan", "Transportasi", "Belanja", "Hiburan", "Kesehatan", "Tagihan", "Lainnya")
            } else {
                listOf("Gaji", "Bonus", "Investasi", "Hadiah", "Lainnya")
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = addState.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                addViewModel.onCategoryChange(category)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Recurring Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Transaksi Berulang", style = MaterialTheme.typography.bodyLarge)
                    Text("Catat otomatis setiap bulan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = addState.isRecurring,
                    onCheckedChange = addViewModel::onRecurringChange
                )
            }

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
