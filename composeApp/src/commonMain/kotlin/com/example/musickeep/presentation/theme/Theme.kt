package com.example.musickeep.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS (MusicKeep Style) ====================

private val MusicPrimary = Color(0xFF1DB954) // Vibrant Green (Spotify-like)
private val MusicBlack = Color(0xFF121212)
private val MusicSurface = Color(0xFF1E1E1E)
private val MusicGray = Color(0xFFB3B3B3)

private val DarkColorScheme = darkColorScheme(
    primary = MusicPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF006622),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFBB86FC),
    onSecondary = Color.Black,
    background = MusicBlack,
    onBackground = Color.White,
    surface = MusicSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = MusicGray,
    outline = Color(0xFF404040)
)

private val LightColorScheme = lightColorScheme(
    primary = MusicPrimary,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = MusicBlack,
    surface = Color(0xFFF5F5F5),
    onSurface = MusicBlack
    // Fallback to defaults for others
)

@Composable
fun MusicKeepTheme(
    darkTheme: Boolean = true, // Default to dark for music apps
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
