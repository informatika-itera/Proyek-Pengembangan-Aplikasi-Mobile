package com.example.rewind.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class RewindColorScheme(
    val surfaceElevated: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val borderSubtle: Color,
    val borderGold: Color,
)

val DarkRewindColors = RewindColorScheme(
    surfaceElevated = SurfaceElevated,
    textSecondary = TextSecondary,
    textMuted = TextMuted,
    borderSubtle = BorderSubtle,
    borderGold = BorderGold,
)

val LightRewindColors = RewindColorScheme(
    surfaceElevated = Color(0xFFEDD9C0),
    textSecondary = Color(0xFF6B4A1A),
    textMuted = Color(0xFF8B6040),
    borderSubtle = Color(0xFFD4A87A),
    borderGold = Color(0xFFB07830),
)

val LocalRewindColors = staticCompositionLocalOf { DarkRewindColors }