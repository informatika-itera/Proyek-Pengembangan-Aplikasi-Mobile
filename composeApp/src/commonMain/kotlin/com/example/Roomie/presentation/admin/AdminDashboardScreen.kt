package com.example.Roomie.presentation.admin

import androidx.compose.foundation.clickable
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
import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.presentation.util.AppStrings
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Laporan", "Kontrol Sistem")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            SystemControlTab(
                                state = state,
                                viewModel = viewModel,
                                onActionSuccess = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }
                            )
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

private enum class PickerStage { BUILDING, FLOOR, ROOM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemControlTab(
    state: AdminUiState.Success,
    viewModel: AdminViewModel,
    onActionSuccess: (String) -> Unit
) {
    var announceTitle by remember { mutableStateOf("") }
    var announceMsg by remember { mutableStateOf("") }
    
    // Room Selection State
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    var roomNote by remember { mutableStateOf("") }
    var showRoomPicker by remember { mutableStateOf(false) }

    // Picker Wizard State
    var currentStage by remember { mutableStateOf(PickerStage.BUILDING) }
    var tempBuilding by remember { mutableStateOf<Building?>(null) }
    var tempFloor by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Broadcast Section
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Broadcast Pengumuman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = announceTitle, onValueChange = { announceTitle = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = announceMsg, onValueChange = { announceMsg = it }, label = { Text("Pesan") }, modifier = Modifier.fillMaxWidth())
                    Button(
                        onClick = {
                            viewModel.broadcastMessage(announceTitle, announceMsg)
                            onActionSuccess("Pengumuman berhasil disiarkan!")
                            announceTitle = ""; announceMsg = ""
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = announceTitle.isNotBlank() && announceMsg.isNotBlank()
                    ) { Text("Kirim ke Mahasiswa") }
                }
            }
        }

        // 2. Room Override Section
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Override Status Ruangan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    OutlinedCard(
                        onClick = { 
                            currentStage = PickerStage.BUILDING
                            showRoomPicker = true 
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(selectedRoom?.let { "Ruang ${it.name} - Lt ${it.floor} (${it.id.split("-")[0]})" } ?: "Pilih Ruangan...")
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    OutlinedTextField(value = roomNote, onValueChange = { roomNote = it }, label = { Text("Catatan/Alasan") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { 
                                selectedRoom?.let {
                                    viewModel.overrideRoomStatus(it.id, RoomStatus.BOOKED, roomNote)
                                    onActionSuccess("Status ${it.name} diubah ke PENUH")
                                    selectedRoom = null; roomNote = "" 
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                            enabled = selectedRoom != null
                        ) { Text("Set Full") }
                        Button(
                            onClick = { 
                                selectedRoom?.let {
                                    viewModel.overrideRoomStatus(it.id, RoomStatus.MAINTENANCE, roomNote)
                                    onActionSuccess("Status ${it.name} diubah ke PERBAIKAN")
                                    selectedRoom = null; roomNote = "" 
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B), contentColor = Color.Black),
                            enabled = selectedRoom != null
                        ) { Text("Set Repair") }
                    }
                    
                    if (selectedRoom != null) {
                        OutlinedButton(
                            onClick = {
                                selectedRoom?.let {
                                    viewModel.overrideRoomStatus(it.id, RoomStatus.AVAILABLE, null)
                                    onActionSuccess("Status ${it.name} kembali TERSEDIA")
                                    selectedRoom = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Reset ke Tersedia") }
                    }
                }
            }
        }
    }

    // Wizard Room Picker Dialog
    if (showRoomPicker) {
        AlertDialog(
            onDismissRequest = { showRoomPicker = false },
            title = { 
                Text(when(currentStage) {
                    PickerStage.BUILDING -> "Pilih Gedung"
                    PickerStage.FLOOR -> "Pilih Lantai - ${tempBuilding?.name}"
                    PickerStage.ROOM -> "Pilih Ruangan - Lt ${tempFloor}"
                }) 
            },
            text = {
                Box(Modifier.height(300.dp)) {
                    LazyColumn {
                        when (currentStage) {
                            PickerStage.BUILDING -> {
                                items(state.buildings) { building ->
                                    ListItem(
                                        headlineContent = { Text(building.name) },
                                        supportingContent = { Text(if (building.isAvailable) "Aktif" else "Coming Soon") },
                                        modifier = Modifier.clickable(enabled = building.isAvailable) { 
                                            tempBuilding = building
                                            currentStage = PickerStage.FLOOR
                                        }
                                    )
                                }
                            }
                            PickerStage.FLOOR -> {
                                items((1..4).toList()) { floor ->
                                    ListItem(
                                        headlineContent = { Text("Lantai $floor") },
                                        modifier = Modifier.clickable { 
                                            tempFloor = floor
                                            currentStage = PickerStage.ROOM
                                        }
                                    )
                                }
                            }
                            PickerStage.ROOM -> {
                                val filteredRooms = state.rooms.filter { 
                                    it.id.startsWith(tempBuilding?.id ?: "") && it.floor == tempFloor 
                                }
                                items(filteredRooms) { room ->
                                    ListItem(
                                        headlineContent = { Text("Ruang ${room.name}") },
                                        supportingContent = { Text("Status: ${room.status}") },
                                        modifier = Modifier.clickable { 
                                            selectedRoom = room
                                            showRoomPicker = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (currentStage != PickerStage.BUILDING) {
                    TextButton(onClick = { 
                        currentStage = if (currentStage == PickerStage.ROOM) PickerStage.FLOOR else PickerStage.BUILDING 
                    }) { Text("Kembali") }
                }
            },
            dismissButton = {
                TextButton(onClick = { showRoomPicker = false }) { Text("Batal") }
            }
        )
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
