package com.example.hujjah.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LuxuryGold,
    onPrimary = PureWhite,
    background = PureWhite,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = PureWhite,
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = NeonGold,
    onPrimary = TrueBlack,
    background = TrueBlack,
    onBackground = TextPrimaryDark,
    surface = TrueBlack,
    onSurface = TextPrimaryDark,
    surfaceVariant = TrueBlack,
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark
)

data class HujjahColors(
    val isDarkTheme: Boolean,
    val goldHighlight: Color,
    val islamicGreen: Color
)

val LocalHujjahColors = staticCompositionLocalOf {
    HujjahColors(
        isDarkTheme = false,
        goldHighlight = LuxuryGold,
        islamicGreen = DarkIslamicGreen
    )
}

@Composable
fun HujjahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val hujjahColors = HujjahColors(
        isDarkTheme = darkTheme,
        goldHighlight = if (darkTheme) NeonGold else LuxuryGold,
        islamicGreen = DarkIslamicGreen
    )

    CompositionLocalProvider(LocalHujjahColors provides hujjahColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
