package com.example.synesthesia.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ==================== COLORS (CELESTIAL OVERHAUL) ====================

// Base Light Mode (High Contrast)
val DeepIndigo = Color(0xFF0F172A)
val RoyalBlue = Color(0xFF1E40AF)
val SoftGray = Color(0xFFF1F5F9)
val GhostWhite = Color(0xFFF8FAFC)

// Astronomy Mode (Deep Space)
val SpaceBlack = Color(0xFF030712)
val StarWhite = Color(0xFFF8FAFC)
val BrightYellow = Color(0xFFFDE047)
val NebulaPurple = Color(0xFF7C3AED)
val SupernovaOrange = Color(0xFFF97316)

// Emotion Tokens (Vibrant 3D Palette)
val JoyColor = Color(0xFFFFD700)
val MelancholyColor = Color(0xFF60A5FA)
val CalmColor = Color(0xFF34D399)
val AngerColor = Color(0xFFF87171)

enum class ThemeMode {
    NORMAL, ASTRONOMY
}

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF), // Brighter blue-white for contrast
    onPrimaryContainer = RoyalBlue,
    secondary = JoyColor,
    onSecondary = DeepIndigo,
    background = Color.White,
    onBackground = DeepIndigo,
    surface = GhostWhite,
    onSurface = DeepIndigo,
    error = Color(0xFFB91C1C),
    outline = Color(0xFF64748B)
)

private val AstronomyColorScheme = darkColorScheme(
    primary = BrightYellow,
    onPrimary = SpaceBlack,
    primaryContainer = Color(0xFF1E1B4B),
    onPrimaryContainer = StarWhite,
    secondary = NebulaPurple,
    onSecondary = StarWhite,
    background = SpaceBlack,
    onBackground = StarWhite,
    surface = Color(0xFF0F172A),
    onSurface = StarWhite,
    error = Color(0xFFF87171),
    outline = Color(0xFF334155)
)

// ==================== TYPOGRAPHY ====================

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    )
)

@Composable
fun NoteAITheme(
    themeMode: ThemeMode = ThemeMode.NORMAL,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.NORMAL -> LightColorScheme
        ThemeMode.ASTRONOMY -> AstronomyColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
