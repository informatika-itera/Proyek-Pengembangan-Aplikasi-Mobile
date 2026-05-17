package com.example.Roomie.presentation.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomie.domain.model.*
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
    val tabs = listOf("Laporan", "Approval", "Kontrol")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CONTROL CENTER", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is AdminUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                    is AdminUiState.Success -> {
                        when (selectedTab) {
                            0 -> ReportManagementTab(state, viewModel, onActionSuccess = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            })
                            1 -> ApprovalTab(state, viewModel, onActionSuccess = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            })
                            else -> SystemControlTab(
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
                    is AdminUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun ApprovalTab(state: AdminUiState.Success, viewModel: AdminViewModel, onActionSuccess: (String) -> Unit) {
    val pendingBookings = state.filteredBookings.filter { it.status == BookingStatus.PENDING }
    
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.filter.bookingQuery,
            onValueChange = viewModel::onBookingSearch,
            placeholder = { Text("Cari ruangan atau tujuan...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (pendingBookings.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada pengajuan pinjam", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(pendingBookings) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pengajuan Ruangan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(booking.id, style = MaterialTheme.typography.labelSmall)
                            }
                            Text("${booking.buildingName} - Ruang ${booking.roomName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Tujuan: ${booking.subject}", style = MaterialTheme.typography.bodyMedium)
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { 
                                        viewModel.approveBooking(booking)
                                        onActionSuccess("Peminjaman disetujui & Status Ruang diupdate")
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("SETUJUI") }
                                
                                OutlinedButton(
                                    onClick = { 
                                        viewModel.rejectBooking(booking)
                                        onActionSuccess("Peminjaman ditolak")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("TOLAK") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportManagementTab(state: AdminUiState.Success, viewModel: AdminViewModel, onActionSuccess: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.filter.reportQuery,
                    onValueChange = viewModel::onReportSearch,
                    placeholder = { Text("Cari deskripsi atau lokasi...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.filter.reportStatusFilter == null,
                        onClick = { viewModel.onReportStatusFilter(null) },
                        label = { Text("Semua") }
                    )
                    FilterChip(
                        selected = state.filter.reportStatusFilter == ReportStatus.PENDING,
                        onClick = { viewModel.onReportStatusFilter(ReportStatus.PENDING) },
                        label = { Text("Pending") }
                    )
                    FilterChip(
                        selected = state.filter.reportStatusFilter == ReportStatus.IN_PROGRESS,
                        onClick = { viewModel.onReportStatusFilter(ReportStatus.IN_PROGRESS) },
                        label = { Text("Proses") }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard("PENDING", state.pendingCount.toString(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    AdminStatCard("URGENT", state.highUrgencyCount.toString(), MaterialTheme.colorScheme.error, Modifier.weight(1f))
                }
            }
            items(state.filteredReports) { report ->
                ReportAdminCard(report, viewModel, onActionSuccess)
            }
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
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    var roomNote by remember { mutableStateOf("") }
    var showRoomPicker by remember { mutableStateOf(false) }
    var currentStage by remember { mutableStateOf(PickerStage.BUILDING) }
    var tempBuilding by remember { mutableStateOf<Building?>(null) }
    var tempFloor by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Broadcast Pengumuman", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = announceTitle, 
                        onValueChange = { announceTitle = it }, 
                        label = { Text("Judul Pengumuman") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = announceMsg, 
                        onValueChange = { announceMsg = it }, 
                        label = { Text("Isi Pesan") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            viewModel.broadcastMessage(announceTitle, announceMsg)
                            onActionSuccess("Pengumuman berhasil disiarkan!")
                            announceTitle = ""; announceMsg = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = announceTitle.isNotBlank() && announceMsg.isNotBlank()
                    ) { Text("SIARKAN SEKARANG", fontWeight = FontWeight.Bold) }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Override Fasilitas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    
                    Surface(
                        onClick = { 
                            currentStage = PickerStage.BUILDING
                            showRoomPicker = true 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = selectedRoom?.let { "Ruang ${it.name} - Lt ${it.floor}" } ?: "Pilih Ruangan Target...",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = roomNote, 
                        onValueChange = { roomNote = it }, 
                        label = { Text("Catatan Status (Opsional)") }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                enabled = selectedRoom != null,
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("SET FULL", fontWeight = FontWeight.Bold) }
                            Button(
                                onClick = { 
                                    selectedRoom?.let {
                                        viewModel.overrideRoomStatus(it.id, RoomStatus.MAINTENANCE, roomNote)
                                        onActionSuccess("Status ${it.name} diubah ke PERBAIKAN")
                                        selectedRoom = null; roomNote = "" 
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                enabled = selectedRoom != null,
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("SET REPAIR", fontWeight = FontWeight.Bold) }
                        }
                        
                        if (selectedRoom != null) {
                            OutlinedButton(
                                onClick = {
                                    selectedRoom?.let {
                                        viewModel.overrideRoomStatus(it.id, RoomStatus.AVAILABLE, null)
                                        onActionSuccess("Status ${it.name} kembali TERSEDIA")
                                        selectedRoom = null; roomNote = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("RESET KE TERSEDIA", fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
    }

    if (showRoomPicker) {
        AlertDialog(
            onDismissRequest = { showRoomPicker = false },
            title = { 
                Text(
                    when(currentStage) {
                        PickerStage.BUILDING -> "Pilih Gedung"
                        PickerStage.FLOOR -> "Pilih Lantai"
                        PickerStage.ROOM -> "Pilih Ruangan"
                    },
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                ) 
            },
            text = {
                Box(Modifier.height(350.dp)) {
                    LazyColumn {
                        when (currentStage) {
                            PickerStage.BUILDING -> {
                                items(state.buildings) { building ->
                                    ListItem(
                                        headlineContent = { Text(building.name, fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text(if (building.isAvailable) "Aktif" else "Segera") },
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
                                        headlineContent = { Text("Lantai $floor", fontWeight = FontWeight.Bold) },
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
                                        headlineContent = { Text("Ruang ${room.name}", fontWeight = FontWeight.Bold) },
                                        trailingContent = { Text(room.status.name, style = MaterialTheme.typography.labelSmall) },
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
                    }) { Text("KEMBALI", color = MaterialTheme.colorScheme.primary) }
                }
            },
            dismissButton = {
                TextButton(onClick = { showRoomPicker = false }) { Text("BATAL") }
            }
        )
    }
}

@Composable
fun ReportAdminCard(
    report: Report,
    viewModel: AdminViewModel,
    onActionSuccess: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(report.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Text(report.status.name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column {
                Text(report.description, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(report.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            if (report.imageUrl != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("📷 View Image Attached", style = MaterialTheme.typography.labelSmall)
                        // In real implementation, use Coil to load report.imageUrl
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { 
                        viewModel.updateReportStatus(report.id, ReportStatus.IN_PROGRESS) 
                        onActionSuccess("Laporan sedang diproses")
                    },
                    modifier = Modifier.weight(1f),
                    enabled = report.status == ReportStatus.PENDING,
                    shape = RoundedCornerShape(12.dp)
                ) { Text("PROSES") }
                OutlinedButton(
                    onClick = { 
                        viewModel.updateReportStatus(report.id, ReportStatus.DONE)
                        onActionSuccess("Laporan telah selesai")
                    },
                    modifier = Modifier.weight(1f),
                    enabled = report.status != ReportStatus.DONE,
                    shape = RoundedCornerShape(12.dp)
                ) { Text("SELESAI") }
            }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.displaySmall, color = color, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}
