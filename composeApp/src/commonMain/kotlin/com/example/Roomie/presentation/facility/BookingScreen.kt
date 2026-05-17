package com.example.Roomie.presentation.facility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    roomId: String,
    onBack: () -> Unit,
    viewModel: BookingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // UI states for pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(roomId) {
        viewModel.loadRoomDetail(roomId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Form Peminjaman", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Room Info Brief
            state.room?.let { r ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(r.name, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Target Ruangan", style = MaterialTheme.typography.labelSmall)
                            Text("Lantai ${r.floor} - GKU 2", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Form Inputs
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Detail Peminjaman", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                // Date Picker Trigger
                Box {
                    OutlinedTextField(
                        value = state.date,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Tanggal") },
                        placeholder = { Text("Klik untuk pilih tanggal") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Start Time Trigger
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.startTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Mulai") },
                            placeholder = { Text("00:00") },
                            leadingIcon = { Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showStartTimePicker = true })
                    }

                    // End Time Trigger
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.endTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selesai") },
                            placeholder = { Text("00:00") },
                            leadingIcon = { Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showEndTimePicker = true })
                    }
                }

                OutlinedTextField(
                    value = state.purpose,
                    onValueChange = viewModel::onPurposeChange,
                    label = { Text("Tujuan Peminjaman") },
                    placeholder = { Text("Contoh: Rapat Himpunan / Belajar") },
                    leadingIcon = { Icon(Icons.Default.HistoryEdu, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = state.attendees,
                    onValueChange = viewModel::onAttendeesChange,
                    label = { Text("Estimasi Peserta") },
                    placeholder = { Text("Contoh: 20 Orang") },
                    leadingIcon = { Icon(Icons.Default.Groups, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::submitBooking,
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("AJUKAN SEKARANG", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }
        }
    }

    // --- DATE PICKER DIALOG ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = Instant.fromEpochMilliseconds(it)
                        val date = instant.toLocalDateTime(TimeZone.UTC)
                        viewModel.onDateChange("${date.dayOfMonth}/${date.monthNumber}/${date.year}")
                    }
                    showDatePicker = false
                }) { Text("PILIH") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("BATAL") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- TIME PICKER DIALOG ---
    if (showStartTimePicker || showEndTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false; showEndTimePicker = false },
            title = { Text("Pilih Jam") },
            text = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val timeStr = "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                    if (showStartTimePicker) viewModel.onStartTimeChange(timeStr)
                    else viewModel.onEndTimeChange(timeStr)
                    showStartTimePicker = false
                    showEndTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false; showEndTimePicker = false }) { Text("BATAL") }
            }
        )
    }

    if (state.isSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState(); onBack() },
            title = { Text("Pengajuan Terkirim", fontWeight = FontWeight.Bold) },
            text = { Text("Berhasil mengajukan peminjaman. Silakan cek status di menu Jadwal.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetState(); onBack() }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
