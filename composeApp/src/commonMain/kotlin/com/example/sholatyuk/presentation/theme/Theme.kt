package com.example.sholatyuk.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightTeal,
    onPrimary = TextWhite,
    secondary = Gold,
    onSecondary = DeepBlue,
    background = DeepBlue,
    onBackground = TextWhite,
    surface = CardBackground,
    onSurface = TextWhite,
    surfaceVariant = DarkTeal,
    onSurfaceVariant = TextGray
)

private val LightColorScheme = lightColorScheme(
    primary = LightTeal,
    onPrimary = TextWhite,
    secondary = Gold,
    onSecondary = DeepBlue,
    background = DeepBlue,
    onBackground = TextWhite,
    surface = CardBackground,
    onSurface = TextWhite,
    surfaceVariant = DarkTeal,
    onSurfaceVariant = TextGray
)

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Forcing a dark theme style as per the image
    val colorScheme = DarkColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
