package com.example.foodsaver.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus

@Composable
fun AITipsSection(items: List<FoodItem>) {
    val tip = generateSimpleAITip(items)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Saran FoodSaver AI",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun generateSimpleAITip(items: List<FoodItem>): String {
    val expiredOrNear = items.filter { it.getStatus() != FoodStatus.SAFE }
        .sortedBy { it.getDaysRemaining() }
    
    if (expiredOrNear.isNotEmpty()) {
        val first = expiredOrNear.first()
        return when {
            first.getStatus() == FoodStatus.EXPIRED -> 
                "Aduh, ${first.name} sudah lewat tanggalnya nih. Yuk cek dulu kondisinya sebelum dibuang."
            first.category == "Buah" -> 
                "${first.name} sebentar lagi lewat masa segarnya. Enak lho kalau dijadikan jus atau smoothie hari ini!"
            first.category == "Roti" || first.name.contains("Roti", true) ->
                "${first.name} sudah mau habis masanya, coba dijadikan roti panggang yuk biar tetap nikmat."
            else -> 
                "Saran AI: Pakai ${first.name} duluan yuk dalam masakanmu biar nggak mubazir!"
        }
    }
    
    val buahCount = items.count { it.category == "Buah" }
    if (buahCount > 0) return "Tips: Biar buah-buahanmu tetap segar, simpan di tempat sejuk atau kulkas ya."
    
    return "Wah, stok makananmu aman semua! Tetap semangat masak biar nggak ada yang terbuang ya."
}
