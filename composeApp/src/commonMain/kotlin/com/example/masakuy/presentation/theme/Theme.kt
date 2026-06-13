package com.example.masakuy.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MasakuyColorScheme = lightColorScheme(
    primary            = OrangeMain,
    onPrimary          = Color.White,
    primaryContainer   = OrangeLight,
    onPrimaryContainer = TextLight,
    secondary          = OrangeLight,
    onSecondary        = Color.White,
    background         = Color(0xFFFFFBF7),
    onBackground       = TextLight,
    surface            = Color.White,
    onSurface          = TextLight,
    surfaceVariant     = LightGray,
    onSurfaceVariant   = DarkGray,
    error              = Color(0xFFE53935),
    onError            = Color.White,
)

private val MasakuyDarkColorScheme = darkColorScheme(
    primary            = OrangeMain,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFF4A2000),
    onPrimaryContainer = Color(0xFFFFD9B8),
    secondary          = OrangeLight,
    onSecondary        = Color.White,
    background         = Color(0xFF1A1A1A),
    onBackground       = Color(0xFFF5F5F5),
    surface            = Color(0xFF2A2A2A),
    onSurface          = Color(0xFFF5F5F5),
    surfaceVariant     = Color(0xFF3A3A3A),
    onSurfaceVariant   = Color(0xFFCCCCCC),
    error              = Color(0xFFCF6679),
    onError            = Color.White,
)

@Composable
fun MasakuyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) MasakuyDarkColorScheme else MasakuyColorScheme,
        typography  = MasakuyTypography,
        content     = content
    )
}
