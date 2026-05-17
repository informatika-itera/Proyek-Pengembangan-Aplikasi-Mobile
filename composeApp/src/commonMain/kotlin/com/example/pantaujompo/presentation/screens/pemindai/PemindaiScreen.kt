package com.example.pantaujompo.presentation.screens.pemindai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemindaiScreen() {
    // Variabel untuk nyimpen ketikan makanan lo
    var manualInput by remember { mutableStateOf("") }
    // Biar layarnya bisa di-scroll kalau HP-nya kecil
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("AI Nutrition Scanner", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        Text("Pindai atau ketik makanan Anda", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 24.dp))

        // METODE 1: Kotak Kamera Besar
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().height(200.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ketuk untuk Membuka Kamera", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // METODE 2: Input Text Manual (Ini yang hilang tadi bray!)
        OutlinedTextField(
            value = manualInput,
            onValueChange = { manualInput = it },
            placeholder = { Text("Atau ketik manual (misal: Nasi Padang)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Trigger AI (Buat Sprint 3)
        Button(
            onClick = { /* TODO Sprint 3: Panggil Gemini AI */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("✨ ANALISIS GIZI (AI)", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Kotak Hasil Analisis Makro (Dummy Sprint 2)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Estimasi Nutrisi (Dummy AI)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                MacroBar("Protein", 0.7f, Color(0xFF00FF00), "85g")
                Spacer(modifier = Modifier.height(12.dp))
                MacroBar("Karbohidrat", 0.4f, Color(0xFFFFA500), "120g")
                Spacer(modifier = Modifier.height(12.dp))
                MacroBar("Lemak", 0.3f, Color(0xFFFF5252), "45g")
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Ruang kosong bawah
    }
}

@Composable
fun MacroBar(label: String, progress: Float, color: Color, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(2f).height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color, trackColor = MaterialTheme.colorScheme.background
        )
        Text(value, modifier = Modifier.width(40.dp).padding(start = 8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    }
}