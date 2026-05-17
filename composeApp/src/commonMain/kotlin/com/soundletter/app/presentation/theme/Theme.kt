package com.soundletter.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DeepNavy = Color(0xFF0B0E14)
private val ElectricBlue = Color(0xFF00E5FF)
private val CyanAccent = Color(0xFF00B8D4)
private val GlassWhite = Color(0x1AFFFFFF)
private val BorderWhite = Color(0x33FFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = CyanAccent,
    background = DeepNavy,
    surface = DeepNavy,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun SoundLetterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

object SoundLetterColors {
    val GlassBackground = GlassWhite
    val GlassBorder = BorderWhite
    val BackgroundGradient = listOf(DeepNavy, Color(0xFF161B22))
}
