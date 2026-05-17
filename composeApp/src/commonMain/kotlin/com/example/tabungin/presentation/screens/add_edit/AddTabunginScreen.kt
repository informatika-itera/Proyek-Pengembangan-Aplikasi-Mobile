package com.example.tabungin.presentation.screens.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import org.koin.core.parameter.parametersOf

val ICON_OPTIONS  = listOf("🎯","🏠","✈️","📱","🎓","🚗","💍","🏋️","💻","🎮","👜","🏝️","📚","🍔","⚽")
val COLOR_OPTIONS = listOf(
    "#4CAF50","#2196F3","#FF9800","#E91E63",
    "#9C27B0","#00BCD4","#FF5722","#607D8B"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditScreen(
    targetId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditViewModel = koinViewModel(parameters = { parametersOf(targetId) })
) {
    val uiState     by viewModel.uiState.collectAsState()
    val scrollState  = rememberScrollState()

    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onNavigateBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Edit Target" else "Tambah Target",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value         = uiState.nama,
                onValueChange = viewModel::onNamaChange,
                label         = { Text("Nama Target *") },
                placeholder   = { Text("Contoh: Beli Laptop") },
                singleLine    = true,
                isError       = uiState.nama.isEmpty(),
                modifier      = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value           = uiState.targetAmount,
                onValueChange   = viewModel::onTargetAmountChange,
                label           = { Text("Jumlah Target (Rp) *") },
                placeholder     = { Text("Contoh: 5000000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine      = true,
                modifier        = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value         = uiState.deadline,
                onValueChange = viewModel::onDeadlineChange,
                label         = { Text("Deadline * (yyyy-MM-dd)") },
                placeholder   = { Text("Contoh: 2025-12-31") },
                singleLine    = true,
                isError       = uiState.deadline.isEmpty(),
                modifier      = Modifier.fillMaxWidth()
            )

            // ── Icon Picker ──────────────────────────────
            Text("Pilih Ikon", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                ICON_OPTIONS.forEach { icon ->
                    FilterChip(
                        selected = uiState.icon == icon,
                        onClick  = { viewModel.onIconChange(icon) },
                        label    = { Text(icon, style = MaterialTheme.typography.titleMedium) }
                    )
                }
            }

            // ── Color Picker ─────────────────────────────
            Text("Pilih Warna", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                COLOR_OPTIONS.forEach { color ->
                    FilterChip(
                        selected = uiState.warna == color,
                        onClick  = { viewModel.onWarnaChange(color) },
                        label    = { Text(color, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Button(
                onClick  = viewModel::saveTarget,
                enabled  = viewModel.isFormValid && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color    = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (uiState.isEditMode) "Perbarui Target" else "Simpan Target")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    uiState.error?.let { msg ->
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title  = { Text("Gagal Menyimpan") },
            text   = { Text(msg) },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) { Text("OK") }
            }
        )
    }
}
