package com.example.Roomie.presentation.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.Roomie.domain.model.*
import kotlinx.datetime.*
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
    var currentView by remember { mutableStateOf("OVERVIEW") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (currentView == "OVERVIEW") "COMMAND CENTER" else currentView,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    if (currentView != "OVERVIEW") {
                        IconButton(onClick = { currentView = "OVERVIEW" }) {
                            Icon(Icons.Default.Close, "Back to Hub", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is AdminUiState.Loading -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
                is AdminUiState.Success -> {
                    AnimatedContent(
                        targetState = currentView,
                        transitionSpec = {
                            fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                        }
                    ) { view ->
                        when (view) {
                            "OVERVIEW" -> AdminOverviewHub(
                                state = state,
                                onNavigate = { currentView = it }
                            )
                            "LAPORAN" -> ReportManagementTab(state, viewModel, onActionSuccess = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            })
                            "APPROVAL" -> ApprovalTab(state, viewModel, onActionSuccess = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            })
                            "KONTROL" -> SystemControlTab(viewModel, onActionSuccess = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            })
                            "HISTORY" -> HistoryTab(state)
                        }
                    }
                }
                is AdminUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOverviewHub(
    state: AdminUiState.Success,
    onNavigate: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showFacilitySheet by remember { mutableStateOf(false) }
    var showReportBreakdown by remember { mutableStateOf(false) }

    // --- ACCURACY LOGIC: REAL-TIME SYNC ---
    val tz = TimeZone.currentSystemDefault()
    val now = Clock.System.now()
    val today = now.toLocalDateTime(tz).date
    
    // 1. Calculate Facility Health (Synchronized with Facility menu logic)
    // A room is considered "Unavailable" if:
    // - Its status is MAINTENANCE in database
    // - OR there is an APPROVED booking on TODAY'S date
    val totalRoomsCount = state.rooms.size.coerceAtLeast(1)
    
    val busyRoomIds = state.allBookings
        .filter { booking ->
            val bookingDate = Instant.fromEpochMilliseconds(booking.startTime)
                .toLocalDateTime(tz).date
            booking.status == BookingStatus.APPROVED && bookingDate == today
        }
        .map { it.roomId }
        .toSet()

    val availableRoomsCount = state.rooms.count { room ->
        // Mirroring FacilityViewModel logic: 
        // 1. Maintenance is always unavailable
        // 2. Otherwise, check if there's a booking TODAY
        room.status != RoomStatus.MAINTENANCE && !busyRoomIds.contains(room.id)
    }
    
    val facilityHealth = availableRoomsCount.toFloat() / totalRoomsCount

    // 2. Calculate Report Resolution
    val totalReportsCount = state.allReports.size.coerceAtLeast(1)
    val solvedReportsCount = state.allReports.count { it.status == ReportStatus.DONE }
    val reportResolution = solvedReportsCount.toFloat() / totalReportsCount

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Dual Gauges (Fixed UI: Centered, Better Proportions)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GaugeCard(
                    title = "FACILITY HEALTH",
                    value = "${(facilityHealth * 100).toInt()}%",
                    progress = facilityHealth,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { showFacilitySheet = true }
                )
                GaugeCard(
                    title = "RESOLVED RATE",
                    value = "${(reportResolution * 100).toInt()}%",
                    progress = reportResolution,
                    color = Color(0xFFB22222), // Itera Red
                    modifier = Modifier.weight(1f),
                    onClick = { showReportBreakdown = !showReportBreakdown }
                )
            }
        }

        // Urgency Breakdown (Expandable)
        if (showReportBreakdown) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Urgency Breakdown", fontWeight = FontWeight.Bold)
                        UrgencyItem("High Priority", state.allReports.count { it.urgency == UrgencyLevel.HIGH }, Color(0xFFB22222))
                        UrgencyItem("Medium Priority", state.allReports.count { it.urgency == UrgencyLevel.MEDIUM }, MaterialTheme.colorScheme.primary)
                        UrgencyItem("Low Priority", state.allReports.count { it.urgency == UrgencyLevel.LOW }, Color.Gray)
                    }
                }
            }
        }

        // 2. Menu Hub
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("MANAGEMENT HUB", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HubTile(
                        title = "Approvals",
                        count = state.allBookings.count { it.status == BookingStatus.PENDING }.toString(),
                        icon = Icons.AutoMirrored.Filled.FactCheck,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("APPROVAL") }
                    )
                    HubTile(
                        title = "Reports",
                        count = state.pendingCount.toString(),
                        icon = Icons.Default.ReportProblem,
                        color = Color(0xFFB22222),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("LAPORAN") }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HubTile(
                        title = "Control",
                        count = "LIVE",
                        icon = Icons.Default.Campaign,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("KONTROL") }
                    )
                    HubTile(
                        title = "Audit Log",
                        count = state.auditLogs.size.toString(),
                        icon = Icons.Default.HistoryEdu,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("HISTORY") }
                    )
                }
            }
        }
    }

    if (showFacilitySheet) {
        ModalBottomSheet(
            onDismissRequest = { showFacilitySheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            FacilityStatusContent(state.rooms, state.allBookings)
        }
    }
}

@Composable
fun GaugeCard(
    title: String,
    value: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.03f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = color.copy(alpha = 0.1f),
                    strokeWidth = 12.dp
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    strokeWidth = 12.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    value, 
                    fontWeight = FontWeight.Black, 
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                title, 
                style = MaterialTheme.typography.labelSmall, 
                fontWeight = FontWeight.ExtraBold, 
                color = color.copy(alpha = 0.8f),
                letterSpacing = 1.sp,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun UrgencyItem(label: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(count.toString(), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FacilityStatusContent(rooms: List<Room>, allBookings: List<Booking>) {
    val tz = TimeZone.currentSystemDefault()
    val now = Clock.System.now()
    val today = now.toLocalDateTime(tz).date
    
    // Identify truly busy rooms (Booked today)
    val currentlyBookedIds = allBookings
        .filter { booking ->
            val bookingDate = Instant.fromEpochMilliseconds(booking.startTime)
                .toLocalDateTime(tz).date
            booking.status == BookingStatus.APPROVED && bookingDate == today
        }
        .map { it.roomId }
        .toSet()

    val nonAvailable = rooms.filter { room ->
        room.status == RoomStatus.MAINTENANCE || currentlyBookedIds.contains(room.id)
    }
    
    Column(Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 24.dp, end = 24.dp)) {
        Text("Status Fasilitas Saat Ini", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Text("Daftar ruangan yang sedang digunakan atau dalam perbaikan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(24.dp))
        
        if (nonAvailable.isEmpty()) {
            Box(Modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Semua ruangan tersedia", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.heightIn(max = 450.dp)) {
                items(nonAvailable) { room ->
                    val isInMaintenance = room.status == RoomStatus.MAINTENANCE
                    val isCurrentlyBooked = currentlyBookedIds.contains(room.id)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = if (isInMaintenance) Icons.Default.Build else Icons.Default.LockClock
                        val color = if (isInMaintenance) MaterialTheme.colorScheme.primary else Color(0xFFB22222)
                        val statusText = if (isInMaintenance) "MAINTENANCE" else "IN USE (BOOKED)"
                        
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(room.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(statusText, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HubTile(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(), 
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ApprovalTab(state: AdminUiState.Success, viewModel: AdminViewModel, onActionSuccess: (String) -> Unit) {
    val pendingBookings = state.allBookings.filter { it.status == BookingStatus.PENDING }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Pengajuan Menunggu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text("Review permohonan peminjaman ruangan mahasiwa", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
        }

        if (pendingBookings.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Semua pengajuan sudah diproses", color = Color.Gray)
                }
            }
        } else {
            items(pendingBookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(booking.subject ?: "Tanpa Judul", fontWeight = FontWeight.Bold)
                                Text("Ruang ${booking.roomName}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.alpha(0.1f), color = MaterialTheme.colorScheme.onSurface)
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.approveBooking(booking); onActionSuccess("Berhasil disetujui") },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) { Text("SETUJUI", fontWeight = FontWeight.Bold) }
                            
                            OutlinedButton(
                                onClick = { viewModel.rejectBooking(booking); onActionSuccess("Berhasil ditolak") },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("TOLAK", color = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SystemControlTab(
    viewModel: AdminViewModel,
    onActionSuccess: (String) -> Unit
) {
    var announceTitle by remember { mutableStateOf("") }
    var announceMsg by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("System Broadcast", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Kirim Pengumuman Global", fontWeight = FontWeight.Bold)
                }
                
                OutlinedTextField(
                    value = announceTitle, 
                    onValueChange = { announceTitle = it }, 
                    label = { Text("Judul Pengumuman") }, 
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = announceMsg, 
                    onValueChange = { announceMsg = it }, 
                    label = { Text("Isi Pesan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 3
                )
                
                Button(
                    onClick = {
                        viewModel.broadcastMessage(announceTitle, announceMsg)
                        onActionSuccess("Pengumuman Terkirim!")
                        announceTitle = ""; announceMsg = ""
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = announceTitle.isNotBlank() && announceMsg.isNotBlank()
                ) {
                    Text("KIRIM BROADCAST", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ReportManagementTab(state: AdminUiState.Success, viewModel: AdminViewModel, onActionSuccess: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Laporan Kerusakan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
        }

        items(state.filteredReports) { report ->
            ReportAdminCard(report, viewModel, onActionSuccess)
        }
    }
}

@Composable
fun ReportAdminCard(report: Report, viewModel: AdminViewModel, onActionSuccess: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape) {
                    Text(report.category, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Text(report.status.name, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelSmall)
            }
            Text(report.description, fontWeight = FontWeight.Bold)
            
            if (report.imageUrl != null) {
                AsyncImage(
                    model = report.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (report.status == ReportStatus.PENDING) {
                    Button(
                        onClick = { viewModel.updateReportStatus(report.id, ReportStatus.IN_PROGRESS); onActionSuccess("Diproses") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("PROSES") }
                }
                if (report.status != ReportStatus.DONE) {
                    OutlinedButton(
                        onClick = { viewModel.updateReportStatus(report.id, ReportStatus.DONE); onActionSuccess("Selesai") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("SELESAI") }
                }
            }
        }
    }
}

@Composable
fun HistoryTab(state: AdminUiState.Success) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Riwayat Aktivitas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black) }
        
        items(state.auditLogs) { log ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                Spacer(Modifier.width(16.dp))
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(log.action, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Oleh: ${log.userName}", style = MaterialTheme.typography.bodySmall)
                        log.details?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                    }
                }
            }
        }
    }
}
