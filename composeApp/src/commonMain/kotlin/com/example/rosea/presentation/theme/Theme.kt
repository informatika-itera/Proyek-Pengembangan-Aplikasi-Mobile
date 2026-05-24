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

// ==================== MODERN SOFT PINK PALETTE ====================

// Light Colors - Soft, Airy, Professional
private val RosePrimary = Color(0xFFE91E63) // Pink yang berwibawa
private val RoseOnPrimary = Color(0xFFFFFFFF)
private val RosePrimaryContainer = Color(0xFFFCE4EC) // Sangat lembut
private val RoseOnPrimaryContainer = Color(0xFF880E4F)

private val RoseSecondary = Color(0xFF9C27B0) // Ungu lembut sebagai aksen
private val RoseBackground = Color(0xFFFFFAFB) // Putih dengan semburat pink
private val RoseSurface = Color(0xFFFFFFFF)
private val RoseOutline = Color(0xFFE0B0B6)

// Dark Colors - Elegant Dark Rose
private val RosePrimaryDark = Color(0xFFF48FB1)
private val RoseOnPrimaryDark = Color(0xFF4A0021)
private val RoseBackgroundDark = Color(0xFF1A1114)
private val RoseSurfaceDark = Color(0xFF251A1D)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = RosePrimary,
    onPrimary = RoseOnPrimary,
    primaryContainer = RosePrimaryContainer,
    onPrimaryContainer = RoseOnPrimaryContainer,
    secondary = RoseSecondary,
    background = RoseBackground,
    surface = RoseSurface,
    onSurface = Color(0xFF352F30),
    outline = RoseOutline,
    surfaceVariant = Color(0xFFF5E9EB)
)

private val DarkColorScheme = darkColorScheme(
    primary = RosePrimaryDark,
    onPrimary = RoseOnPrimaryDark,
    background = RoseBackgroundDark,
    surface = RoseSurfaceDark,
    onSurface = Color(0xFFECE0E1)
)

// ==================== TYPOGRAPHY ====================

private val RoseTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = RosePrimary
    )
)

private val RoseShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp), // Lebih bulat, lebih modern
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
