package com.example.foodsaver.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Komponen kartu untuk menampilkan item makanan di list.
 */
@Composable
fun FoodItemCard(
    name: String,
    expiryDate: String,
    quantity: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Kadaluwarsa: $expiryDate", style = MaterialTheme.typography.bodySmall)
                Text(text = "Stok: $quantity", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
