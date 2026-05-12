package com.example.pantaujompo.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PemindaiGiziAIScreen() { CenteredText("Layar Pemindai Gizi") }

@Composable
fun IntegratedHistoryScreen() { CenteredText("Layar Riwayat Terpadu") }

@Composable
fun HealthLiteracyScreen() { CenteredText("Layar Literasi Kesehatan") }

@Composable
fun UserProfileScreen() { CenteredText("Layar Profil Pengguna") }

@Composable
fun CenteredText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, color = Color.White)
    }
}