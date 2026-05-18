package com.example.Roomie.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ITERA Official Palette
private val IteraGold = Color(0xFFD4AF37)
private val IteraGoldDark = Color(0xFF8B7500)
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

private val LightColorScheme = lightColorScheme(
    primary = IteraGold,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF4CC),
    onPrimaryContainer = IteraGoldDark,
    secondary = IteraGoldDark,
    onSecondary = Color.White,
    tertiary = IteraRed,
    onTertiary = Color.White,
    background = Color(0xFFFDFDFD),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color.DarkGray,
    error = IteraRed,
    outline = IteraGold
)

@Composable
fun RoomieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
