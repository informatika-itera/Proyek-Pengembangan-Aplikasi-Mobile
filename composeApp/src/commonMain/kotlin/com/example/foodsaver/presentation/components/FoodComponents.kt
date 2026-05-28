package com.example.foodsaver.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.foodsaver.presentation.theme.*
import com.example.foodsaver.core.util.formatQuantity

@Composable
fun FoodItemCard(
    item: FoodItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val status = item.getStatus()
    val isDark = isSystemInDarkTheme()
    
    val statusColor = when (status) {
        FoodStatus.SAFE -> if (isDark) SafeTextDark else SafeTextLight
        FoodStatus.NEAR_EXPIRY -> if (isDark) WarningTextDark else WarningTextLight
        else -> if (isDark) ExpiredTextDark else ExpiredTextLight
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick?.invoke() }
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
                color = statusColor.copy(alpha = 0.15f)
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
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    StatusBadge(status = status)
                }
                
                Text(
                    text = "${item.category} • ${item.quantity.formatQuantity(item.unit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Simpan: ${item.storageLocation}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.getStatusLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: FoodStatus) {
    val isDark = isSystemInDarkTheme()
    val (text, color) = when (status) {
        FoodStatus.SAFE -> "Aman" to if (isDark) SafeTextDark else SafeTextLight
        FoodStatus.NEAR_EXPIRY -> "Penting" to if (isDark) WarningTextDark else WarningTextLight
        FoodStatus.EXPIRED -> "Expired" to if (isDark) ExpiredTextDark else ExpiredTextLight
        FoodStatus.EXPIRED_TODAY -> "Hari Ini" to if (isDark) ExpiredTextDark else ExpiredTextLight
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
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
