package com.example.Roomie.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.presentation.util.AppStrings
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Laporan", "Kontrol Sistem")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.ADMIN_DASHBOARD) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is AdminUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is AdminUiState.Success -> {
                        if (selectedTab == 0) {
                            ReportManagementTab(state, viewModel)
                        } else {
                            SystemControlTab(viewModel)
                        }
                    }
                    is AdminUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ReportManagementTab(state: AdminUiState.Success, viewModel: AdminViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard("Pending", state.pendingCount.toString(), Color(0xFFFF9800), Modifier.weight(1f))
                AdminStatCard("High Urgency", state.highUrgencyCount.toString(), Color(0xFFF44336), Modifier.weight(1f))
            }
        }
        items(state.allReports) { report ->
            ReportAdminCard(report, viewModel)
        }
    }
}

@Composable
fun SystemControlTab(viewModel: AdminViewModel) {
    var announceTitle by remember { mutableStateOf("") }
    var announceMsg by remember { mutableStateOf("") }
    var roomId by remember { mutableStateOf("") }
    var roomNote by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Broadcast Section
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Broadcast Pengumuman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = announceTitle,
                        onValueChange = { announceTitle = it },
                        label = { Text("Judul") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = announceMsg,
                        onValueChange = { announceMsg = it },
                        label = { Text("Pesan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.broadcastMessage(announceTitle, announceMsg)
                            announceTitle = ""; announceMsg = ""
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = announceTitle.isNotBlank() && announceMsg.isNotBlank()
                    ) {
                        Text("Kirim ke Semua Mahasiswa")
                    }
                }
            }
        }

        // 2. Room Override Section
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Override Status Ruangan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = roomId,
                        onValueChange = { roomId = it },
                        label = { Text("Nomor Ruang (Contoh: 101)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = roomNote,
                        onValueChange = { roomNote = it },
                        label = { Text("Catatan (Alasan)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.overrideRoomStatus("GKU2-$roomId", RoomStatus.BOOKED, roomNote); roomId = ""; roomNote = "" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) { Text("Set Full") }
                        Button(
                            onClick = { viewModel.overrideRoomStatus("GKU2-$roomId", RoomStatus.MAINTENANCE, roomNote); roomId = ""; roomNote = "" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B), contentColor = Color.Black)
                        ) { Text("Set Repair") }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportAdminCard(report: com.example.Roomie.domain.model.Report, viewModel: AdminViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(report.category, fontWeight = FontWeight.Bold)
                Text(report.status.name, color = MaterialTheme.colorScheme.primary)
            }
            Text(report.location, style = MaterialTheme.typography.labelSmall)
            Text(report.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.updateReportStatus(report.id, ReportStatus.IN_PROGRESS) },
                    enabled = report.status == ReportStatus.PENDING
                ) { Text("Proses") }
                OutlinedButton(
                    onClick = { viewModel.updateReportStatus(report.id, ReportStatus.DONE) },
                    enabled = report.status != ReportStatus.DONE
                ) { Text("Selesai") }
            }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
