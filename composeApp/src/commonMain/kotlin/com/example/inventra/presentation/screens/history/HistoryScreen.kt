package com.example.inventra.presentation.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.inventra.core.localization.AppStrings
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.presentation.components.EmptyState
import com.example.inventra.presentation.components.InventRaBottomNav
import com.example.inventra.presentation.components.LoadingIndicator
import com.example.inventra.presentation.util.formatDateOnly
import org.koin.compose.viewmodel.koinViewModel
import com.example.inventra.core.util.rememberImagePickerLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val viewModel: HistoryViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == com.example.inventra.domain.model.UserRole.ADMIN
    val strings = AppStrings.current
    
    var showImageSourceOptions by remember { mutableStateOf(false) }
    var recordToReturn by remember { mutableStateOf<Long?>(null) }
    val imagePicker = rememberImagePickerLauncher { bytes, fileName ->
        recordToReturn?.let { id ->
            viewModel.returnItem(id, bytes, fileName)
            recordToReturn = null
        }
    }

    if (showImageSourceOptions) {
        AlertDialog(
            onDismissRequest = { 
                showImageSourceOptions = false
                recordToReturn = null 
            },
            title = { Text(strings.returnProofTitle) },
            text = { Text(strings.returnProofDesc) },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceOptions = false
                    imagePicker.takePhoto()
                }) {
                    Text(strings.camera)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceOptions = false
                    imagePicker.pickImage()
                }) {
                    Text(strings.gallery)
                }
            }
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(strings.pending, strings.active, strings.all)
    var showFullImageProofUrl by remember { mutableStateOf<String?>(null) }

    if (showFullImageProofUrl != null) {
        AlertDialog(
            onDismissRequest = { showFullImageProofUrl = null },
            text = {
                AsyncImage(
                    model = showFullImageProofUrl,
                    contentDescription = "Bukti Pengembalian",
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            },
            confirmButton = {
                TextButton(onClick = { showFullImageProofUrl = null }) { Text(strings.close) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.borrowing,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            InventRaBottomNav(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = Color.Transparent
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
                        title = strings.noHistoryTitle,
                        description = strings.noHistoryDesc
                    )
                }

                is HistoryUiState.Success -> {
                        val displayedRecords = when (selectedTab) {
                        0 -> state.records.filter { 
                            it.status == BorrowStatus.PENDING || it.status == BorrowStatus.PENDING_RETURN 
                        }
                        1 -> state.records.filter {
                            it.status == BorrowStatus.ACTIVE || it.status == BorrowStatus.OVERDUE
                        }
                        else -> state.records
                    }

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
                                        "$overdueCount ${strings.overdueAlert}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        strings.takeAction,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    if (selectedTab == 0) {
                        val pendingCount = state.records.count { 
                            it.status == BorrowStatus.PENDING || it.status == BorrowStatus.PENDING_RETURN 
                        }
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
                                        "$pendingCount ${strings.pendingActionDesc}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }

                    if (displayedRecords.isEmpty()) {
                        EmptyState(
                            title = when (selectedTab) {
                                0 -> strings.noPendingTitle
                                1 -> strings.noActiveTitle
                                else -> strings.noHistoryTitle
                            },
                            description = when (selectedTab) {
                                0 -> strings.noPendingDesc
                                1 -> strings.noActiveDesc
                                else -> strings.noHistoryDesc
                            }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = displayedRecords,
                                key = { record -> record.id } // FIX: avoid unnecessary recomposition
                            ) { record ->
                                BorrowRecordCard(
                                    record = record,
                                    isAdmin = isAdmin,
                                    onApprove = if (isAdmin && record.status == BorrowStatus.PENDING) {
                                        { viewModel.approveRequest(record.id) }
                                    } else null,
                                    onApproveReturn = if (isAdmin && record.status == BorrowStatus.PENDING_RETURN) {
                                        { viewModel.approveReturn(record.id) }
                                    } else null,
                                    onReturn = if (record.status == BorrowStatus.ACTIVE ||
                                        record.status == BorrowStatus.OVERDUE) {
                                        { 
                                            recordToReturn = record.id
                                            showImageSourceOptions = true
                                        }
                                    } else null,
                                    onViewProof = { url -> showFullImageProofUrl = url }
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
    isAdmin: Boolean = false,
    onApprove: (() -> Unit)? = null,
    onApproveReturn: (() -> Unit)? = null,
    onReturn: (() -> Unit)? = null,
    onViewProof: (String) -> Unit = {}
) {
    val isOverdue = record.status == BorrowStatus.OVERDUE
    val isReturned = record.status == BorrowStatus.RETURNED
    val isPending = record.status == BorrowStatus.PENDING
    val isPendingReturn = record.status == BorrowStatus.PENDING_RETURN
    val strings = AppStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                isPending -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                isPendingReturn -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
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
                Icon(
                    imageVector = when {
                        isReturned -> Icons.Default.CheckCircle
                        isOverdue -> Icons.Default.Warning
                        isPending -> Icons.Default.Pending
                        isPendingReturn -> Icons.AutoMirrored.Filled.AssignmentReturn
                        else -> Icons.Default.History
                    },
                    contentDescription = null,
                    tint = when {
                        isReturned -> MaterialTheme.colorScheme.secondary
                        isOverdue -> MaterialTheme.colorScheme.error
                        isPending -> MaterialTheme.colorScheme.secondary
                        isPendingReturn -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.itemName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${strings.borrower}: ${record.borrowerName} (${record.borrowerDivision.ifBlank { "-" }})",
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
                            "${strings.dueDate}: ${record.dueDate.formatDateOnly()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOverdue) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.outline
                        )
                    }
                    if ((isReturned || isPendingReturn) && record.returnDate != null) {
                        Text(
                            "${strings.returnDate}: ${record.returnDate.formatDateOnly()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (record.fineAmount > 0) {
                        Text(
                            "${strings.fine}: Rp ${record.fineAmount.toLocaleString()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Bukti Pengembalian untuk Admin
                    if (isAdmin && !record.returnProofUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedCard(
                            onClick = { onViewProof(record.returnProofUrl) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(60.dp)
                        ) {
                            AsyncImage(
                                model = record.returnProofUrl,
                                contentDescription = "Proof",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            "Lihat Bukti Foto",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onViewProof(record.returnProofUrl) }
                        )
                    }
                }

                Surface(
                    color = when {
                        isReturned -> MaterialTheme.colorScheme.secondaryContainer
                        isOverdue -> MaterialTheme.colorScheme.errorContainer
                        isPending -> MaterialTheme.colorScheme.tertiaryContainer
                        isPendingReturn -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (record.status) {
                            BorrowStatus.RETURNED -> strings.returned
                            BorrowStatus.OVERDUE -> strings.overdue
                            BorrowStatus.ACTIVE -> strings.active
                            BorrowStatus.PENDING -> strings.pending
                            BorrowStatus.PENDING_RETURN -> strings.waitingApproval
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isReturned -> MaterialTheme.colorScheme.onSecondaryContainer
                            isOverdue -> MaterialTheme.colorScheme.error
                            isPending -> MaterialTheme.colorScheme.onTertiaryContainer
                            isPendingReturn -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (onApprove != null || onApproveReturn != null || onReturn != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
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
                            Text(strings.approveBorrow, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    if (onApproveReturn != null) {
                        Button(
                            onClick = onApproveReturn,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.approveReturn, style = MaterialTheme.typography.labelMedium)
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
                                Icons.AutoMirrored.Filled.AssignmentReturn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.returnItem, style = MaterialTheme.typography.labelMedium)
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
