package com.example.foodsaver.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.foodsaver.presentation.theme.ExpiredRed
import com.example.foodsaver.presentation.theme.PrimaryGreen
import com.example.foodsaver.presentation.theme.WarningOrange

@Composable
fun FoodItemCard(
    item: FoodItem,
    modifier: Modifier = Modifier
) {
    val status = item.getStatus()
    val statusColor = when (status) {
        FoodStatus.SAFE -> PrimaryGreen
        FoodStatus.NEAR_EXPIRY -> WarningOrange
        FoodStatus.EXPIRED -> ExpiredRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Icon/Emoji Box
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getEmojiForCategory(item.category),
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238),
                        maxLines = 1
                    )
                    StatusBadge(status = status)
                }
                
                Text(
                    text = "${item.category} • ${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Simpan: ${item.storageLocation}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.getStatusLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: FoodStatus) {
    val (text, color) = when (status) {
        FoodStatus.SAFE -> "Aman" to PrimaryGreen
        FoodStatus.NEAR_EXPIRY -> "Penting" to WarningOrange
        FoodStatus.EXPIRED -> "Expired" to ExpiredRed
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

fun getEmojiForCategory(category: String): String {
    return when (category) {
        "Sayur" -> "🥬"
        "Buah" -> "🍎"
        "Daging" -> "🥩"
        "Susu & Telur" -> "🥛"
        "Minuman" -> "🥤"
        "Camilan" -> "🍿"
        "Bumbu" -> "🧂"
        else -> "🍴"
    }
}
