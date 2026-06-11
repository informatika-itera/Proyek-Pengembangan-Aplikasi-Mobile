package com.example.inventra.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Global state untuk menyimpan preferensi tema (bisa diubah dari layar mana saja)
val LocalThemeIsDark = compositionLocalOf { mutableStateOf(false) }

// --- Light Colors ---
private val LightPrimary = Color(0xFF005691) // Blue HMIF
private val LightPrimaryContainer = Color(0xFFD1E4FF)
private val LightOnPrimaryContainer = Color(0xFF001D35)
private val LightSecondary = Color(0xFF6B5F00) // Gold/Yellow HMIF muted
private val LightSecondaryContainer = Color(0xFFFBE46D)
private val LightOnSecondaryContainer = Color(0xFF211B00)
private val LightTertiary = Color(0xFF456633) // Dark Green HMIF
private val LightTertiaryContainer = Color(0xFFC6EFAD)
private val LightBackground = Color(0xFFFDFBFF)
private val LightSurface = Color(0xFFFDFBFF)
private val LightSurfaceVariant = Color(0xFFDFE2EB)
private val LightOutline = Color(0xFF73777F)
private val LightError = Color(0xFFBA1A1A)

// --- Dark Colors ---
private val DarkPrimary = Color(0xFF9ECAFF)
private val DarkPrimaryContainer = Color(0xFF004875)
private val DarkOnPrimaryContainer = Color(0xFFD1E4FF)
private val DarkSecondary = Color(0xFFDEC84D)
private val DarkSecondaryContainer = Color(0xFF514700)
private val DarkOnSecondaryContainer = Color(0xFFFBE46D)
private val DarkBackground = Color(0xFF1A1C1E)
private val DarkSurface = Color(0xFF1A1C1E)
private val DarkSurfaceVariant = Color(0xFF43474E)
private val DarkOutline = Color(0xFF8D9199)
private val DarkError = Color(0xFFFFB4AB)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = Color.White,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = Color.White,
    tertiaryContainer = LightTertiaryContainer,
    background = LightBackground,
    onBackground = Color(0xFF1A1C1E),
    surface = LightSurface,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF43474E),
    outline = LightOutline,
    error = LightError,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF003355),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = Color(0xFF383100),
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    onBackground = Color(0xFFE2E2E6),
    surface = DarkSurface,
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC3C7CF),
    outline = DarkOutline,
    error = DarkError,
    onError = Color(0xFF690005)
)

@Composable
fun InventRaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        // Kita menggunakan typography bawaan Material 3 Android Studio (tanpa font kustom)
        content = content
    )
}