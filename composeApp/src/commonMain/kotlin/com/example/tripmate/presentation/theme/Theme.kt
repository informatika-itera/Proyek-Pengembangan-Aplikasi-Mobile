package com.example.tripmate.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Palette — clean modern travel app
private val Navy        = Color(0xFF0D1B2A)
private val NavyDark    = Color(0xFF060F17)
private val Indigo      = Color(0xFF1A3C5E)
private val Teal        = Color(0xFF0F7B6C)
private val TealLight   = Color(0xFF13A594)
private val Cream       = Color(0xFFF5F0E8)
private val Surface     = Color(0xFFFAF8F5)
private val SurfaceDark = Color(0xFF111820)
private val CardDark    = Color(0xFF1A2530)
private val TextPrimary = Color(0xFF0D1B2A)
private val TextMuted   = Color(0xFF5C6B7A)
private val White       = Color(0xFFFFFFFF)
private val ErrorRed    = Color(0xFFD62839)

private val LightColors = lightColorScheme(
    primary            = Navy,
    onPrimary          = White,
    primaryContainer   = Indigo,
    onPrimaryContainer = White,
    secondary          = Teal,
    onSecondary        = White,
    secondaryContainer = Color(0xFFD4F0EC),
    onSecondaryContainer = Color(0xFF003730),
    tertiary           = Color(0xFF8B5E3C),
    onTertiary         = White,
    background         = Surface,
    onBackground       = TextPrimary,
    surface            = White,
    onSurface          = TextPrimary,
    surfaceVariant     = Cream,
    onSurfaceVariant   = TextMuted,
    error              = ErrorRed,
    onError            = White,
    outline            = Color(0xFFCDD5DC)
)

private val DarkColors = darkColorScheme(
    primary            = Color(0xFF4A9EBA),
    onPrimary          = NavyDark,
    primaryContainer   = Indigo,
    onPrimaryContainer = White,
    secondary          = TealLight,
    onSecondary        = NavyDark,
    secondaryContainer = Color(0xFF004D45),
    onSecondaryContainer = Color(0xFF80FFEE),
    background         = SurfaceDark,
    onBackground       = Color(0xFFE8EEF4),
    surface            = CardDark,
    onSurface          = Color(0xFFE8EEF4),
    surfaceVariant     = Color(0xFF1F2D3A),
    onSurfaceVariant   = Color(0xFF8FA3B3),
    error              = Color(0xFFFF6B7A),
    onError            = NavyDark,
    outline            = Color(0xFF2A3D4F)
)

@Composable
fun TripMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
