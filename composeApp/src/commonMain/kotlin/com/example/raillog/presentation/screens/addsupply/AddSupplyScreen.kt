package com.example.raillog.presentation.screens.addsupply

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.raillog.domain.model.PartCategory
import com.example.raillog.domain.model.Priority
import org.koin.compose.viewmodel.koinViewModel

private val SuccessEmerald = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSupplyScreen(
    itemId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddSupplyViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(itemId) {
        if (itemId != null) {
            viewModel.loadItemForEdit(itemId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Edit Suku Cadang" else "Tambah Suku Cadang",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = { viewModel.onEvent(AddSupplyEvent.SaveItem) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Simpan")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (uiState.isEditMode) "Simpan Perubahan" else "Simpan Data",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            if (uiState.generalError != null) {
                Text(
                    text = uiState.generalError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onEvent(AddSupplyEvent.NameChanged(it)) },
                label = { Text("Nama Komponen") },
                placeholder = { Text("Masukkan nama komponen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(4.dp)
            )

            OutlinedTextField(
                value = uiState.partCode,
                onValueChange = {
                    viewModel.onEvent(AddSupplyEvent.PartCodeChanged(it.uppercase()))
                },
                label = { Text("ID Part (Nomor Seri)") },
                placeholder = { Text("Contoh: PRT-BOGI-001") },
                isError = uiState.partCodeError != null,
                supportingText = {
                    if (uiState.partCodeError != null) {
                        Text(text = uiState.partCodeError!!)
                    }
                },
                trailingIcon = {
                    if (uiState.partCodeError != null) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(4.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Kategori Subsistem", style = MaterialTheme.typography.titleSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PartCategory.entries) { category ->
                        val isSelected = uiState.category == category
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.onEvent(AddSupplyEvent.CategoryChanged(category))
                            },
                            label = { Text(category.displayName) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SuccessEmerald.copy(alpha = 0.2f),
                                selectedLabelColor = SuccessEmerald,
                                selectedLeadingIconColor = SuccessEmerald
                            )
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.quantity,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() })
                            viewModel.onEvent(AddSupplyEvent.QuantityChanged(it))
                    },
                    label = { Text("Jumlah") },
                    placeholder = { Text("0") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace)
                )
                OutlinedTextField(
                    value = uiState.unit,
                    onValueChange = { viewModel.onEvent(AddSupplyEvent.UnitChanged(it)) },
                    label = { Text("Satuan") },
                    placeholder = { Text("Unit/Pcs/Kg") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp)
                )
            }

            OutlinedTextField(
                value = uiState.supplier,
                onValueChange = { viewModel.onEvent(AddSupplyEvent.SupplierChanged(it)) },
                label = { Text("Pemasok (Supplier)") },
                placeholder = { Text("Nama supplier") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(4.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Prioritas Pengadaan", style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Priority.entries.forEach { priority ->
                        val isSelected = uiState.priority == priority
                        val activeColor = if (priority == Priority.CRITICAL)
                            MaterialTheme.colorScheme.error else SuccessEmerald

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onEvent(AddSupplyEvent.PriorityChanged(priority)) },
                            label = {
                                Text(priority.displayName, modifier = Modifier.fillMaxWidth())
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = activeColor.copy(alpha = 0.15f),
                                selectedLabelColor = activeColor
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onEvent(AddSupplyEvent.NotesChanged(it)) },
                label = { Text("Deskripsi Teknis") },
                placeholder = { Text("Spesifikasi, material, atau catatan khusus...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}