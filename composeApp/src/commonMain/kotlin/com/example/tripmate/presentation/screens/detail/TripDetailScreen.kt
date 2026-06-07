package com.example.tripmate.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripmate.presentation.screens.home.HomeUiState
import com.example.tripmate.presentation.screens.home.TripViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TripViewModel = koinViewModel(),
    packingViewModel: PackingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val packingItems by packingViewModel.items.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var itemToDelete by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(tripId) {
        packingViewModel.loadItems(tripId)
    }

    val trip = when (val state = uiState) {
        is HomeUiState.Success -> state.trips.find { it.id == tripId }
        else -> null
    }

    val unchecked = packingItems.filter { !it.isChecked }
    val checked = packingItems.filter { it.isChecked }
    val checkedCount = checked.size
    val totalCount = packingItems.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Trip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus Trip",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0)
            )
        },
        floatingActionButton = {}  // FAB dipindah ke dalam konten, di baris input
    ) { padding ->
        when {
            trip == null && uiState is HomeUiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
            }
            trip == null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    Alignment.Center
                ) {
                    Text("Trip tidak ditemukan", color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // ── Info Card ──────────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Destinasi row: icon lokasi + nama
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = trip.destination,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )

                            // Tanggal mulai
                            DetailIconRow(
                                icon = Icons.Default.DateRange,
                                label = "Tanggal mulai",
                                value = trip.startDate,
                                valueColor = MaterialTheme.colorScheme.onSurface
                            )

                            // Tanggal selesai
                            DetailIconRow(
                                icon = Icons.Default.DateRange,
                                label = "Tanggal selesai",
                                value = trip.endDate,
                                valueColor = MaterialTheme.colorScheme.onSurface
                            )

                            // Budget — pakai icon wallet (wallet tidak ada di default, pakai AccountBalance atau simulasi)
                            DetailIconRow(
                                icon = Icons.Default.DateRange, // ganti dengan wallet icon jika tersedia di project
                                label = "Budget",
                                value = "Rp ${formatBudget(trip.budget)}",
                                valueColor = MaterialTheme.colorScheme.secondary,
                                valueBold = true
                            )
                        }
                    }

                    // ── Packing Section ────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // Header: "Yang dibawa" + counter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Yang dibawa",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (totalCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            "$checkedCount / $totalCount",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Label "Sudah dibawa" jika ada checked items
                            if (checked.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Sudah dibawa",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Unchecked items
                            unchecked.forEachIndexed { index, item ->
                                PackingRow(
                                    number = index + 1,
                                    name = item.name,
                                    isChecked = false,
                                    onToggle = {
                                        packingViewModel.toggleItem(item.id, item.isChecked)
                                    },
                                    onDelete = { itemToDelete = item.id }
                                )
                                if (index < unchecked.lastIndex || checked.isNotEmpty()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 52.dp),
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    )
                                }
                            }

                            // Checked items
                            checked.forEachIndexed { index, item ->
                                PackingRow(
                                    number = unchecked.size + index + 1,
                                    name = item.name,
                                    isChecked = true,
                                    onToggle = {
                                        packingViewModel.toggleItem(item.id, item.isChecked)
                                    },
                                    onDelete = { itemToDelete = item.id }
                                )
                                if (index < checked.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 52.dp),
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    )
                                }
                            }

                            if (totalCount == 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Belum ada item packing",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                            )
                            Spacer(Modifier.height(12.dp))

                            // ── Input Row: TextField + tombol + + FAB edit ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newItemName,
                                    onValueChange = { newItemName = it },
                                    placeholder = {
                                        Text(
                                            "Tambah item...",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        focusedBorderColor = MaterialTheme.colorScheme.secondary
                                    )
                                )

                                // Tombol +
                                Button(
                                    onClick = {
                                        if (newItemName.isNotBlank()) {
                                            packingViewModel.addItem(tripId, newItemName)
                                            newItemName = ""
                                        }
                                    },
                                    enabled = newItemName.isNotBlank(),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.size(48.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Tambah item",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // FAB Edit Trip — teal
                                FloatingActionButton(
                                    onClick = { onNavigateToEdit(tripId) },
                                    modifier = Modifier.size(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Trip",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    // ── Dialog hapus trip ──────────────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Trip", fontWeight = FontWeight.SemiBold) },
            text = {
                Text("Yakin ingin menghapus perjalanan ke ${trip?.destination}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    trip?.let { viewModel.deleteTrip(it.id) }
                    showDeleteDialog = false
                    onNavigateBack()
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // ── Dialog hapus item ──────────────────────────────────────────────────
    itemToDelete?.let { id ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus item", fontWeight = FontWeight.SemiBold) },
            text = { Text("Yakin ingin menghapus item ini?") },
            confirmButton = {
                TextButton(onClick = {
                    packingViewModel.deleteItem(id)
                    itemToDelete = null
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

// ── Composable: PackingRow ─────────────────────────────────────────────────
@Composable
private fun PackingRow(
    number: Int,
    name: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Nomor urut — Box teal muda
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Nama item
        Text(
            text = name,
            fontSize = 14.sp,
            color = if (isChecked)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.onSurface,
            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )

        // Tombol delete — merah muda
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus item",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Tombol ceklis — teal muda
        IconButton(
            onClick = onToggle,
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (isChecked)
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = if (isChecked) "Batal ceklis" else "Ceklis",
                    tint = if (isChecked)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ── Composable: DetailIconRow ──────────────────────────────────────────────
@Composable
private fun DetailIconRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    valueBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(17.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (valueBold) FontWeight.SemiBold else FontWeight.Medium,
            color = valueColor
        )
    }
}

// ── Helper ─────────────────────────────────────────────────────────────────
private fun formatBudget(budget: Double): String =
    budget.toLong().toString().reversed().chunked(3).joinToString(".").reversed()