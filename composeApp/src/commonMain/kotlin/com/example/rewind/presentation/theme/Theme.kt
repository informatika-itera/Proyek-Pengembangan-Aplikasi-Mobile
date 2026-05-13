package com.example.rewind.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== REWIND CINEMA PALETTE ====================

// Core Brand Colors
private val GoldAmber        = Color(0xFFE8A838) // Primary gold
private val TheaterRed       = Color(0xFFC0392B) // Secondary red
private val VelvetRed        = Color(0xFF8B1A1A) // Deep red accent
private val BackgroundDark   = Color(0xFF1A0A0A) // Near-black warm dark
private val SurfaceDark      = Color(0xFF2C0D0D) // Dark red-tinted surface
private val TextWarm         = Color(0xFFFAD7A0) // Warm cream text

// Derived Colors — Light Mode
private val GoldAmberLight   = Color(0xFFF5C46A) // Lighter gold for containers
private val CreamWarm        = Color(0xFFFFF8EE) // Off-white warm background
private val CreamSurface     = Color(0xFFFFF1DC) // Slightly deeper cream surface
private val RedContainer     = Color(0xFFFFDAD6) // Soft red container (light)
private val DarkBrown        = Color(0xFF3E1A0A) // Deep warm dark for text on light
private val OutlineLight     = Color(0xFFB07830) // Muted gold outline

// Derived Colors — Dark Mode
private val GoldDark         = Color(0xFFFFB951) // Brighter gold on dark bg
private val RedContainerDark = Color(0xFF5C1010) // Dark red container
private val OutlineDark      = Color(0xFF8B6020) // Subtle warm outline

private val White            = Color(0xFFFFFFFF)
private val ErrorRed         = Color(0xFFCF6679)
private val ErrorDarkText    = Color(0xFF680020)

// ==================== LIGHT MODE ====================

private val LightColorScheme = lightColorScheme(
    primary                = GoldAmber,
    onPrimary              = DarkBrown,
    primaryContainer       = GoldAmberLight,
    onPrimaryContainer     = DarkBrown,

    secondary              = TheaterRed,
    onSecondary            = White,
    secondaryContainer     = RedContainer,
    onSecondaryContainer   = VelvetRed,

    tertiary               = VelvetRed,
    onTertiary             = White,
    tertiaryContainer      = Color(0xFFFFE0DC),
    onTertiaryContainer    = DarkBrown,

    error                  = Color(0xFFB3261E),
    onError                = White,
    errorContainer         = Color(0xFFF9DEDC),
    onErrorContainer       = Color(0xFF410E0B),

    background             = CreamWarm,
    onBackground           = DarkBrown,
    surface                = CreamSurface,
    onSurface              = DarkBrown,
    surfaceVariant         = Color(0xFFEDD9C0),
    onSurfaceVariant       = Color(0xFF5C3A1A),
    outline                = OutlineLight
)

// ==================== DARK MODE ====================

private val DarkColorScheme = darkColorScheme(
    primary                = GoldDark,
    onPrimary              = BackgroundDark,
    primaryContainer       = Color(0xFF7A5010),
    onPrimaryContainer     = GoldAmberLight,

    secondary              = TheaterRed,
    onSecondary            = TextWarm,
    secondaryContainer     = RedContainerDark,
    onSecondaryContainer   = TextWarm,

    tertiary               = TextWarm,
    onTertiary             = VelvetRed,
    tertiaryContainer      = VelvetRed,
    onTertiaryContainer    = TextWarm,

    error                  = ErrorRed,
    onError                = ErrorDarkText,
    errorContainer         = Color(0xFF8C1D18),
    onErrorContainer       = Color(0xFFF9DEDC),

    background             = BackgroundDark,
    onBackground           = TextWarm,
    surface                = SurfaceDark,
    onSurface              = TextWarm,
    surfaceVariant         = Color(0xFF3D1515),
    onSurfaceVariant       = Color(0xFFE8C89A),
    outline                = OutlineDark
)

// ==================== THEME ====================

@Composable
fun RewindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}