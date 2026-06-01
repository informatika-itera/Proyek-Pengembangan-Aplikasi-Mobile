package com.example.tripmate.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
                    Text("Detail Trip", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0)
            )
        },
        floatingActionButton = {
            trip?.let {
                FloatingActionButton(
                    onClick = { onNavigateToEdit(tripId) },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Trip")
                }
            }
        }
    ) { padding ->
        when {
            trip == null && uiState is HomeUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            trip == null -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Text("Trip tidak ditemukan", color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(trip.destination,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(.4f))
                            DetailRow("Tanggal mulai", trip.startDate)
                            DetailRow("Tanggal selesai", trip.endDate)
                            DetailRow("Budget", "Rp ${formatBudget(trip.budget)}")
                        }
                    }

                    // Packing list card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Yang dibawa",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface)
                                if (totalCount > 0) {
                                    Text("$checkedCount / $totalCount",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Unchecked items
                            unchecked.forEachIndexed { index, item ->
                                PackingRow(
                                    number = index + 1,
                                    name = item.name,
                                    isChecked = false,
                                    onToggle = { packingViewModel.toggleItem(item.id, item.isChecked) },
                                    onDelete = { itemToDelete = item.id }
                                )
                                if (index < unchecked.lastIndex || checked.isNotEmpty()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 36.dp),
                                        color = MaterialTheme.colorScheme.outline.copy(.25f))
                                }
                            }

                            // Checked items (dicoret, di bawah)
                            if (checked.isNotEmpty()) {
                                if (unchecked.isNotEmpty()) Spacer(Modifier.height(6.dp))
                                Text("Sudah dibawa",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(bottom = 6.dp))
                                checked.forEachIndexed { index, item ->
                                    PackingRow(
                                        number = unchecked.size + index + 1,
                                        name = item.name,
                                        isChecked = true,
                                        onToggle = { packingViewModel.toggleItem(item.id, item.isChecked) },
                                        onDelete = { itemToDelete = item.id }
                                    )
                                    if (index < checked.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(start = 36.dp),
                                            color = MaterialTheme.colorScheme.outline.copy(.25f))
                                    }
                                }
                            }

                            // Input tambah item
                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(.3f))
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newItemName,
                                    onValueChange = { newItemName = it },
                                    placeholder = {
                                        Text("Tambah item...", fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        packingViewModel.addItem(tripId, newItemName)
                                        newItemName = ""
                                    },
                                    enabled = newItemName.isNotBlank(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Tambah",
                                        modifier = Modifier.padding(0.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(72.dp))
                }
            }
        }
    }

    // Dialog hapus trip
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Trip") },
            text = { Text("Yakin ingin menghapus perjalanan ke ${trip?.destination}?") },
            confirmButton = {
                TextButton(onClick = {
                    trip?.let { viewModel.deleteTrip(it.id) }
                    showDeleteDialog = false
                    onNavigateBack()
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    // Dialog hapus item
    itemToDelete?.let { id ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus item") },
            text = { Text("Yakin ingin menghapus item ini?") },
            confirmButton = {
                TextButton(onClick = {
                    packingViewModel.deleteItem(id)
                    itemToDelete = null
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Batal") }
            }
        )
    }
}

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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$number",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(22.dp)
        )
        HorizontalDivider(
            modifier = Modifier
                .height(16.dp)
                .width(1.dp)
                .padding(horizontal = 0.dp),
            color = MaterialTheme.colorScheme.outline.copy(.4f)
        )
        Spacer(Modifier.width(10.dp))
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
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus item",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(.5f))
        }
        IconButton(onClick = onToggle) {
            Icon(
                Icons.Default.Check,
                contentDescription = if (isChecked) "Batal" else "Centang",
                tint = if (isChecked)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun formatBudget(budget: Double): String =
    budget.toLong().toString().reversed().chunked(3).joinToString(".").reversed()
