package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.presentation.AppViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    roomId: String?,
    onBack: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    viewModel: FacilityDetailViewModel = koinViewModel(),
    appViewModel: AppViewModel = koinViewModel()
) {
    val room by viewModel.getRoom(roomId ?: "").collectAsState(null)
    val currentUser by appViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.role == UserRole.ADMIN

    var showStatusDialog by remember { mutableStateOf<RoomStatus?>(null) }
    var statusNote by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Informasi Ruangan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                if (!isAdmin) {
                    // Student View: Pinjam Button
                    if (room?.status == RoomStatus.AVAILABLE) {
                        Button(
                            onClick = { room?.id?.let { onNavigateToBooking(it) } },
                            modifier = Modifier.fillMaxWidth().padding(20.dp).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("PINJAM SEKARANG", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                    }
                } else {
                    // Admin View: Status Controls
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.updateStatus(roomId ?: "", RoomStatus.AVAILABLE, null) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("SET OK", fontWeight = FontWeight.Bold) }
                        
                        Button(
                            onClick = { showStatusDialog = RoomStatus.BOOKED },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("SET FULL", fontWeight = FontWeight.Bold) }
                        
                        Button(
                            onClick = { showStatusDialog = RoomStatus.MAINTENANCE },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("REPAIR", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    ) { padding ->
        room?.let { r ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Title
                Column {
                    Text(
                        text = if (r.id.contains("AULA")) r.name else "Ruangan ${r.name}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Gedung Kuliah Umum 2 • Lantai ${r.floor}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Dynamic Status Banner
                val bannerColor = when(r.status) {
                    RoomStatus.BOOKED -> MaterialTheme.colorScheme.error
                    RoomStatus.MAINTENANCE -> MaterialTheme.colorScheme.primary
                    else -> Color(0xFF4CAF50)
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = bannerColor.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, bannerColor.copy(alpha = 0.3f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (r.status == RoomStatus.AVAILABLE) Icons.Default.Info else Icons.Default.Person,
                            contentDescription = null,
                            tint = bannerColor
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = r.status.name,
                                fontWeight = FontWeight.ExtraBold,
                                color = bannerColor,
                                letterSpacing = 1.sp
                            )
                            if (r.status == RoomStatus.BOOKED) {
                                Text(r.borrowerName ?: "Kegiatan Terjadwal", style = MaterialTheme.typography.bodySmall)
                            } else if (r.status == RoomStatus.MAINTENANCE) {
                                Text(r.maintenanceDescription ?: "Dalam Perbaikan", style = MaterialTheme.typography.bodySmall)
                            } else {
                                Text("Ruangan siap digunakan", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Facility Info Grid
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Fasilitas & Kapasitas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FacilityDetailItem(Icons.Default.People, "${r.capacity} Kursi", Modifier.weight(1f))
                        if (r.hasAc) FacilityDetailItem(Icons.Default.AcUnit, "Full AC", Modifier.weight(1f))
                        if (r.hasProjector) FacilityDetailItem(Icons.Default.Videocam, "Proyektor", Modifier.weight(1f))
                    }
                }

                // Timeline Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Jadwal Hari Ini", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            repeat(4) { index ->
                                val time = "${8 + index * 2}:00 - ${10 + index * 2}:00"
                                val isCurrentBooking = r.status == RoomStatus.BOOKED && index == 1
                                ModernTimelineItem(
                                    time = time,
                                    event = if (isCurrentBooking) r.borrowerName ?: "Terisi" else "Tersedia",
                                    isAvailable = !isCurrentBooking && r.status != RoomStatus.MAINTENANCE,
                                    isLast = index == 3
                                )
                            }
                        }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }

    // Admin Status Input Dialog
    showStatusDialog?.let { targetStatus ->
        AlertDialog(
            onDismissRequest = { showStatusDialog = null; statusNote = "" },
            title = { Text("Update Status Ruangan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Berikan catatan untuk status ${targetStatus.name}:")
                    OutlinedTextField(
                        value = statusNote,
                        onValueChange = { statusNote = it },
                        placeholder = { Text(if (targetStatus == RoomStatus.BOOKED) "Nama Peminjam..." else "Detail Kerusakan...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateStatus(roomId ?: "", targetStatus, statusNote)
                        showStatusDialog = null
                        statusNote = ""
                    }
                ) { Text("UPDATE", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = null; statusNote = "" }) { Text("BATAL") }
            }
        )
    }
}

@Composable
fun FacilityDetailItem(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ModernTimelineItem(time: String, event: String, isAvailable: Boolean, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            modifier = Modifier.width(100.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isAvailable) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = event,
            modifier = Modifier.padding(vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isAvailable) FontWeight.Normal else FontWeight.Bold,
            color = if (isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
        )
    }
}
