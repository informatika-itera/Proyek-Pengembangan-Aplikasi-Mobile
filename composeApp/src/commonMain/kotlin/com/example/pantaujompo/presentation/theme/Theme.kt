package com.example.pantaujompo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ===== DARK MODE PALETTE =====
val NeonGreen = Color(0xFF00E676)
val NeonCyan = Color(0xFF00BCD4)
val NeonPurple = Color(0xFFE91E63)
val DarkBackground = Color(0xFF080808)
val SurfaceDark = Color(0xFF151515)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0A0)
val ErrorRed = Color(0xFFFF5252)

// ===== LIGHT MODE PALETTE (Broken White & Strava Orange with Navy) =====
val LightBackground = Color(0xFFFAF9F6)    // Broken White (Off-white yang hangat)
val LightSurface = Color(0xFFFFFFFF)       // Putih bersih untuk Card
val LightPrimary = Color(0xFFFC4C02)       // Orange Strava khas olahraga
val LightSecondary = Color(0xFF003049)     // Navy Biru Gelap (Cocok dengan Oranye)
val LightOnBackground = Color(0xFF001524)  // Biru Navy sangat gelap pekat untuk Teks utama
val LightOnSurface = Color(0xFF001524)
val LightSecondaryText = Color(0xFF4A6071) // Biru keabu-abuan untuk teks sekunder
val LightOutline = Color(0xFFD1DDE6)       // Garis batas kebiruan halus

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    tertiary = NeonPurple,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = Color.White.copy(0.12f)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    secondary = LightSecondary,
    onSecondary = Color.White,
    tertiary = Color(0xFF0EA5E9), // Biru langit lembut sebagai aksen
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFE9F0F5), // Card background alternatif bernuansa biru muda
    onSurfaceVariant = LightSecondaryText,
    error = Color(0xFFEF4444),
    onError = Color.White,
    outline = LightOutline
)

@Composable
fun PantauJompoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}