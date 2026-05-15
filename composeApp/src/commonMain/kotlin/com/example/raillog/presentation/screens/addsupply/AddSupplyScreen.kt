package com.example.raillog.presentation.screens.addsupply

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.raillog.domain.model.PartCategory
import com.example.raillog.domain.model.Priority
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSupplyScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddSupplyViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigasi kembali otomatis jika berhasil disimpan
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Suku Cadang") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.onEvent(AddSupplyEvent.SaveItem) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Simpan", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simpan")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.error != null) {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = uiState.partCode,
                onValueChange = { viewModel.onEvent(AddSupplyEvent.PartCodeChanged(it)) },
                label = { Text("Kode Part *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onEvent(AddSupplyEvent.NameChanged(it)) },
                label = { Text("Nama Komponen *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Kategori Subsistem", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PartCategory.entries) { category ->
                    FilterChip(
                        selected = uiState.category == category,
                        onClick = { viewModel.onEvent(AddSupplyEvent.CategoryChanged(category)) },
                        label = { Text(category.displayName) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.quantity,
                    onValueChange = { viewModel.onEvent(AddSupplyEvent.QuantityChanged(it)) },
                    label = { Text("Jumlah") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.unit,
                    onValueChange = { viewModel.onEvent(AddSupplyEvent.UnitChanged(it)) },
                    label = { Text("Satuan") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = uiState.supplier,
                onValueChange = { viewModel.onEvent(AddSupplyEvent.SupplierChanged(it)) },
                label = { Text("Pemasok (Supplier)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Prioritas Pengadaan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Priority.entries) { priority ->
                    FilterChip(
                        selected = uiState.priority == priority,
                        onClick = { viewModel.onEvent(AddSupplyEvent.PriorityChanged(priority)) },
                        label = { Text(priority.displayName) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onEvent(AddSupplyEvent.NotesChanged(it)) },
                label = { Text("Catatan Tambahan") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
        }
    }
}