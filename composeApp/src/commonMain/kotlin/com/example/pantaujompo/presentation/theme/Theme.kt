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

// ===== LIGHT MODE PALETTE =====
val LightBackground = Color(0xFFF0F2F5)
val LightSurface = Color(0xFFFFFFFF)
val LightPrimary = Color(0xFF00897B)       // Teal - good contrast on white
val LightSecondary = Color(0xFF0097A7)
val LightOnBackground = Color(0xFF0D0D0D)  // Near-black for max readability
val LightOnSurface = Color(0xFF1A1A1A)
val LightSecondaryText = Color(0xFF4A4A4A)
val LightOutline = Color(0xFFBDBDBD)

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
    tertiary = Color(0xFF7B1FA2),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFE8ECF0),
    onSurfaceVariant = LightSecondaryText,
    error = Color(0xFFD32F2F),
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