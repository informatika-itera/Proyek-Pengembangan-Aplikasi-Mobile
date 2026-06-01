package com.example.neurodeck.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ════════════════════════════════════════════════════════════════════════════
// NeuroDeck Theme Entry Point
//
// Apply ke root composition di App.kt:
//   neurodeckTheme(darkTheme = isSystemInDarkTheme()) {
//       AppNavHost(...)
//   }
//
// Theme menyediakan:
//   1. ColorScheme — Vivid Logic (light) / Midnight (dark)
//   2. Typography  — Bricolage Grotesque + JetBrains Mono
//
// Default: ikut system (light atau dark). User bisa override via
// UserPreferencesRepository.themeMode (Light/Dark/System).
// ════════════════════════════════════════════════════════════════════════════

// ════════════════════════════════════════════════════════════════════════════
// COLOR SCHEMES — Material 3 standard mapping
// ════════════════════════════════════════════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    primary = LightTokens.Primary,
    onPrimary = LightTokens.OnPrimary,
    primaryContainer = LightTokens.PrimaryContainer,
    onPrimaryContainer = LightTokens.OnPrimaryContainer,

    secondary = LightTokens.Secondary,
    onSecondary = LightTokens.OnSecondary,
    secondaryContainer = LightTokens.SecondaryContainer,
    onSecondaryContainer = LightTokens.OnSecondaryContainer,

    tertiary = LightTokens.Tertiary,
    onTertiary = LightTokens.OnTertiary,
    tertiaryContainer = LightTokens.TertiaryContainer,
    onTertiaryContainer = LightTokens.OnTertiaryContainer,

    error = LightTokens.Error,
    onError = LightTokens.OnError,
    errorContainer = LightTokens.ErrorContainer,
    onErrorContainer = LightTokens.OnErrorContainer,

    background = LightTokens.Background,
    onBackground = LightTokens.OnBackground,
    surface = LightTokens.Surface,
    onSurface = LightTokens.OnSurface,
    surfaceVariant = LightTokens.SurfaceVariant,
    onSurfaceVariant = LightTokens.OnSurfaceVariant,
    outline = LightTokens.Outline,
    outlineVariant = LightTokens.OutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTokens.Primary,
    onPrimary = DarkTokens.OnPrimary,
    primaryContainer = DarkTokens.PrimaryContainer,
    onPrimaryContainer = DarkTokens.OnPrimaryContainer,

    secondary = DarkTokens.Secondary,
    onSecondary = DarkTokens.OnSecondary,
    secondaryContainer = DarkTokens.SecondaryContainer,
    onSecondaryContainer = DarkTokens.OnSecondaryContainer,

    tertiary = DarkTokens.Tertiary,
    onTertiary = DarkTokens.OnTertiary,
    tertiaryContainer = DarkTokens.TertiaryContainer,
    onTertiaryContainer = DarkTokens.OnTertiaryContainer,

    error = DarkTokens.Error,
    onError = DarkTokens.OnError,
    errorContainer = DarkTokens.ErrorContainer,
    onErrorContainer = DarkTokens.OnErrorContainer,

    background = DarkTokens.Background,
    onBackground = DarkTokens.OnBackground,
    surface = DarkTokens.Surface,
    onSurface = DarkTokens.OnSurface,
    surfaceVariant = DarkTokens.SurfaceVariant,
    onSurfaceVariant = DarkTokens.OnSurfaceVariant,
    outline = DarkTokens.Outline,
    outlineVariant = DarkTokens.OutlineVariant,
)

// ════════════════════════════════════════════════════════════════════════════
// THEME COMPOSABLE
// ════════════════════════════════════════════════════════════════════════════

/**
 * Root theme wrapper untuk NeuroDeck.
 *
 * @param darkTheme  True kalau pakai dark color scheme. Default mengikuti
 *                   system setting (isSystemInDarkTheme).
 * @param content    Composable subtree yang akan inherit MaterialTheme.
 */
@Composable
fun neurodeckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = neuroDeckTypography(),
        content = content,
    )
}