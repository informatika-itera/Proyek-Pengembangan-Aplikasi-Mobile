package com.example.Roomie.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ITERA Official Palette
private val IteraGold = Color(0xFFD4AF37)
private val IteraGoldLight = Color(0xFFFFD700)
private val IteraRed = Color(0xFFB22222)
private val DeepBlack = Color(0xFF121212)
private val SurfaceGrey = Color(0xFF1E1E1E)

private val DarkColorScheme = darkColorScheme(
    primary = IteraGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3E3621),
    onPrimaryContainer = IteraGoldLight,
    secondary = IteraGoldLight,
    onSecondary = Color.Black,
    tertiary = IteraRed,
    onTertiary = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = SurfaceGrey,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color.LightGray,
    error = IteraRed,
    outline = IteraGold
)

// Kita paksa Dark Theme biar kerasa vibe ITERA-nya (Black & Gold)
@Composable
fun RoomieTheme(
    darkTheme: Boolean = true, // Force dark for premium look
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
