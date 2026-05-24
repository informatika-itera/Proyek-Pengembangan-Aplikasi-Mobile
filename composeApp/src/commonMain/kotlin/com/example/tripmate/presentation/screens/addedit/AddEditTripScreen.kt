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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
    var budget by remember { mutableStateOf("") }

    var destinationError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }
    var budgetError by remember { mutableStateOf(false) }

    val isEditMode = tripId != null
    val title = if (isEditMode) "Edit Trip" else "Tambah Trip"

    // Load existing trip data if edit mode
    LaunchedEffect(tripId, uiState) {
        if (isEditMode && uiState is HomeUiState.Success) {
            val trip = (uiState as HomeUiState.Success).trips.find { it.id == tripId }
            trip?.let {
                destination = it.destination
                startDate = it.startDate
                endDate = it.endDate
                budget = it.budget.toLong().toString()
            }
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
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

            OutlinedTextField(
                value = startDate,
                onValueChange = {
                    startDate = it
                    startDateError = false
                },
                label = { Text("Tanggal Mulai") },
                placeholder = { Text("YYYY-MM-DD") },
                isError = startDateError,
                supportingText = if (startDateError) {
                    { Text("Tanggal mulai tidak boleh kosong") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = {
                    endDate = it
                    endDateError = false
                },
                label = { Text("Tanggal Selesai") },
                placeholder = { Text("YYYY-MM-DD") },
                isError = endDateError,
                supportingText = if (endDateError) {
                    { Text("Tanggal selesai tidak boleh kosong") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = budget,
                onValueChange = {
                    budget = it
                    budgetError = false
                },
                label = { Text("Budget (Rp)") },
                placeholder = { Text("Contoh: 5000000") },
                isError = budgetError,
                supportingText = if (budgetError) {
                    { Text("Masukkan budget yang valid") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Validate
                    destinationError = destination.isBlank()
                    startDateError = startDate.isBlank()
                    endDateError = endDate.isBlank()
                    budgetError = budget.isBlank() || budget.toDoubleOrNull() == null

                    if (!destinationError && !startDateError && !endDateError && !budgetError) {
                        val trip = Trip(
                            id = tripId ?: 0L,
                            destination = destination.trim(),
                            startDate = startDate.trim(),
                            endDate = endDate.trim(),
                            budget = budget.toDouble(),
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
