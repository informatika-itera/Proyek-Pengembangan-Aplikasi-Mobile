package com.example.pantaujompo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet Warna Premium Pantau Jompo (Dark + Neon Green)
val NeonGreen = Color(0xFF00FF00) // Hijau stabilo sporty
val DarkBackground = Color(0xFF121212) // Super dark gray (image_0.png background)
val SurfaceDark = Color(0xFF1E1E1E) // Warna Card (image_0.png cards)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0A0)
val ErrorRed = Color(0xFFFF5252)

// Konfigurasi Warna Dark Mode
private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    error = ErrorRed,
    onSurfaceVariant = TextSecondary // Digunakan untuk teks deskripsi di card
)

@Composable
fun PantauJompoTheme(
    content: @Composable () -> Unit
) {
    // Kita paksakan pakai Dark Mode agar UI-nya "nyala" seperti referensi
    MaterialTheme(
        colorScheme = DarkColorScheme,
        // TODO: Typography bisa ditambahkan di sini nanti
        content = content
    )
}