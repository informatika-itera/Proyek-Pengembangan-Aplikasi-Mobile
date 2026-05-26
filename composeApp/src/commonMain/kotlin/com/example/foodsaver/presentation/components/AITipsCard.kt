package com.example.foodsaver.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.theme.PrimaryGreen
import com.example.foodsaver.presentation.theme.TextMain
import com.example.foodsaver.presentation.theme.TextSecondary

@Composable
fun AITipsSection(items: List<FoodItem>) {
    val tip = generateSimpleAITip(items)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Saran FoodSaver AI",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Text(
                    tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMain,
                    lineHeight = 16.sp
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
                "Kamu punya ${first.name} yang sudah expired. Segera periksa kondisinya sebelum dibuang."
            first.category == "Buah" -> 
                "${first.name} hampir expired. Kamu bisa mengolahnya menjadi jus atau smoothie!"
            first.category == "Roti" || first.name.contains("Roti", true) ->
                "${first.name} hampir expired, enak jika dijadikan roti panggang hari ini."
            else -> 
                "AI menyarankan kamu mengonsumsi ${first.name} terlebih dahulu karena akan segera kedaluwarsa."
        }
    }
    
    val buahCount = items.count { it.category == "Buah" }
    if (buahCount > 0) return "Tips: Simpan buah di tempat sejuk atau kulkas agar tetap segar lebih lama."
    
    return "Stok makananmu aman! Tetap catat belanjaan baru agar tidak ada yang terbuang."
}
