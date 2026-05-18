package com.example.pocketguard.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== POCKETGUARD COLORS ====================
// Diambil dari pocketguard_design_system.html
val PgPrimary = Color(0xFF1A6B4A)       // Hijau utama aplikasi
val PgPrimaryLight = Color(0xFFE8F5EE)  // Latar belakang hijau muda/light chip
val PgAccent = Color(0xFF2ECC8A)        // Hijau terang untuk aksen/aktivitas aktif
val PgWarning = Color(0xFFF5A623)       // Oranye untuk peringatan batas anggaran
val PgDanger = Color(0xFFE84040)        // Merah untuk pengeluaran / tombol hapus

// Warna dasar Tampilan Terang (Light Mode)
val BackgroundLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF1C1B1F)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1C1B1F)
val SurfaceVariantLight = Color(0xFFF8F9FA) // Warna muted (abu-abu sangat muda) untuk kartu/list
val OnSurfaceVariantLight = Color(0xFF49454F)

// Warna dasar Tampilan Gelap (Dark Mode)
val BackgroundDark = Color(0xFF121212)
val OnBackgroundDark = Color(0xFFE6E1E5)
val SurfaceDark = Color(0xFF1E1E1E)
val OnSurfaceDark = Color(0xFFE6E1E5)
val SurfaceVariantDark = Color(0xFF2D2D2D) // Abu-abu gelap untuk kontainer komponen
val OnSurfaceVariantDark = Color(0xFFCAC4D0)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = PgPrimary,
    onPrimary = Color.White,
    primaryContainer = PgPrimaryLight,
    onPrimaryContainer = PgPrimary,
    secondary = PgAccent,
    onSecondary = Color.White,
    secondaryContainer = PgPrimaryLight,
    onSecondaryContainer = PgPrimary,
    error = PgDanger,
    onError = Color.White,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = Color(0xFF79747E)
)

private val DarkColorScheme = darkColorScheme(
    primary = PgAccent, // Menggunakan warna aksen yang lebih cerah agar kontras di mode gelap
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF005234),
    onPrimaryContainer = PgPrimaryLight,
    secondary = PgPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1A3D2B),
    onSecondaryContainer = PgPrimaryLight,
    error = PgDanger,
    onError = Color.White,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = Color(0xFF938F99)
)

// ==================== THEME ====================

@Composable
fun PocketGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}