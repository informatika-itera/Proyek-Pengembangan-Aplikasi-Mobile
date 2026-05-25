package com.example.fitkos.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS (FitKos Palette) ====================
private val FitKosGreen = Color(0xFF2E7D32)         // Hijau Utama
private val FitKosGreenLight = Color(0xFF009688)    // Hijau Toska
private val FitKosOrange = Color(0xFFFFB74D)       // Orange Aksen
private val FitKosBg = Color(0xFFF1F8F4)           // Background Hijau Sangat Muda

private val LightColorScheme = lightColorScheme(
    primary = FitKosGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F0D0),
    onPrimaryContainer = Color(0xFF00390A),
    secondary = FitKosGreenLight,
    onSecondary = Color.White,
    tertiary = FitKosOrange,
    background = FitKosBg,
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE0EEE0),
    onSurfaceVariant = Color(0xFF444444),
    outline = Color(0xFF747974)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003300),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
