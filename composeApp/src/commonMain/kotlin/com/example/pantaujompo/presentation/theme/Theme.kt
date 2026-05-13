package com.example.pantaujompo.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warna Utama
val NeonGreenDark = Color(0xFF76FF03) // Hijau menyala untuk Dark Mode
val GreenPrimaryLight = Color(0xFF2E7D32) // Hijau elegan untuk Light Mode

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreenDark,
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimaryLight,
    onPrimary = Color.White,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onBackground = Color(0xFF1E1E1E),
    onSurface = Color(0xFF1E1E1E)
)

@Composable
fun PantauJompoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Otomatis deteksi tema HP
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}