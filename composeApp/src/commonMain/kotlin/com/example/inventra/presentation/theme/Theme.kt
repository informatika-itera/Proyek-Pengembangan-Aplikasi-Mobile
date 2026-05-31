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
private val LightPrimary = Color(0xFF003F7C)
private val LightPrimaryContainer = Color(0xFF17579F)
private val LightOnPrimaryContainer = Color(0xFFFFFFFF)
private val LightSecondary = Color(0xFF606200)
private val LightSecondaryContainer = Color(0xFFE3E838)
private val LightOnSecondaryContainer = Color(0xFF646700)
private val LightBackground = Color(0xFFF8F9FA)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFE1E3E4)
private val LightOutline = Color(0xFF727782)
private val LightError = Color(0xFFBA1A1A)

// --- Dark Colors ---
private val DarkPrimary = Color(0xFFA7C8FF) // Biru yang lebih terang untuk visibilitas
private val DarkPrimaryContainer = Color(0xFF003F7C)
private val DarkOnPrimaryContainer = Color(0xFFD5E3FF)
private val DarkSecondary = Color(0xFFCACE18) // Kuning/hijau yang lebih terang
private val DarkSecondaryContainer = Color(0xFF484A00)
private val DarkOnSecondaryContainer = Color(0xFFE3E838)
private val DarkBackground = Color(0xFF191C1D) // Latar belakang gelap
private val DarkSurface = Color(0xFF2E3132) // Permukaan card gelap
private val DarkSurfaceVariant = Color(0xFF424751)
private val DarkOutline = Color(0xFFC2C6D2)
private val DarkError = Color(0xFFFFB4AB)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    outline = LightOutline,
    error = LightError
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkOutline,
    error = DarkError
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