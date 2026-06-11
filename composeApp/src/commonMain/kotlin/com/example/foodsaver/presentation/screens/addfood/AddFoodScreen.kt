package com.example.foodsaver.presentation.screens.addfood

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.presentation.components.FoodSaverDropdown
import com.example.foodsaver.presentation.components.FoodSaverTextField
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    foodId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddFoodViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(foodId) {
        if (foodId != null && foodId > 0) {
            viewModel.loadFood(foodId)
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    if (showDatePicker) {
        AddFoodDatePicker(
            currentDate = state.expiryDate,
            onDateSelected = { viewModel.onDateChange(it) },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            AddFoodTopBar(
                isEditMode = foodId != null,
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .testTag("add_food_scroll_column"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BasicInfoSection(state, viewModel)

                    StorageSection(state, viewModel, onShowDatePicker = { showDatePicker = true })

                    NotesSection(state, viewModel)

                    state.error?.let {
                        Text(
                            it, 
                            color = MaterialTheme.colorScheme.error, 
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp).testTag("txt_add_food_error"),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SaveButton(
                        isEditMode = foodId != null,
                        onClick = viewModel::saveFood
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFoodTopBar(isEditMode: Boolean, onBackClick: () -> Unit) {
    TopAppBar(
        title = { 
            Text(
                if (isEditMode) "Ubah Makanan" else "Tambah Makanan",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            ) 
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFoodDatePicker(
    currentDate: Instant,
    onDateSelected: (Instant) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.toEpochMilliseconds()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(Instant.fromEpochMilliseconds(it))
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_confirm_date")
            ) { Text("Pilih", color = MaterialTheme.colorScheme.primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                todayContentColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun BasicInfoSection(state: AddFoodUiState, viewModel: AddFoodViewModel) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Informasi Dasar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            
            FoodSaverTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Nama Makanan",
                placeholder = "Contoh: Susu Sapi",
                modifier = Modifier.testTag("tf_food_name")
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FoodSaverTextField(
                    value = state.quantity,
                    onValueChange = viewModel::onQuantityChange,
                    label = "Jumlah",
                    modifier = Modifier.weight(1f).testTag("tf_food_quantity"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                FoodSaverDropdown(
                    label = "Satuan",
                    options = FoodItem.UNITS,
                    selectedOption = state.unit,
                    onOptionSelected = viewModel::onUnitChange,
                    modifier = Modifier.weight(1.2f).testTag("dropdown_unit")
                )
            }

            FoodSaverDropdown(
                label = "Kategori",
                options = FoodItem.CATEGORIES,
                selectedOption = state.category,
                onOptionSelected = viewModel::onCategoryChange,
                modifier = Modifier.testTag("dropdown_category")
            )
        }
    }
}

@Composable
private fun StorageSection(
    state: AddFoodUiState, 
    viewModel: AddFoodViewModel,
    onShowDatePicker: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Penyimpanan & Masa Kesegaran", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

            FoodSaverDropdown(
                label = "Lokasi Penyimpanan",
                options = FoodItem.STORAGE_LOCATIONS,
                selectedOption = state.storageLocation,
                onOptionSelected = viewModel::onStorageLocationChange,
                modifier = Modifier.testTag("dropdown_location")
            )

            FoodSaverTextField(
                value = state.expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                onValueChange = {},
                label = "Estimasi Kedaluwarsa",
                modifier = Modifier.testTag("tf_expiry_date"),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = onShowDatePicker, modifier = Modifier.testTag("btn_open_date_picker")) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pilih Tanggal", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                supportingText = "Aplikasi menyarankan tanggal berdasarkan kategori, kamu bisa mengubahnya manual."
            )
        }
    }
}

@Composable
private fun NotesSection(state: AddFoodUiState, viewModel: AddFoodViewModel) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Catatan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 8.dp))
            FoodSaverTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Catatan",
                placeholder = "Catatan opsional...",
                modifier = Modifier.testTag("tf_food_notes"),
                minLines = 3,
                singleLine = false
            )
        }
    }
}

@Composable
private fun SaveButton(isEditMode: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("add_food_save_button"),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            if (isEditMode) "Simpan Perubahan" else "Simpan Makanan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
