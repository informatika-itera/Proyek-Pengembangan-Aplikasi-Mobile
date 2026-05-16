package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.repository.FacilityRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    roomId: String?,
    onBack: () -> Unit,
    facilityRepository: FacilityRepository = org.koin.compose.koinInject()
) {
    val room by facilityRepository.getRoomById(roomId ?: "").collectAsState(null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Ruangan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (room?.status == RoomStatus.AVAILABLE) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = { /* Implementasi Pinjam */ },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text("Pinjam Sekarang")
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (r.id.contains("AULA")) r.name else "Ruangan ${r.name}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Gedung Kuliah Umum 2 - Lantai ${r.floor}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Dynamic Status Info
                when(r.status) {
                    RoomStatus.BOOKED -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Sedang Digunakan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    Text(r.borrowerName ?: "Tidak ada nama peminjam", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    RoomStatus.MAINTENANCE -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)) // Light Yellow
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFBC02D))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Sedang Perbaikan", fontWeight = FontWeight.Bold, color = Color(0xFFFBC02D))
                                    Text(r.maintenanceDescription ?: "Detail perbaikan tidak tersedia", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    else -> {}
                }

                HorizontalDivider()

                // Info Fasilitas
                Text(text = "Fasilitas Tersedia", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    FacilityInfoItem(
                        icon = Icons.Default.People, 
                        label = "${r.capacity} Kursi"
                    )
                    if (r.hasAc) FacilityInfoItem(Icons.Default.AcUnit, "AC")
                    if (r.hasProjector) FacilityInfoItem(Icons.Default.Videocam, "Proyektor")
                }

                HorizontalDivider()

                // Timeline Sederhana
                Text(text = "Jadwal Hari Ini", style = MaterialTheme.typography.titleSmall)
                repeat(4) { index ->
                    val time = "${8 + index * 2}:00 - ${10 + index * 2}:00"
                    val isCurrentBooking = r.status == RoomStatus.BOOKED && index == 1
                    TimelineItem(
                        time = time,
                        event = if (isCurrentBooking) r.borrowerName ?: "Booked" else "Tersedia",
                        isAvailable = !isCurrentBooking && r.status != RoomStatus.MAINTENANCE
                    )
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun FacilityInfoItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun TimelineItem(time: String, event: String, isAvailable: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            modifier = Modifier.width(110.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    MaterialTheme.shapes.small
                )
                .padding(12.dp)
        ) {
            Text(
                text = event,
                color = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
