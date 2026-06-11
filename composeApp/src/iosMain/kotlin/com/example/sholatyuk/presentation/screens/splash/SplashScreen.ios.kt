package com.example.sholatyuk.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.sholatyuk.presentation.theme.DeepBlue
import com.example.sholatyuk.presentation.theme.DarkTeal
import kotlinx.coroutines.delay

@Composable
actual fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    // Pada iOS, jika logo belum dipindahkan ke commonMain, kita tampilkan background saja
    // atau gunakan placeholder. Untuk sekarang, langsung delay ke Home.
    
    LaunchedEffect(Unit) {
        delay(1000L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkTeal, DeepBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Logo bisa ditambahkan di sini jika sudah dipindah ke commonResources
    }
}
