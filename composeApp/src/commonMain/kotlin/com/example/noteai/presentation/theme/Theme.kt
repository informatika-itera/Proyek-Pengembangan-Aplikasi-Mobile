package com.example.noteai.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS ====================

private val White = Color(0xFFFFFFFF)
private val Black = Color(0xFF000000)
private val GrayDark = Color(0xFF1A1A1A)
private val GrayMedium = Color(0xFF333333)
private val GrayLight = Color(0xFFCCCCCC)

// ==================== COLOR SCHEME ====================

private val OledColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = White,
    onPrimaryContainer = Black,
    
    secondary = GrayLight,
    onSecondary = Black,
    secondaryContainer = GrayMedium,
    onSecondaryContainer = White,
    
    tertiary = White,
    onTertiary = Black,
    
    background = Black,
    onBackground = White,
    
    surface = Black,
    onSurface = White,
    
    surfaceVariant = GrayDark,
    onSurfaceVariant = GrayLight,
    
    outline = GrayMedium,
    outlineVariant = GrayDark,
    
    error = Color(0xFFCF6679),
    onError = Black
)

// ==================== THEME ====================

@Composable
fun DailyBlissTheme(
    darkTheme: Boolean = true, 
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OledColorScheme,
        typography = Typography,
        content = content
    )
}
