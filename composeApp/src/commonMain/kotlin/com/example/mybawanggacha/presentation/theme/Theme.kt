package com.example.mybawanggacha.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = CodeGeassColors.CcPrimary,
    onPrimary = CodeGeassColors.CcOnPrimary,
    primaryContainer = CodeGeassColors.CcPrimaryContainer,
    onPrimaryContainer = CodeGeassColors.CcOnPrimaryContainer,
    secondary = CodeGeassColors.CcSecondary,
    onSecondary = CodeGeassColors.CcOnSecondary,
    secondaryContainer = CodeGeassColors.CcSecondaryContainer,
    onSecondaryContainer = CodeGeassColors.CcOnSecondaryContainer,
    tertiary = CodeGeassColors.CcTertiary,
    onTertiary = CodeGeassColors.CcOnTertiary,
    tertiaryContainer = CodeGeassColors.CcTertiaryContainer,
    onTertiaryContainer = CodeGeassColors.CcOnTertiaryContainer,
    error = CodeGeassColors.CcError,
    onError = CodeGeassColors.CcOnError,
    errorContainer = CodeGeassColors.CcErrorContainer,
    onErrorContainer = CodeGeassColors.CcOnErrorContainer,
    background = CodeGeassColors.CcBackground,
    onBackground = CodeGeassColors.CcOnBackground,
    surface = CodeGeassColors.CcSurface,
    onSurface = CodeGeassColors.CcOnSurface,
    surfaceVariant = CodeGeassColors.CcSurfaceVariant,
    onSurfaceVariant = CodeGeassColors.CcOnSurfaceVariant,
    outline = CodeGeassColors.CcOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = CodeGeassColors.LelouchTertiaryContainer,
    onPrimary = CodeGeassColors.LelouchOnTertiaryContainer,
    primaryContainer = CodeGeassColors.LelouchPrimaryContainer,
    onPrimaryContainer = CodeGeassColors.LelouchOnPrimaryContainer,
    secondary = CodeGeassColors.LelouchSecondary,
    onSecondary = CodeGeassColors.LelouchOnSecondary,
    tertiary = CodeGeassColors.LelouchPrimary,
    onTertiary = CodeGeassColors.LelouchOnPrimary,
    background = CodeGeassColors.LelouchDeepBlack,
    onBackground = CodeGeassColors.LelouchOnBackground,
    surface = CodeGeassColors.LelouchSurfaceDark,
    onSurface = CodeGeassColors.LelouchOnSurface,
    surfaceVariant = CodeGeassColors.LelouchSurfaceVariant,
    onSurfaceVariant = CodeGeassColors.LelouchOnBackground,
    outline = CodeGeassColors.LelouchPrimary
)

// ==================== THEME ====================

@Composable
fun MBGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
