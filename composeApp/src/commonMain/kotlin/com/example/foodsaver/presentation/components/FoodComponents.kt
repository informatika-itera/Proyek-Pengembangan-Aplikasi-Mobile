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
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.theme.DangerRed
import com.example.foodsaver.presentation.theme.FreshGreen
import com.example.foodsaver.presentation.theme.WarningOrange

@Composable
fun FoodItemCard(
    name: String,
    expiryDate: String,
    quantity: String,
    category: String,
    status: FoodStatus,
    daysRemaining: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Icon Placeholder
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getEmojiForCategory(category),
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )
                Text(
                    text = "$category • $quantity",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (daysRemaining < 0) "Expired" else "Expired dalam $daysRemaining hari",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (status == FoodStatus.SAFE) FreshGreen else if (status == FoodStatus.NEAR_EXPIRY) WarningOrange else DangerRed
                )
            }

            StatusBadge(status = status)
        }
    }
}

@Composable
fun StatusBadge(status: FoodStatus) {
    val (text, color) = when (status) {
        FoodStatus.SAFE -> "Aman" to FreshGreen
        FoodStatus.NEAR_EXPIRY -> "Hampir Expired" to WarningOrange
        FoodStatus.EXPIRED -> "Expired" to DangerRed
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

fun getEmojiForCategory(category: String): String {
    return when (category.lowercase()) {
        "sayuran" -> "🥬"
        "buah" -> "🍎"
        "daging" -> "🥩"
        "susu", "dairy" -> "🥛"
        "minuman" -> "🥤"
        "snack", "camilan" -> "🍿"
        "bumbu" -> "🧂"
        "karbohidrat", "nasi", "mie" -> "🍚"
        else -> "🍴"
    }
}
