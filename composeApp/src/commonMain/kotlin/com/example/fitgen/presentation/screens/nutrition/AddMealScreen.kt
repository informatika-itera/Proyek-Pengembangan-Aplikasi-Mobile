package com.example.fitgen.presentation.screens.nutrition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitgen.domain.model.MealType
import com.example.fitgen.core.util.rememberCameraLauncher // Pastikan import ini sesuai dengan lokasi file CameraLauncher kamu
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddMealViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Inisialisasi peluncur kamera
    val cameraLauncher = rememberCameraLauncher { imageBytes ->
        if (imageBytes != null) {
            viewModel.analyzeFoodWithAI(imageBytes)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Makanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value         = uiState.namaMakanan,
                onValueChange = viewModel::onNamaChanged,
                label         = { Text("Nama Makanan *") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true
            )
            OutlinedTextField(
                value           = uiState.kaloriInput,
                onValueChange   = viewModel::onKaloriChanged,
                label           = { Text("Kalori (kkal) *") },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value           = uiState.proteinInput,
                    onValueChange   = viewModel::onProteinChanged,
                    label           = { Text("Protein (g)") },
                    modifier        = Modifier.weight(1f),
                    singleLine      = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value           = uiState.karbohidratInput,
                    onValueChange   = viewModel::onKarbohidratChanged,
                    label           = { Text("Karbo (g)") },
                    modifier        = Modifier.weight(1f),
                    singleLine      = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            OutlinedTextField(
                value           = uiState.lemakInput,
                onValueChange   = viewModel::onLemakChanged,
                label           = { Text("Lemak (g)") },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            MealTypeDropdown(
                selected = uiState.jenisMakan,
                onSelect = viewModel::onJenisMakanChanged,
                modifier = Modifier.fillMaxWidth()
            )

            // ==========================================
            // KODE SCANNER AI
            // ==========================================
            Spacer(modifier = Modifier.height(16.dp))

            // Pemisah Visual "ATAU"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                Text(
                    text = " ATAU ",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Scanner AI
            OutlinedButton(
                onClick = { cameraLauncher() }, // Memanggil fungsi kamera di sini
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFE91E63) // Warna pink/maroon accent
                ),
                border = BorderStroke(1.dp, Color(0xFFE91E63)),
                enabled = !uiState.isLoading // Matikan tombol saat AI sedang memproses
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan Makanan",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Ubah teks agar interaktif
                Text(text = if (uiState.isLoading) "Menganalisis..." else "Scan Makanan dengan AI")
            }

            Spacer(modifier = Modifier.height(8.dp))
            // ==========================================

            Button(
                onClick  = viewModel::saveMeal,
                enabled  = uiState.namaMakanan.isNotBlank() && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isLoading) "Menyimpan..." else "Simpan Makanan")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealTypeDropdown(
    selected: MealType,
    onSelect: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = modifier
    ) {
        OutlinedTextField(
            value         = selected.label,
            onValueChange = {},
            readOnly      = true,
            label         = { Text("Jenis Makan") },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier      = Modifier.fillMaxWidth().menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MealType.entries.forEach { type ->
                DropdownMenuItem(
                    text    = { Text(type.label) },
                    onClick = { onSelect(type); expanded = false }
                )
            }
        }
    }
}