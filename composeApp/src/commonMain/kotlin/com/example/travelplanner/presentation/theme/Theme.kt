package com.example.travelplanner.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── PALETTE: Sophisticated Travel (Semi-Formal Elegant) ──────────────
// Inspirasi: premium travel magazine, first-class lounge, fine map cartography

// Primary — Deep Ink Navy (kepercayaan, otoritas, elegan)
private val InkNavy        = Color(0xFF1B3A5C)
private val OnInkNavy      = Color(0xFFFFFFFF)
private val NavyContainer  = Color(0xFFD6E8F5)
private val OnNavyContainer= Color(0xFF001C35)

// Secondary — Antique Gold (kemewahan, premium, hangat)
private val AntiqueGold        = Color(0xFFB8893A)
private val OnAntiqueGold      = Color(0xFFFFFFFF)
private val GoldContainer      = Color(0xFFF5E6C8)
private val OnGoldContainer    = Color(0xFF3B2500)

// Tertiary — Slate Teal (tenang, sophisticated)
private val SlateTeal          = Color(0xFF3D7A6F)
private val OnSlateTeal        = Color(0xFFFFFFFF)
private val TealContainer      = Color(0xFFCCEDE7)
private val OnTealContainer    = Color(0xFF002B25)

// Neutral warm
private val ErrorRed           = Color(0xFFC0392B)
private val OnErrorRed         = Color(0xFFFFFFFF)
private val ErrorContainer     = Color(0xFFFFDAD6)
private val OnErrorContainer   = Color(0xFF410002)

// Light surfaces — warm off-white (premium paper feel)
private val BackgroundLight    = Color(0xFFF6F4EF)
private val OnBackgroundLight  = Color(0xFF151210)
private val SurfaceLight       = Color(0xFFFFFEFC)
private val OnSurfaceLight     = Color(0xFF151210)
private val SurfaceVarLight    = Color(0xFFECE7DC)
private val OnSurfaceVarLight  = Color(0xFF3C3628)
private val OutlineLight       = Color(0xFF8C7D60)

// Dark surfaces
private val BackgroundDark     = Color(0xFF111009)
private val OnBackgroundDark   = Color(0xFFF0EDE5)
private val SurfaceDark        = Color(0xFF1A1812)
private val OnSurfaceDark      = Color(0xFFF0EDE5)
private val SurfaceVarDark     = Color(0xFF302D22)
private val OnSurfaceVarDark   = Color(0xFFCEC4AB)
private val OutlineDark        = Color(0xFF9A8E74)

// ── COLOR SCHEMES ─────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary              = InkNavy,
    onPrimary            = OnInkNavy,
    primaryContainer     = NavyContainer,
    onPrimaryContainer   = OnNavyContainer,
    secondary            = AntiqueGold,
    onSecondary          = OnAntiqueGold,
    secondaryContainer   = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary             = SlateTeal,
    onTertiary           = OnSlateTeal,
    tertiaryContainer    = TealContainer,
    onTertiaryContainer  = OnTealContainer,
    error                = ErrorRed,
    onError              = OnErrorRed,
    errorContainer       = ErrorContainer,
    onErrorContainer     = OnErrorContainer,
    background           = BackgroundLight,
    onBackground         = OnBackgroundLight,
    surface              = SurfaceLight,
    onSurface            = OnSurfaceLight,
    surfaceVariant       = SurfaceVarLight,
    onSurfaceVariant     = OnSurfaceVarLight,
    outline              = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary              = Color(0xFF8DB9DC),
    onPrimary            = Color(0xFF00304F),
    primaryContainer     = Color(0xFF004B78),
    onPrimaryContainer   = NavyContainer,
    secondary            = Color(0xFFDDB96A),
    onSecondary          = Color(0xFF3B2500),
    secondaryContainer   = Color(0xFF583900),
    onSecondaryContainer = GoldContainer,
    tertiary             = Color(0xFF7ECABD),
    onTertiary           = Color(0xFF003730),
    tertiaryContainer    = Color(0xFF1F5149),
    onTertiaryContainer  = TealContainer,
    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = ErrorContainer,
    background           = BackgroundDark,
    onBackground         = OnBackgroundDark,
    surface              = SurfaceDark,
    onSurface            = OnSurfaceDark,
    surfaceVariant       = SurfaceVarDark,
    onSurfaceVariant     = OnSurfaceVarDark,
    outline              = OutlineDark
)

// ── TYPOGRAPHY ────────────────────────────────────────────────────────

private val TravelTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 56.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 44.sp, lineHeight = 52.sp),
    displaySmall  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = (-0.3).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 30.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.15.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp),
    titleSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp, letterSpacing = 0.15.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = 0.25.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.4.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium   = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp)
)

// ── THEME ─────────────────────────────────────────────────────────────

@Composable
fun TravelPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme  = colorScheme,
        typography   = TravelTypography,
        content      = content
    )
}

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = TravelPlannerTheme(darkTheme = darkTheme, content = content)
