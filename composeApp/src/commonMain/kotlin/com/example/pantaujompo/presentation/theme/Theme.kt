package com.example.pantaujompo.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// MAIN tema Materia3 untuk Dark mode
private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    surface = DarkSurface,
    background = BackgroundDark,
    onPrimary = Color.Black,
    onSurface = WhiteLight,
    onBackground = WhiteLight
)

@Composable
fun PantauJompoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}