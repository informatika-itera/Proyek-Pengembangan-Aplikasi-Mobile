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
val Background = CreamBackground
val OnBackground = DarkText
val Surface = Color.White
val OnSurface = DarkText
val SurfaceVariant = Color(0xFFF5F2E8)
val OnSurfaceVariant = MutedText

// ── Dark Mode ──
val PrimaryDark = GoldenSuedeLight
val OnPrimaryDark = Color.White
val PrimaryContainerDark = GoldenSuedeDark.copy(alpha = 0.3f)
val OnPrimaryContainerDark = Color.White
val SecondaryDark = Color(0xFFA8C9AD)
val OnSecondaryDark = Color(0xFF1A3D20)
val BackgroundDark = DarkBackground
val OnBackgroundDark = Color.White
val SurfaceDark = Color(0xFF252525)
val OnSurfaceDark = Color.White
val SurfaceVariantDark = Color(0xFF333333)
val OnSurfaceVariantDark = Color(0xFFB0B0B0)

// ── Semantic Colors ──
val PriorityHigh = Color(0xFFFF4D4D)
val PriorityMedium = HighlightGold
val PriorityLow = Color(0xFF22C55E)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = MutedText.copy(alpha = 0.5f),
    outlineVariant = Color.White.copy(alpha = 0.25f)
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = Color.White.copy(alpha = 0.3f),
    outlineVariant = Color.White.copy(alpha = 0.1f)
)
