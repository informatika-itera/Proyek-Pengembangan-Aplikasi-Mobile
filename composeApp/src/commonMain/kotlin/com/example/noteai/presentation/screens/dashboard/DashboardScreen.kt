package com.example.noteai.presentation.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToMealLog: () -> Unit,
    onNavigateToAddMeal: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FitKos") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Dashboard Harian",
                style = MaterialTheme.typography.headlineSmall
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ringkasan hari ini",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Budget makan: belum diatur")
                    Text("Catatan makanan: belum ada")
                    Text("Minum air: belum dicatat")
                    Text("Olahraga: belum dilakukan")
                }
            }

            Button(
                onClick = onNavigateToMealLog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Catatan Makanan")
            }

            Button(
                onClick = onNavigateToAddMeal,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tambah Catatan Makanan")
            }
        }
    }
}