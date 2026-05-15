package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Ruangan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Icon back bisa ditambahkan di sini
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = { /* Implementasi Pinjam */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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
            // Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text("Foto Ruangan $roomId", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(
                text = "Ruangan $roomId",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Info Fasilitas
            Text(text = "Fasilitas Tersedia", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FacilityInfoItem(Icons.Default.People, "40 Kursi")
                FacilityInfoItem(Icons.Default.AcUnit, "AC")
                FacilityInfoItem(Icons.Default.Videocam, "Proyektor")
            }

            Divider()

            // Timeline Sederhana
            Text(text = "Jadwal Hari Ini", style = MaterialTheme.typography.titleMedium)
            repeat(3) { index ->
                TimelineItem(
                    time = "${8 + index * 2}:00 - ${10 + index * 2}:00",
                    event = if (index == 1) "Matakuliah PAM" else "Tersedia",
                    isAvailable = index != 1
                )
            }
        }
    }
}

@Composable
fun FacilityInfoItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
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
            modifier = Modifier.width(100.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    MaterialTheme.shapes.small
                )
                .padding(8.dp)
        ) {
            Text(
                text = event,
                color = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
