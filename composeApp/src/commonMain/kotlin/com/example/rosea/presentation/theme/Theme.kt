package com.example.rosea.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================== BRIGHT & SOFT ROSE PALETTE ====================

// Colors that are bright ("terang") but remain soft ("soft") and clean.
private val BrightRose = Color(0xFFFF85A1)     // Main Primary: Bright, cheerful pink
private val SoftRose = Color(0xFFFFB3C6)       // Secondary: Lighter pink
private val PaleRose = Color(0xFFFFE5EC)       // Tertiary: Very soft background-ish pink
private val White = Color(0xFFFFFFFF)
private val OffWhiteRose = Color(0xFFFFF9FA)   // Background: Clean white with a hint of rose

// Text Colors (Avoid pure Black)
private val DeepRoseText = Color(0xFF5F4349)   // Dark Muted Rose for main text
private val MediumRoseText = Color(0xFF8E6E74) // Lighter version for secondary text

private val LightColorScheme = lightColorScheme(
    primary = BrightRose,
    onPrimary = White,
    primaryContainer = PaleRose,
    onPrimaryContainer = BrightRose,
    secondary = SoftRose,
    onSecondary = White,
    background = OffWhiteRose,
    onBackground = DeepRoseText,
    surface = White,
    onSurface = DeepRoseText,
    outline = BrightRose.copy(alpha = 0.2f),
    surfaceVariant = PaleRose.copy(alpha = 0.5f),
    onSurfaceVariant = DeepRoseText
)

private val DarkColorScheme = darkColorScheme(
    primary = BrightRose,
    onPrimary = Color.Black,
    background = Color(0xFF2D1F21), // Deep Warm Brownish-Rose
    onBackground = Color(0xFFFFE5EC),
    surface = Color(0xFF3D2C2E),
    onSurface = Color(0xFFFFE5EC),
    primaryContainer = Color(0xFF880E4F),
    onPrimaryContainer = PaleRose
)

private val RoseTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        letterSpacing = (-0.5).sp,
        color = DeepRoseText
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = DeepRoseText
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = DeepRoseText
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = MediumRoseText
    )
)

private val RoseShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun RoseaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = RoseTypography,
        shapes = RoseShapes,
        content = content
    )
}
