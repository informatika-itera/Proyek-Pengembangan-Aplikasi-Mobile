package com.example.noteai.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS ====================

// Light Theme - Warm Amber / Cream
private val Primary = Color(0xFFC45A14)
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFFFDBCA)
private val OnPrimaryContainer = Color(0xFF3F1200)

private val Secondary = Color(0xFF7A5C46)
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFFFDCC5)
private val OnSecondaryContainer = Color(0xFF2C1608)

private val Tertiary = Color(0xFF5F6537)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFE4EBB1)
private val OnTertiaryContainer = Color(0xFF1C2000)

private val Error = Color(0xFFBA1A1A)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF410002)

// Background & Surface Light
private val BackgroundLight = Color(0xFFFFF8F2)
private val OnBackgroundLight = Color(0xFF241A14)

private val SurfaceLight = Color(0xFFFFFBF7)
private val OnSurfaceLight = Color(0xFF241A14)

private val SurfaceVariantLight = Color(0xFFF4DED1)
private val OnSurfaceVariantLight = Color(0xFF53443C)

private val OutlineLight = Color(0xFF85736A)

// Dark Theme - Warm Amber / Dark Coffee
private val PrimaryDark = Color(0xFFFFB68F)
private val OnPrimaryDark = Color(0xFF682800)
private val PrimaryContainerDark = Color(0xFF933C00)
private val OnPrimaryContainerDark = Color(0xFFFFDBCA)

private val SecondaryDark = Color(0xFFE8BFA3)
private val OnSecondaryDark = Color(0xFF442A18)
private val SecondaryContainerDark = Color(0xFF60402D)
private val OnSecondaryContainerDark = Color(0xFFFFDCC5)

private val TertiaryDark = Color(0xFFC8CE98)
private val OnTertiaryDark = Color(0xFF303500)
private val TertiaryContainerDark = Color(0xFF474D1D)
private val OnTertiaryContainerDark = Color(0xFFE4EBB1)

private val ErrorDark = Color(0xFFFFB4AB)
private val OnErrorDark = Color(0xFF690005)
private val ErrorContainerDark = Color(0xFF93000A)
private val OnErrorContainerDark = Color(0xFFFFDAD6)

// Background & Surface Dark
private val BackgroundDark = Color(0xFF1A120D)
private val OnBackgroundDark = Color(0xFFF2DFD4)

private val SurfaceDark = Color(0xFF211812)
private val OnSurfaceDark = Color(0xFFF2DFD4)

private val SurfaceVariantDark = Color(0xFF53443C)
private val OnSurfaceVariantDark = Color(0xFFD7C2B7)

private val OutlineDark = Color(0xFFA08D83)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,

    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,

    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    background = BackgroundLight,
    onBackground = OnBackgroundLight,

    surface = SurfaceLight,
    onSurface = OnSurfaceLight,

    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,

    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,

    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = OutlineDark
)

// ==================== THEME ====================

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}