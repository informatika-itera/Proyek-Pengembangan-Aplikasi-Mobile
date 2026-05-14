package com.example.pocketguard.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.presentation.components.CategoryBadge
import com.example.pocketguard.presentation.components.EmptyState
import com.example.pocketguard.presentation.components.LoadingIndicator
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionDetailViewModel = koinViewModel()
) {
    // FIX 1: Gunakan uiState sesuai dengan yang ada di ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

    // FIX 2: Navigasi back ditangani oleh event dari ViewModel
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TransactionDetailEvent.TransactionDeleted -> onNavigateBack()
                is TransactionDetailEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteTransaction() // Panggil tanpa lambda karena sudah dihandle LaunchedEffect
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Akses aksi hanya jika statusnya Success
                    if (uiState is TransactionDetailUiState.Success) {
                        IconButton(onClick = { onNavigateToEdit(transactionId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // FIX 3: Gunakan when untuk menangani berbagai status UI
        when (val state = uiState) {
            is TransactionDetailUiState.Loading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }

            is TransactionDetailUiState.NotFound -> {
                EmptyState(
                    title = "Tidak Ditemukan",
                    message = "Transaksi mungkin telah dihapus",
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is TransactionDetailUiState.Success -> {
                val currentTransaction = state.transaction
                val amountColor = if (currentTransaction.type == TransactionType.INCOME) {
                    Color(0xFF2E7D32) // Hijau
                } else {
                    MaterialTheme.colorScheme.error // Merah
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${if (currentTransaction.type == TransactionType.INCOME) "+" else "-"} Rp ${currentTransaction.amount}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CategoryBadge(category = currentTransaction.category.displayName)

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailItem(label = "Deskripsi", value = currentTransaction.description)

                            // FIX 4: Gunakan HorizontalDivider (Material3)
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )

                            DetailItem(label = "Tipe", value = if (currentTransaction.type == TransactionType.INCOME) "Pemasukan" else "Pengeluaran")

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )

                            DetailItem(label = "Tanggal", value = formatTimestamp(currentTransaction.createdAt))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Transaksi") },
        text = { Text("Apakah Anda yakin ingin menghapus data transaksi ini?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    // Menambahkan padding nol untuk menit agar format waktu rapi (misal: 10:05)
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year} ${dateTime.hour}:$minute"
}