package com.example.noteai.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== TWILIGHT PALETTE COLORS ====================

private val DeepOceanBlue = Color(0xFF26425A)
private val TwilightBlue = Color(0xFF86A8CF)
private val LightSoftPink = Color(0xFFE1CBD7)
private val MauvePink = Color(0xFFC38EB4)
private val MidnightDark = Color(0xFF0A1128) // Warna ekstra gelap untuk background Dark Mode
private val White = Color(0xFFFFFFFF)

// ==================== LIGHT MODE ====================

private val PrimaryLight = DeepOceanBlue
private val OnPrimaryLight = White
private val PrimaryContainerLight = TwilightBlue
private val OnPrimaryContainerLight = MidnightDark

private val SecondaryLight = MauvePink
private val OnSecondaryLight = White
private val SecondaryContainerLight = LightSoftPink
private val OnSecondaryContainerLight = DeepOceanBlue

private val TertiaryLight = TwilightBlue
private val OnTertiaryLight = White
private val TertiaryContainerLight = LightSoftPink
private val OnTertiaryContainerLight = MidnightDark

private val ErrorLight = Color(0xFFB3261E)
private val OnErrorLight = White
private val ErrorContainerLight = Color(0xFFF9DEDC)
private val OnErrorContainerLight = Color(0xFF410E0B)

private val BackgroundLight = Color(0xFFF8F9FA) // Off-white lembut agar mata tidak cepat lelah
private val OnBackgroundLight = MidnightDark
private val SurfaceLight = Color(0xFFF8F9FA)
private val OnSurfaceLight = MidnightDark
private val SurfaceVariantLight = LightSoftPink
private val OnSurfaceVariantLight = DeepOceanBlue

private val OutlineLight = DeepOceanBlue.copy(alpha = 0.5f)

// ==================== DARK MODE ====================

private val PrimaryDark = TwilightBlue // Di mode gelap, warna primary dibuat lebih terang
private val OnPrimaryDark = MidnightDark
private val PrimaryContainerDark = DeepOceanBlue
private val OnPrimaryContainerDark = TwilightBlue

private val SecondaryDark = LightSoftPink
private val OnSecondaryDark = MidnightDark
private val SecondaryContainerDark = MauvePink
private val OnSecondaryContainerDark = White

private val TertiaryDark = MauvePink
private val OnTertiaryDark = MidnightDark
private val TertiaryContainerDark = DeepOceanBlue
private val OnTertiaryContainerDark = LightSoftPink

private val ErrorDark = Color(0xFFF2B8B5)
private val OnErrorDark = Color(0xFF601410)
private val ErrorContainerDark = Color(0xFF8C1D18)
private val OnErrorContainerDark = Color(0xFFF9DEDC)

private val BackgroundDark = MidnightDark
private val OnBackgroundDark = LightSoftPink
private val SurfaceDark = MidnightDark
private val OnSurfaceDark = LightSoftPink
private val SurfaceVariantDark = DeepOceanBlue
private val OnSurfaceVariantDark = LightSoftPink

private val OutlineDark = TwilightBlue.copy(alpha = 0.5f)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
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