package com.example.masakuy.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = OrangeMain,
    secondary = OrangeLight,
    tertiary = TextDark
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeMain,
    secondary = OrangeLight,
    tertiary = TextLight
)

@Composable
fun MasakuyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MasakuyTypography,
        content = content
    )
}