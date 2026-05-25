package com.example.sholatyuk.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}