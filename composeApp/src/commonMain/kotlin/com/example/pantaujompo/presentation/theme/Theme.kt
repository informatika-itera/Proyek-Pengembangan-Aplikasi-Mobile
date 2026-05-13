package com.example.pantaujompo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet Warna Premium Fitness App
val NeonGreen = Color(0xFFC6FF00) // Hijau stabilo sporty
val DarkBackground = Color(0xFF0D0D0D) // Super dark gray (nyaman di mata)
val SurfaceDark = Color(0xFF1A1A1A) // Warna Card (sedikit lebih terang dari BG)
val SurfaceVariantDark = Color(0xFF262626) // Warna Card untuk elemen di dalam Card
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0A0)
val ErrorRed = Color(0xFFFF5252)

private val PantauJompoColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black, // Teks di atas tombol hijau otomatis hitam
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed
)

@Composable
fun PantauJompoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PantauJompoColorScheme,
        // TODO: Nanti kita bisa tambahin custom Typography di sini
        content = content
    )
}