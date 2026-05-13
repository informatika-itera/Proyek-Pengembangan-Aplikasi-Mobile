package com.example.pantaujompo.presentation.screens.riwayat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pantaujompo.domain.model.ActivityModel
import com.example.pantaujompo.presentation.screens.home.DashboardUiState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    viewModel: RiwayatViewModel = koinViewModel() // Pakai ViewModel asli dari database
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "RIWAYAT TERPADU",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari data (misal: 'lari')") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        LazyRow(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            val filters = listOf("Semua Data", "Nutrisi", "Aktivitas", "Keluhan")
            items(filters) { filter ->
                val selected = filter == "Semua Data"
                ElevatedFilterChip(
                    selected = selected,
                    onClick = { /* TODO Sprint Berikutnya */ },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Text("BASIS DATA BULAN INI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        // Tarik data asli dari SQLDelight
        when (val result = state) {
            is DashboardUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is DashboardUiState.Empty -> Text("Belum ada data aktivitas. Yuk mulai olahraga!", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 20.dp))
            is DashboardUiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(result.activities) { activity ->
                        ActivityLogItem(activity = activity, onDelete = { viewModel.deleteActivity(activity.id) })
                    }
                }
            }
            else -> {}
        }
    }
}

// Komponen Card UI Premium
@Composable
fun ActivityLogItem(activity: ActivityModel, onDelete: () -> Unit) {
    // Percantik judul berdasarkan tipe olahraga
    val title = when (activity.type.uppercase()) {
        "LARI" -> "🏃 Pelacakan: Lari"
        "SEPEDA" -> "🚴 Pelacakan: Sepeda"
        "JALAN" -> "🚶 Pelacakan: Jalan Santai"
        else -> "📍 Aktivitas: ${activity.type}"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "${activity.distanceKm} km • ${activity.durationMinutes} menit • ${activity.caloriesBurned} kkal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = { /* TODO Sprint Berikutnya */ }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.7f)) }
            }
        }
    }
}