package com.kosthub.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost

@Composable
fun KostCompactRow(kost: Kost) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = kost.namaKos, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${kost.daerah} - ${formatJarakKm(kost.jarakKm)} km",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(text = formatHargaTahunan(kost.hargaTahunan), style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun formatJarakKm(km: Double): String {
    return km.toString().replace(".", ",")
}

private fun formatHargaTahunan(value: Long): String {
    val digits = value.toString()
    val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
    return "Rp$grouped"
}
