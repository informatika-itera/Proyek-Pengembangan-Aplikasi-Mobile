package com.example.fitgen.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = FitOrange,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = FitOrangeContainer,
    onPrimaryContainer = OnFitOrangeContainer,
    secondary = FitGreen,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = FitGreenContainer,
    onSecondaryContainer = OnFitGreenContainer,
    tertiary = FitBlue,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = FitBlueContainer,
    onTertiaryContainer = OnFitBlueContainer,
    error = FitError,
    onError = OnFitError,
    errorContainer = FitErrorContainer,
    onErrorContainer = OnFitErrorContainer,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = FitOrangeLight,
    onPrimary = Color(0xFF4A1800),
    primaryContainer = FitOrange,
    onPrimaryContainer = FitOrangeContainer,
    secondary = FitGreen,
    onSecondary = FitGreenDark,
    secondaryContainer = FitGreenDark,
    onSecondaryContainer = FitGreenContainer,
    tertiary = FitBlue,
    onTertiary = FitBlueDark,
    tertiaryContainer = FitBlueDark,
    onTertiaryContainer = FitBlueContainer,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

// ==================== THEME ====================

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