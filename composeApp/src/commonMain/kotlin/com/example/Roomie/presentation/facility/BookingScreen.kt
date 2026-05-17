package com.example.Roomie.presentation.facility

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    roomId: String,
    onBack: () -> Unit,
    viewModel: BookingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
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
                
                OutlinedTextField(
                    value = state.date,
                    onValueChange = viewModel::onDateChange,
                    label = { Text("Pilih Tanggal") },
                    placeholder = { Text("Contoh: 15 Mei 2026") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.startTime,
                        onValueChange = viewModel::onStartTimeChange,
                        label = { Text("Jam Mulai") },
                        placeholder = { Text("08:00") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = state.endTime,
                        onValueChange = viewModel::onEndTimeChange,
                        label = { Text("Jam Selesai") },
                        placeholder = { Text("10:00") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
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
