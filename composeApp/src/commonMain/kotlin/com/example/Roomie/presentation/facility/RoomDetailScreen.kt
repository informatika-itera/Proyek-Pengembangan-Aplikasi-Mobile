package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    roomId: String?,
    onBack: () -> Unit
) {
    val cleanRoomId = roomId?.replace("GKU2-", "") ?: ""
    val isAula = roomId?.contains("AULA") == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAula) "Detail Aula" else "Detail Ruangan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = { /* Implementasi Pinjam */ },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Pinjam Sekarang")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isAula) "Aula GKU 2" else "Ruangan $cleanRoomId",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            val floor = if (isAula) "4" else cleanRoomId.take(1)
            Text(
                text = "Gedung Kuliah Umum 2 - Lantai $floor",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            // Info Fasilitas
            Text(text = "Fasilitas Tersedia", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                FacilityInfoItem(
                    icon = Icons.Default.People, 
                    label = if (isAula) "300 Kursi" else "40 Kursi"
                )
                FacilityInfoItem(Icons.Default.AcUnit, "AC")
                FacilityInfoItem(Icons.Default.Videocam, "Proyektor")
            }

            HorizontalDivider()

            // Timeline Sederhana
            Text(text = "Jadwal Hari Ini", style = MaterialTheme.typography.titleSmall)
            repeat(4) { index ->
                TimelineItem(
                    time = "${8 + index * 2}:00 - ${10 + index * 2}:00",
                    event = if (index == 1) "Mata Kuliah PAM" else "Tersedia",
                    isAvailable = index != 1
                )
            }
        }
    }
}

@Composable
fun FacilityInfoItem(icon: ImageVector, label: String) {
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
fun TimelineItem(time: String, event: String, isAvailable: Boolean) {
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
