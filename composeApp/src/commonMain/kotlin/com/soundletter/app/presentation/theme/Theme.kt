package com.soundletter.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SoundLetterColors.SkyBlue,
    onPrimary = Color.White,
    secondary = SoundLetterColors.SkyBlueDark,
    onSecondary = Color.White,
    background = SoundLetterColors.PureWhite,
    surface = SoundLetterColors.PureWhite,
    onBackground = SoundLetterColors.TextPrimaryLight,
    onSurface = SoundLetterColors.TextPrimaryLight,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = SoundLetterColors.TextSecondaryLight,
    outline = SoundLetterColors.SkyBlue,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = SoundLetterColors.SkyBlue,
    onPrimary = Color.Black,
    secondary = SoundLetterColors.SkyBlueDark,
    onSecondary = Color.Black,
    background = SoundLetterColors.PureBlack,
    surface = SoundLetterColors.DarkSurface,
    onBackground = SoundLetterColors.TextPrimaryDark,
    onSurface = SoundLetterColors.TextPrimaryDark,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = SoundLetterColors.TextSecondaryDark,
    outline = SoundLetterColors.SkyBlue,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun SoundLetterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
