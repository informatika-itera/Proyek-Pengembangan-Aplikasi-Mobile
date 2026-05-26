package com.example.fitgen.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ==================== LIGHT COLOR SCHEME ====================

private val LightColorScheme = lightColorScheme(
    primary               = MaroonPrimary,
    onPrimary             = MaroonOnPrimary,
    primaryContainer      = MaroonPrimaryContainer,
    onPrimaryContainer    = MaroonOnPrimaryContainer,
    secondary             = CreamSecondary,
    onSecondary           = OnCreamSecondary,
    secondaryContainer    = CreamSecondaryContainer,
    onSecondaryContainer  = OnCreamSecondaryContainer,
    tertiary              = TertiaryLight,
    onTertiary            = OnTertiaryLight,
    tertiaryContainer     = TertiaryContainerLight,
    onTertiaryContainer   = OnTertiaryContainerLight,
    error                 = ErrorLight,
    onError               = OnErrorLight,
    errorContainer        = ErrorContainerLight,
    onErrorContainer      = OnErrorContainerLight,
    background            = BackgroundLight,
    onBackground          = OnBackgroundLight,
    surface               = SurfaceLight,
    onSurface             = OnSurfaceLight,
    surfaceVariant        = SurfaceVariantLight,
    onSurfaceVariant      = OnSurfaceVariantLight,
    outline               = OutlineLight
)

// ==================== DARK COLOR SCHEME ====================

private val DarkColorScheme = darkColorScheme(
    primary               = MaroonPrimaryDarkScheme,
    onPrimary             = MaroonOnPrimaryDarkScheme,
    primaryContainer      = MaroonPrimaryContainerDark,
    onPrimaryContainer    = MaroonOnPrimaryContainerDark,
    secondary             = CreamSecondaryDark,
    onSecondary           = OnCreamSecondaryDark,
    secondaryContainer    = CreamSecondaryContainerDark,
    onSecondaryContainer  = OnCreamSecondaryContainerDark,
    tertiary              = TertiaryDark,
    onTertiary            = OnTertiaryDark,
    tertiaryContainer     = TertiaryContainerDark,
    onTertiaryContainer   = OnTertiaryContainerDark,
    error                 = ErrorDark,
    onError               = OnErrorDark,
    errorContainer        = ErrorContainerDark,
    onErrorContainer      = OnErrorContainerDark,
    background            = BackgroundDark,
    onBackground          = OnBackgroundDark,
    surface               = SurfaceDark,
    onSurface             = OnSurfaceDark,
    surfaceVariant        = SurfaceVariantDark,
    onSurfaceVariant      = OnSurfaceVariantDark,
    outline               = OutlineDark
)

// ==================== FITGEN THEME ====================

@Composable
fun FitGenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = getTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = typography,
        content     = content
    )
}