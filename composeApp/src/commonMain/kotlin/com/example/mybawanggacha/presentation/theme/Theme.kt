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
    primary = CodeGeassColors.LelouchPrimary,
    onPrimary = CodeGeassColors.LelouchOnPrimary,
    primaryContainer = CodeGeassColors.LelouchPrimaryContainer,
    onPrimaryContainer = CodeGeassColors.LelouchOnPrimaryContainer,
    secondary = CodeGeassColors.LelouchSecondary,
    onSecondary = CodeGeassColors.LelouchOnSecondary,
    secondaryContainer = CodeGeassColors.LelouchSecondaryContainer,
    onSecondaryContainer = CodeGeassColors.LelouchOnSecondaryContainer,
    tertiary = CodeGeassColors.LelouchTertiary,
    onTertiary = CodeGeassColors.LelouchOnTertiary,
    tertiaryContainer = CodeGeassColors.LelouchTertiaryContainer,
    onTertiaryContainer = CodeGeassColors.LelouchOnTertiaryContainer,
    error = CodeGeassColors.LelouchError,
    onError = CodeGeassColors.LelouchOnError,
    errorContainer = CodeGeassColors.LelouchErrorContainer,
    onErrorContainer = CodeGeassColors.LelouchOnErrorContainer,
    background = CodeGeassColors.LelouchBackground,
    onBackground = CodeGeassColors.LelouchOnBackground,
    surface = CodeGeassColors.LelouchSurface,
    onSurface = CodeGeassColors.LelouchOnSurface,
    surfaceVariant = CodeGeassColors.LelouchSurfaceVariant,
    onSurfaceVariant = CodeGeassColors.LelouchOnSurfaceVariant,
    outline = CodeGeassColors.LelouchOutline
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
