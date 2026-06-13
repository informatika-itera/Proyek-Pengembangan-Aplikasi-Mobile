package com.example.tripmate.presentation.screens.addedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tripmate.domain.model.Trip
import com.example.tripmate.presentation.screens.home.HomeUiState
import com.example.tripmate.presentation.screens.home.TripViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

// Format Long millis → "YYYY-MM-DD"
private fun millisToDateString(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}

// Format angka → "5.000.000"
private fun formatWithDots(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    if (digits.isEmpty()) return ""
    return try {
        val number = digits.toLong()
        NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
    } catch (e: Exception) {
        digits
    }
}

// Hapus titik → angka bersih untuk disimpan
private fun stripDots(formatted: String): String = formatted.replace(".", "").replace(",", "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTripScreen(
    tripId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: TripViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budgetDisplay by remember { mutableStateOf("") } // yang tampil dengan titik
    var budgetRaw by remember { mutableStateOf("") }     // yang disimpan tanpa titik

    var destinationError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }
    var budgetError by remember { mutableStateOf(false) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    val isEditMode = tripId != null
    val title = if (isEditMode) "Edit Trip" else "Tambah Trip"

    LaunchedEffect(tripId, uiState) {
        if (isEditMode && uiState is HomeUiState.Success) {
            val trip = (uiState as HomeUiState.Success).trips.find { it.id == tripId }
            trip?.let {
                destination = it.destination
                startDate = it.startDate
                endDate = it.endDate
                budgetRaw = it.budget.toLong().toString()
                budgetDisplay = formatWithDots(budgetRaw)
            }
        }
    }

    // DatePicker start
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let {
                        startDate = millisToDateString(it)
                        startDateError = false
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // DatePicker end
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let {
                        endDate = millisToDateString(it)
                        endDateError = false
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = destination,
                onValueChange = {
                    destination = it
                    destinationError = false
                },
                label = { Text("Destinasi") },
                placeholder = { Text("Contoh: Bali, Indonesia") },
                isError = destinationError,
                supportingText = if (destinationError) {
                    { Text("Destinasi tidak boleh kosong") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Tanggal Mulai — read only, buka DatePicker saat diklik
            OutlinedTextField(
                value = startDate,
                onValueChange = {},
                label = { Text("Tanggal Mulai") },
                placeholder = { Text("Pilih tanggal") },
                isError = startDateError,
                supportingText = if (startDateError) {
                    { Text("Tanggal mulai tidak boleh kosong") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pilih tanggal mulai")
                    }
                }
            )

            // Tanggal Selesai
            OutlinedTextField(
                value = endDate,
                onValueChange = {},
                label = { Text("Tanggal Selesai") },
                placeholder = { Text("Pilih tanggal") },
                isError = endDateError,
                supportingText = if (endDateError) {
                    { Text("Tanggal selesai tidak boleh kosong") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pilih tanggal selesai")
                    }
                }
            )

            // Budget dengan format titik ribuan
            OutlinedTextField(
                value = budgetDisplay,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    budgetRaw = digits
                    budgetDisplay = formatWithDots(digits)
                    budgetError = false
                },
                label = { Text("Budget (Rp)") },
                placeholder = { Text("Contoh: 5.000.000") },
                prefix = { Text("Rp ") },
                isError = budgetError,
                supportingText = if (budgetError) {
                    { Text("Masukkan budget yang valid") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    destinationError = destination.isBlank()
                    startDateError = startDate.isBlank()
                    endDateError = endDate.isBlank()
                    budgetError = budgetRaw.isBlank() || budgetRaw.toDoubleOrNull() == null

                    if (!destinationError && !startDateError && !endDateError && !budgetError) {
                        val trip = Trip(
                            id = tripId ?: 0L,
                            destination = destination.trim(),
                            startDate = startDate.trim(),
                            endDate = endDate.trim(),
                            budget = budgetRaw.toDouble(),
                            createdAt = System.currentTimeMillis()
                        )
                        if (isEditMode) {
                            viewModel.updateTrip(trip)
                        } else {
                            viewModel.insertTrip(trip)
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isEditMode) "Trip berhasil diupdate!" else "Trip berhasil ditambahkan!"
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Update Trip" else "Simpan Trip")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
