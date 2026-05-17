package com.example.noteai.presentation.screens.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.noteai.domain.model.PantryItem
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: PantryViewModel = koinViewModel()
) {
    val items by viewModel.pantryItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val groupedItems = remember(items) {
        items.groupBy { it.category }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inventory Dapur", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Bahan")
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Stok bahan kosong. Tambahkan sekarang!", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedItems.forEach { (category, categoryItems) ->
                    item {
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.typography.labelLarge.color.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                    }
                    items(categoryItems, key = { it.id }) { item ->
                        PantryInventoryCard(
                            item = item,
                            onIncrement = { viewModel.updateAmount(item.id, item.amount, 1.0) },
                            onDecrement = { viewModel.updateAmount(item.id, item.amount, -1.0) },
                            onDelete = { viewModel.removeItem(item.id) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (showAddDialog) {
            AddInventoryItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, amount, unit, category, minStock ->
                    viewModel.addItem(name, amount, unit, category, minStock)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PantryInventoryCard(
    item: PantryItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = item.amount <= item.minStock && item.minStock > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isLowStock) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Low Stock",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                " Low",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                    Text(
                        text = "Minimal: ${item.minStock} ${item.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isLowStock) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = "${item.amount} ${item.unit}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isLowStock) Color(0xFFB71C1C) else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurang")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    FilledIconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }
            }
        }
    }
}

@Composable
fun AddInventoryItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("gram") }
    var category by remember { mutableStateOf("Bahan Utama") }

    val categories = listOf("Bahan Utama", "Bumbu", "Sayuran", "Daging", "Minuman", "Lainnya")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Inventori Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Bahan (cth: Beras)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Jumlah") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Satuan") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = minStock,
                    onValueChange = { minStock = it },
                    label = { Text("Batas Stok Tipis (Minimal)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Kategori", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Sederhana: Hanya text field, idealnya pakai DropdownMenu
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        name,
                        amount.toDoubleOrNull() ?: 0.0,
                        unit,
                        category,
                        minStock.toDoubleOrNull() ?: 0.0
                    )
                },
                enabled = name.isNotBlank() && amount.isNotBlank()
            ) {
                Text("Simpan Stok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
