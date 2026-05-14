package com.example.foodsaver.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodsaver.presentation.components.FoodItemCard

/**
 * Layar utama FoodSaver untuk menampilkan inventory makanan.
 */
@Composable
fun HomeScreen(
    onAddFoodClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("FoodSaver Inventory") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFoodClick) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Makanan")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Bahan Makanan Anda",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Placeholder data untuk demonstrasi struktur
            items(5) { index ->
                FoodItemCard(
                    name = "Bahan Makanan #$index",
                    expiryDate = "10/12/2024",
                    quantity = "2 kg"
                )
            }
        }
    }
}
