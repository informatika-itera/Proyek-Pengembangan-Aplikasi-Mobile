package com.example.pocketguard.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.presentation.components.EmptyState
import com.example.pocketguard.presentation.components.LoadingIndicator
import com.example.pocketguard.presentation.theme.PgDanger
import com.example.pocketguard.presentation.theme.PgPrimary
import com.example.pocketguard.presentation.theme.PgPrimaryLight
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

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
                viewModel.deleteTransaction()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        // 3. ACTIONS FOOTER ROW - Dipindahkan ke bagian bawah Scaffold sesuai desain HTML Anda
        bottomBar = {
            if (uiState is TransactionDetailUiState.Success) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tombol Edit
                        OutlinedButton(
                            onClick = { onNavigateToEdit(transactionId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(text = "Edit", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        }

                        // Tombol Hapus
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, PgDanger)
                        ) {
                            Text(text = "Hapus", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PgDanger)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
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

                // Konfigurasi visual berdasarkan Tipe Transaksi
                val isIncome = currentTransaction.type == TransactionType.INCOME
                val amountColor = if (isIncome) PgPrimary else PgDanger
                val amountSign = if (isIncome) "+" else "-"

                // Konfigurasi Emoji Kategori
                val emoji = when (currentTransaction.category) {
                    TransactionCategory.FOOD -> "🍜"
                    TransactionCategory.TRANSPORT -> "🚗"
                    TransactionCategory.BILLS -> "🏠"
                    TransactionCategory.SALARY -> "💰"
                    TransactionCategory.OTHER -> "📦"
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. LARGE BADGE EMOJI - Lingkaran ikon dekoratif kategori
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = if (isIncome) PgPrimaryLight else Color(0xFFFEE8CC),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. LARGE COLOR NOMINAL DISPLAY
                    Text(
                        text = "$amountSign Rp ${formatCurrency(currentTransaction.amount)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = amountColor,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Keterangan Waktu Sekunder
                    Text(
                        text = formatTimestamp(currentTransaction.createdAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // TABEL RINCIAN INFORMASI DATA (Muted Card Background)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            DetailItemRow(label = "Nama", value = currentTransaction.description)

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                            )

                            DetailItemRow(label = "Kategori", value = "$emoji ${currentTransaction.category.displayName}")

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                            )

                            DetailItemRow(
                                label = "Tipe",
                                value = if (isIncome) "Pemasukan" else "Pengeluaran"
                            )
                        }
                    }
                }
            }
        }
    }
}

// Komponen Baris Rincian Sejajar Kiri-Kanan (Sesuai Gaya Tabel HTML Anda)
@Composable
private fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Transaksi", fontWeight = FontWeight.Bold) },
        text = { Text("Apakah Anda yakin ingin menghapus data transaksi ini?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = PgDanger, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun formatCurrency(amount: Double): String {
    return amount.toLong().toString().reversed().chunked(3).joinToString(".").reversed()
}

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val minute = dateTime.minute.toString().padStart(2, '0')
    val dayName = when (dateTime.dayOfWeek.name) {
        "MONDAY" -> "Senin"
        "TUESDAY" -> "Selasa"
        "WEDNESDAY" -> "Rabu"
        "THURSDAY" -> "Kamis"
        "FRIDAY" -> "Jumat"
        "SATURDAY" -> "Sabtu"
        "SUNDAY" -> "Minggu"
        else -> dateTime.dayOfWeek.name
    }
    return "$dayName, ${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year} · ${dateTime.hour}:$minute"
}