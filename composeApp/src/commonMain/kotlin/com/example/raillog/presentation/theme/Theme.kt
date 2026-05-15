package com.example.raillog.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Design Tokens RailLog Nusantara
private val PrimaryNavy = Color(0xFF1E3A8A)
private val SurfaceSlate = Color(0xFFF7F9FB)
private val SuccessEmerald = Color(0xFF10B981)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    background = SurfaceSlate,
    surface = SurfaceSlate,
    primaryContainer = PrimaryNavy.copy(alpha = 0.1f),
    onPrimary = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryNavy.copy(alpha = 0.8f),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    primaryContainer = PrimaryNavy.copy(alpha = 0.3f),
    onPrimary = Color.White
)

@Composable
fun RailLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}