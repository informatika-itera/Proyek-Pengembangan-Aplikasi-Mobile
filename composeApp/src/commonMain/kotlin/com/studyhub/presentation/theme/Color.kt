package com.studyhub.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Light Mode ──
val Primary = Color(0xFF8B7355)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFEDE8D0)
val OnPrimaryContainer = Color(0xFF2C1F0A)
val Secondary = Color(0xFF6B8F71)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFD4EDDA)
val OnSecondaryContainer = Color(0xFF1A3D20)
val Tertiary = Color(0xFF7B6FA0)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFE8E0F5)
val OnTertiaryContainer = Color(0xFF1E1040)
val Background = Color(0xFFFAFAF7)
val OnBackground = Color(0xFF1C1B1A)
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF1C1B1A)
val SurfaceVariant = Color(0xFFF5F2E8)
val OnSurfaceVariant = Color(0xFF4A4640)
val Outline = Color(0xFFC8C4B4)
val OutlineVariant = Color(0xFFE8E4D4)
val Error = Color(0xFFB3261E)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFF9DEDC)
val OnErrorContainer = Color(0xFF410E0B)

// ── Dark Mode ──
val PrimaryDark = Color(0xFFD4C4A0)
val OnPrimaryDark = Color(0xFF3D2E10)
val PrimaryContainerDark = Color(0xFF5C4A28)
val OnPrimaryContainerDark = Color(0xFFEDE8D0)
val SecondaryDark = Color(0xFFA8C9AD)
val OnSecondaryDark = Color(0xFF1A3D20)
val SecondaryContainerDark = Color(0xFF2D5C34)
val OnSecondaryContainerDark = Color(0xFFD4EDDA)
val TertiaryDark = Color(0xFFCBBEF0)
val OnTertiaryDark = Color(0xFF32276A)
val TertiaryContainerDark = Color(0xFF493E80)
val OnTertiaryContainerDark = Color(0xFFE8E0F5)
val BackgroundDark = Color(0xFF141412)
val OnBackgroundDark = Color(0xFFE8E4D8)
val SurfaceDark = Color(0xFF1E1C18)
val OnSurfaceDark = Color(0xFFE8E4D8)
val SurfaceVariantDark = Color(0xFF2A2820)
val OnSurfaceVariantDark = Color(0xFFC8C4B0)
val OutlineDark = Color(0xFF8C8878)
val OutlineVariantDark = Color(0xFF4A4640)
val ErrorDark = Color(0xFFF2B8B5)
val OnErrorDark = Color(0xFF601410)
val ErrorContainerDark = Color(0xFF8C1D18)
val OnErrorContainerDark = Color(0xFFF9DEDC)

// ── Semantic Colors (same in both modes) ──
val PriorityHigh = Color(0xFFEF4444)
val PriorityMedium = Color(0xFFF59E0B)
val PriorityLow = Color(0xFF22C55E)

val LightColorScheme = lightColorScheme(
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
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

val DarkColorScheme = darkColorScheme(
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
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark
)
