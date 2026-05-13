package com.example.noteai.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS (DOLLAR GREEN & INK PALETTE) ====================

// Light Colors
private val Primary = Color(0xFF1B5E20)
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFA5D6A7)
private val OnPrimaryContainer = Color(0xFF002204)

private val Secondary = Color(0xFFD4AF37)
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFF3E5AB)
private val OnSecondaryContainer = Color(0xFF3E2723)

private val Tertiary = Color(0xFF455A64)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFCFD8DC)
private val OnTertiaryContainer = Color(0xFF102027)

private val Error = Color(0xFFBA1A1A)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF410002)

// ---> BACKGROUND <---
private val BackgroundLight = Color(0xFF85BB65)
private val OnBackgroundLight = Color(0xFF0A1C0E)
private val SurfaceLight = Color(0xFF85BB65)
private val OnSurfaceLight = Color(0xFF0A1C0E)
private val SurfaceVariantLight = Color(0xFFA3CBA0)
private val OnSurfaceVariantLight = Color(0xFF1E3622)

// ---> DARK MODE <---
private val BackgroundDark = Color(0xFF102B16) //
private val OnBackgroundDark = Color(0xFFD9E8DC)
private val SurfaceDark = Color(0xFF102B16)
private val OnSurfaceDark = Color(0xFFD9E8DC)
private val SurfaceVariantDark = Color(0xFF2A4A32)
private val OnSurfaceVariantDark = Color(0xFFB8CDBA)

private val OutlineLight = Color(0xFF4E6B52)
private val OutlineDark = Color(0xFF8EAA92)

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
    primary = Color(0xFF85BB65),
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF005220),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFFF4D03F),
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFF6E5A19),
    onSecondaryContainer = Color(0xFFF3E5AB),
    tertiary = Color(0xFF90A4AE),
    onTertiary = Color(0xFF102027),
    tertiaryContainer = Color(0xFF263238),
    onTertiaryContainer = Color(0xFFCFD8DC),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
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