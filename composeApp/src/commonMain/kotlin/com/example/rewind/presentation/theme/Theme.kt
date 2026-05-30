package com.example.rewind.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val GoldAmberLight   = Color(0xFFF5C46A)
private val CreamWarm        = Color(0xFFFFF8EE)
private val CreamSurface     = Color(0xFFFFF1DC)
private val RedContainer     = Color(0xFFFFDAD6)
private val DarkBrown        = Color(0xFF3E1A0A)
private val OutlineLight     = Color(0xFFB07830)

private val GoldDark         = Color(0xFFFFB951)
private val RedContainerDark = Color(0xFF5C1010)
private val OutlineDark      = Color(0xFF8B6020)

private val White            = Color(0xFFFFFFFF)
private val ErrorRed         = Color(0xFFCF6679)
private val ErrorDarkText    = Color(0xFF680020)

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

@Composable
fun RewindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val rewindColors = if (darkTheme) DarkRewindColors else LightRewindColors

    CompositionLocalProvider(LocalRewindColors provides rewindColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}