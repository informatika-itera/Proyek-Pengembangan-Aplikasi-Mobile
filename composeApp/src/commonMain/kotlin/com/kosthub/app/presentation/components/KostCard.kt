package com.kosthub.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost

@Composable
fun KostCard(
    kost: Kost,
    onClick: () -> Unit,
    onToggleFavorite: (Kost) -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = kost.namaKos, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { onToggleFavorite(kost) }) {
                    Icon(
                        imageVector = if (kost.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatHargaTahunan(kost.hargaTahunan), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${kost.daerah} - ${formatJarakKm(kost.jarakKm)} km", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                badgeItems(kost).take(3).forEach { label ->
                    Badge(text = label)
                }
            }
        }
    }
}

private fun badgeItems(kost: Kost): List<String> {
    val items = mutableListOf<String>()
    if (kost.wifi == "Ada") {
        items.add("WiFi")
    }
    if (kost.fasilitasPendingin != "Tidak ada") {
        items.add(kost.fasilitasPendingin)
    }
    if (kost.kamarMandi.isNotBlank()) {
        items.add(kost.kamarMandi)
    }
    if (kost.areaLaundry == "Ada") {
        items.add("Laundry")
    }
    if (kost.areaDapur == "Ada") {
        items.add("Dapur")
    }
    if (kost.keamananCctv == "Ada") {
        items.add("CCTV")
    }
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
