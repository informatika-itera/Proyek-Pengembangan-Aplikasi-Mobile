package com.example.inventra.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.presentation.components.EmptyState
import com.example.inventra.presentation.components.InventRaBottomNav
import com.example.inventra.presentation.components.LoadingIndicator
import com.example.inventra.presentation.util.formatDateOnly
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val viewModel: HistoryViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pending", "Aktif", "Semua")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Peminjaman",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            InventRaBottomNav(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            when (val state = uiState) {
                is HistoryUiState.Loading -> LoadingIndicator()

                is HistoryUiState.Empty -> {
                    EmptyState(
                        title = "Belum Ada Riwayat",
                        description = "Belum ada transaksi peminjaman yang tercatat."
                    )
                }

                is HistoryUiState.Success -> {
                    val displayedRecords = when (selectedTab) {
                        0 -> state.records.filter { it.status == BorrowStatus.PENDING }
                        1 -> state.records.filter {
                            it.status == BorrowStatus.ACTIVE || it.status == BorrowStatus.OVERDUE
                        }
                        else -> state.records
                    }

                    // Alert overdue
                    val overdueCount = state.records.count { it.status == BorrowStatus.OVERDUE }
                    if (overdueCount > 0) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "$overdueCount item overdue!",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text("Segera tindak lanjuti.", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Info pending
                    if (selectedTab == 0) {
                        val pendingCount = state.records.count { it.status == BorrowStatus.PENDING }
                        if (pendingCount > 0) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Pending,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "$pendingCount permintaan menunggu persetujuan Admin.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }

                    if (displayedRecords.isEmpty()) {
                        EmptyState(
                            title = "Tidak Ada Data",
                            description = when (selectedTab) {
                                0 -> "Tidak ada permintaan pending saat ini."
                                1 -> "Tidak ada peminjaman aktif saat ini."
                                else -> "Belum ada riwayat peminjaman."
                            }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(displayedRecords) { record ->
                                BorrowRecordCard(
                                    record = record,
                                    onApprove = if (record.status == BorrowStatus.PENDING) {
                                        { viewModel.approveRequest(record.id) }
                                    } else null,
                                    onReturn = if (record.status == BorrowStatus.ACTIVE || record.status == BorrowStatus.OVERDUE) {
                                        { viewModel.returnItem(record.id) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BorrowRecordCard(
    record: BorrowRecord,
    onApprove: (() -> Unit)? = null,
    onReturn: (() -> Unit)? = null
) {
    val isOverdue = record.status == BorrowStatus.OVERDUE
    val isReturned = record.status == BorrowStatus.RETURNED
    val isPending = record.status == BorrowStatus.PENDING

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                isPending -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                isReturned -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icon status
                Icon(
                    imageVector = when {
                        isReturned -> Icons.Default.CheckCircle
                        isOverdue -> Icons.Default.Warning
                        isPending -> Icons.Default.Pending
                        else -> Icons.Default.History
                    },
                    contentDescription = null,
                    tint = when {
                        isReturned -> MaterialTheme.colorScheme.secondary
                        isOverdue -> MaterialTheme.colorScheme.error
                        isPending -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.itemName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Peminjam: ${record.borrowerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        "Tanggal: ${record.borrowDate.formatDateOnly()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (!isPending) {
                        Text(
                            "Jatuh tempo: ${record.dueDate.formatDateOnly()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOverdue) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.outline
                        )
                    }
                    if (isReturned && record.returnDate != null) {
                        Text(
                            "Dikembalikan: ${record.returnDate.formatDateOnly()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (record.fineAmount > 0) {
                        Text(
                            "Denda: Rp ${record.fineAmount.toLocaleString()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Status badge
                Surface(
                    color = when {
                        isReturned -> MaterialTheme.colorScheme.secondaryContainer
                        isOverdue -> MaterialTheme.colorScheme.errorContainer
                        isPending -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (record.status) {
                            BorrowStatus.RETURNED -> "SELESAI"
                            BorrowStatus.OVERDUE -> "OVERDUE"
                            BorrowStatus.ACTIVE -> "AKTIF"
                            BorrowStatus.PENDING -> "PENDING"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isReturned -> MaterialTheme.colorScheme.onSecondaryContainer
                            isOverdue -> MaterialTheme.colorScheme.error
                            isPending -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Action buttons
            if (onApprove != null || onReturn != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onApprove != null) {
                        Button(
                            onClick = onApprove,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    if (onReturn != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onReturn,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.AssignmentReturn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kembalikan", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

private fun Long.toLocaleString(): String {
    return this.toString().reversed().chunked(3).joinToString(".").reversed()
}