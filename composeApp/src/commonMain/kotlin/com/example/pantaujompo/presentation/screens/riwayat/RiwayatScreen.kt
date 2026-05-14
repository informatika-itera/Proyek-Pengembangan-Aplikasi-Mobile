package com.example.pantaujompo.presentation.screens.riwayat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pantaujompo.domain.model.ActivityModel
import com.example.pantaujompo.presentation.screens.home.DashboardUiState
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RiwayatScreen(
    viewModel: RiwayatViewModel = koinViewModel(),
    onNavigateToEdit: (Long) -> Unit // INI WAJIB ADA BIAR BISA PINDAH KE FORM EDIT
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header Baru yang Lebih Bersih & Premium
        Text(
            text = "Riwayat Aktivitas",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Lacak setiap progres kebugaran Anda",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Tarik data asli dari SQLDelight
        when (val result = state) {
            is DashboardUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is DashboardUiState.Empty -> {
                Text(
                    text = "Belum ada data aktivitas. Yuk mulai olahraga!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            is DashboardUiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(result.activities) { activity ->
                        ActivityLogItem(
                            activity = activity,
                            onEdit = { onNavigateToEdit(activity.id) }, // Panggil fungsi Edit
                            onDelete = { viewModel.deleteActivity(activity.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) } // Ruang kosong biar gak ketutupan Bottom Nav
                }
            }
            else -> {}
        }
    }
}

// KOMPONEN CARD UI PREMIUM (Mirip Referensi Gambar)
@Composable
fun ActivityLogItem(activity: ActivityModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    // Tentukan Ikon & Judul
    val emoji = when (activity.type.uppercase()) {
        "LARI" -> "🏃"
        "SEPEDA" -> "🚴"
        "JALAN" -> "🚶"
        else -> "🔥"
    }

    val title = when (activity.type.uppercase()) {
        "LARI" -> "Pelacakan: Lari"
        "SEPEDA" -> "Pelacakan: Sepeda"
        "JALAN" -> "Pelacakan: Jalan Santai"
        else -> "Aktivitas: ${activity.type}"
    }

    // Ubah Timestamp jadi Tanggal yang cakep (Misal: 14 Mei 2026)
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(activity.dateTimestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Bulat Kiri
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Kolom Tengah (Detail Aktivitas)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
                Text(
                    "$dateStr • ${activity.durationMinutes} Min • ${activity.caloriesBurned} Kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary // Warna Neon
                )
                Text(
                    "Jarak: ${activity.distanceKm} Km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tombol Edit & Delete disusun vertikal di kanan
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}