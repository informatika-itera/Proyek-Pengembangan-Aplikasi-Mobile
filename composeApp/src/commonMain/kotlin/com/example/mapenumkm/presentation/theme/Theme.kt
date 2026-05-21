package com.example.mapenumkm.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ==================== COLORS ====================

private val Primary = Color(0xFF16A34A)
private val PrimaryDark = Color(0xFF15803D)
private val PrimaryLight = Color(0xFFDCFCE7)
private val OnPrimary = Color(0xFFFFFFFF)

private val Secondary = Color(0xFF60A5FA) // Blue Accent
private val Tertiary = Color(0xFFFACC15) // Yellow Accent
val PurpleAccent = Color(0xFFA78BFA)

private val BackgroundLight = Color(0xFFFBFBFF)
private val OnBackgroundLight = Color(0xFF1C1B1F)
private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF1C1B1F)
private val SurfaceVariantLight = Color(0xFFF3F4F6)
private val OnSurfaceVariantLight = Color(0xFF49454F)
private val OutlineLight = Color(0xFFE5E7EB)

private val BackgroundDark = Color(0xFF1C1B1F)
private val OnBackgroundDark = Color(0xFFE6E1E5)
private val SurfaceDark = Color(0xFF1C1B1F)
private val OnSurfaceDark = Color(0xFFE6E1E5)
private val SurfaceVariantDark = Color(0xFF49454F)
private val OnSurfaceVariantDark = Color(0xFFCAC4D0)
private val OutlineDark = Color(0xFF938F99)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    tertiary = Tertiary,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF16A34A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF15803D),
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = Color(0xFF60A5FA),
    tertiary = Color(0xFFFACC15),
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

// ==================== THEME ====================

private val AppTypography = androidx.compose.material3.Typography(
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun MaPenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
