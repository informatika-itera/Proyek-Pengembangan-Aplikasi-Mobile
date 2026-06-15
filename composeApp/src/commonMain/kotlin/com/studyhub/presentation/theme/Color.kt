package com.studyhub.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Palette Refined ──
val CreamBackground = Color(0xFFF5F0E8)
val DarkBackground = Color(0xFF1A1A1A)
val GoldenSuedeDark = Color(0xFF8B7355)
val GoldenSuedeLight = Color(0xFFC4A882)
val AccentOrange = Color(0xFFFF6B4A)
val AccentCoral = Color(0xFFFF8C69)
val HighlightGold = Color(0xFFF5A623)
val DarkText = Color(0xFF2C2416)
val MutedText = Color(0xFF888780)

// ── Light Mode ──
val Primary = GoldenSuedeDark
val OnPrimary = Color.White
val PrimaryContainer = GoldenSuedeLight.copy(alpha = 0.2f)
val OnPrimaryContainer = DarkText
val Secondary = Color(0xFF6B8F71)
val OnSecondary = Color.White
val Tertiary = HighlightGold
val OnTertiary = Color.White
val Background = CreamBackground
val OnBackground = DarkText
val Surface = Color.White
val OnSurface = DarkText
val SurfaceVariant = Color(0xFFF5F2E8)
val OnSurfaceVariant = MutedText
val Error = Color(0xFFB91C1C)
val OnError = Color.White

// ── Dark Mode ──
val PrimaryDark = GoldenSuedeLight
val OnPrimaryDark = Color.White
val PrimaryContainerDark = GoldenSuedeDark.copy(alpha = 0.3f)
val OnPrimaryContainerDark = Color.White
val SecondaryDark = Color(0xFFA8C9AD)
val OnSecondaryDark = Color(0xFF1A3D20)
val TertiaryDark = HighlightGold.copy(alpha = 0.8f)
val OnTertiaryDark = Color.Black
val BackgroundDark = DarkBackground
val OnBackgroundDark = Color.White
val SurfaceDark = Color(0xFF252525)
val OnSurfaceDark = Color.White
val SurfaceVariantDark = Color(0xFF333333)
val OnSurfaceVariantDark = Color(0xFFB0B0B0)
val ErrorDark = Color(0xFFF2B8B5)
val OnErrorDark = Color(0xFF601410)

// ── Semantic Colors ──
val PriorityHigh = Color(0xFFFF4D4D)
val PriorityHighContainer = Color(0xFFFF4D4D).copy(alpha = 0.15f)
val PriorityMedium = HighlightGold
val PriorityMediumContainer = HighlightGold.copy(alpha = 0.15f)
val PriorityLow = Color(0xFF22C55E)
val PriorityLowContainer = Color(0xFF22C55E).copy(alpha = 0.15f)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    onError = OnError,
    outline = MutedText.copy(alpha = 0.5f),
    outlineVariant = Color.Black.copy(alpha = 0.1f)
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    outline = Color.White.copy(alpha = 0.3f),
    outlineVariant = Color.White.copy(alpha = 0.1f)
)
