package com.kosthub.app.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.UiState

@Composable
fun DetailScreen(
    kostId: Long,
    uiState: UiState<List<Kost>>,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Detail Kost", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = uiState.message)
            is UiState.Empty -> EmptyState(text = "Belum ada data kost")
            is UiState.Success -> {
                val kost = uiState.data.firstOrNull { it.id == kostId }
                if (kost == null) {
                    EmptyState(text = "Kost tidak ditemukan")
                } else {
                    DetailContent(kost = kost, onBack = onBack)
                }
            }
        }
    }
}

@Composable
private fun DetailContent(kost: Kost, onBack: () -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = kost.namaKos, style = MaterialTheme.typography.titleLarge)
    Text(text = formatHargaTahunan(kost.hargaTahunan), style = MaterialTheme.typography.bodyMedium)
    Text(text = "${kost.daerah} - ${formatJarakKm(kost.jarakKm)} km", style = MaterialTheme.typography.bodySmall)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Tipe: ${kost.tipeKos}", style = MaterialTheme.typography.bodySmall)
    Text(text = "Kamar mandi: ${kost.kamarMandi}", style = MaterialTheme.typography.bodySmall)
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = "Fasilitas", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = fasilitasLabels(kost).joinToString(", "))
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onBack) {
        Text(text = "Kembali")
    }
}

private fun fasilitasLabels(kost: Kost): List<String> {
    val items = mutableListOf<String>()
    if (kost.wifi == "Ada") items.add("WiFi")
    if (kost.fasilitasPendingin != "Tidak ada") items.add(kost.fasilitasPendingin)
    if (kost.furniturKasur == "Ada") items.add("Kasur")
    if (kost.furniturLemari == "Ada") items.add("Lemari")
    if (kost.furniturMejaBelajar == "Ada") items.add("Meja belajar")
    if (kost.areaLaundry == "Ada") items.add("Laundry")
    if (kost.areaDapur == "Ada") items.add("Dapur")
    if (kost.keamananCctv == "Ada") items.add("CCTV")
    return items
}

private fun formatJarakKm(km: Double): String {
    return km.toString().replace(".", ",")
}

private fun formatHargaTahunan(value: Long): String {
    val digits = value.toString()
    val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
    return "Rp$grouped"
}
