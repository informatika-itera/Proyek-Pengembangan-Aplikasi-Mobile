package com.example.foodsaver.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = FreshGreen,
    onPrimary = Color.White,
    primaryContainer = FreshGreenLight,
    onPrimaryContainer = FreshGreenDark,
    secondary = WarningOrange,
    onSecondary = Color.White,
    background = BackgroundCream,
    onBackground = TextMain,
    surface = Color.White,
    onSurface = TextMain,
    error = DangerRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = FreshGreen,
    onPrimary = Color.White,
    secondary = WarningOrange,
    error = DangerRed
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
