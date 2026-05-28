package com.example.foodsaver.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = SafeBgLight,
    onPrimaryContainer = PrimaryDarkLight,
    secondary = WarningTextLight,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextMainLight,
    surface = SurfaceLight,
    onSurface = TextMainLight,
    error = ExpiredTextLight,
    onError = Color.White,
    surfaceVariant = SafeBgLight,
    onSurfaceVariant = TextSecondaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = BackgroundDark,
    primaryContainer = SafeBgDark,
    onPrimaryContainer = PrimaryDarkDark,
    secondary = WarningTextDark,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextMainDark,
    surface = SurfaceDark,
    onSurface = TextMainDark,
    error = ExpiredTextDark,
    onError = Color.White,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondaryDark
)

@Composable
fun FoodSaverTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
