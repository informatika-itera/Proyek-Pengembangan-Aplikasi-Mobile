package com.example.metaforge.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet Profesional Esports
val MetaForgeBg = Color(0xFF08223a)         // Background Utama (Sangat Gelap)
val MetaForgeSurface = Color(0xFF092b49)    // Latar Kartu/Panel
val MetaForgeSurfaceVariant = Color(0xFF0e3a61) // Panel Aktif / Hover
val MetaForgeSecondary = Color(0xFF194e7d)  // Border / Aksen Minor
val MetaForgePrimary = Color(0xFF2b6599)    // Tombol Utama / Aksen Mayor

// Warna Fungsional
val MetaForgeRed = Color(0xFFE53935)   // Enemy / Ban / Warning
val MetaForgeGreen = Color(0xFF43A047) // Synergy / Good Matchup
val MetaForgeGold = Color(0xFFFFD700)  // Premium / Tier SS

private val MetaForgeColorScheme = darkColorScheme(
    background = MetaForgeBg,
    surface = MetaForgeSurface,
    surfaceVariant = MetaForgeSurfaceVariant,
    primary = MetaForgePrimary,
    secondary = MetaForgeSecondary,
    error = MetaForgeRed,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White
)

@Composable
fun MetaForgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MetaForgeColorScheme,
        content = content
    )
}