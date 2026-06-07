package com.example.edumate.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ==================== PALET WARNA PROFESIONAL ====================

// Primary
// Primary
private val PrimaryLight = Color(0xFF00D9C0)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFB8FFF6)
private val OnPrimaryContainerLight = Color(0xFF00201D)

// Secondary (biru)
private val SecondaryLight = Color(0xFF2563EB)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFDCE7FF)
private val OnSecondaryContainerLight = Color(0xFF001A43)

// Tertiary
private val TertiaryLight = Color(0xFF4F46E5)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFE3E0FF)
private val OnTertiaryContainerLight = Color(0xFF12005E)

// Background & Surface
private val BackgroundLight = Color(0xFFF8FCFC)
private val OnBackgroundLight = Color(0xFF101414)
private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF101414)
private val SurfaceVariantLight = Color(0xFFDDE5E4)
private val OnSurfaceVariantLight = Color(0xFF414948)

private val ErrorLight = Color(0xFFBA1A1A)
private val OnErrorLight = Color(0xFFFFFFFF)

// ==================== DARK MODE ====================
private val PrimaryDark = Color(0xFF6FFFEF)
private val OnPrimaryDark = Color(0xFF003732)
private val PrimaryContainerDark = Color(0xFF005048)
private val OnPrimaryContainerDark = Color(0xFFB8FFF6)

private val SecondaryDark = Color(0xFFAEC6FF)
private val OnSecondaryDark = Color(0xFF002E74)
private val SecondaryContainerDark = Color(0xFF0044A8)
private val OnSecondaryContainerDark = Color(0xFFDCE7FF)

private val TertiaryDark = Color(0xFFC4C0FF)
private val OnTertiaryDark = Color(0xFF241D7C)
private val TertiaryContainerDark = Color(0xFF3A32A7)
private val OnTertiaryContainerDark = Color(0xFFE3E0FF)

private val BackgroundDark = Color(0xFF0F1414)
private val OnBackgroundDark = Color(0xFFE0E4E3)
private val SurfaceDark = Color(0xFF161B1B)
private val OnSurfaceDark = Color(0xFFE0E4E3)
private val SurfaceVariantDark = Color(0xFF414948)
private val OnSurfaceVariantDark = Color(0xFFC1C9C8)
private val ErrorDark = Color(0xFFFFB4AB)
private val OnErrorDark = Color(0xFF690005)

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
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorLight,
    onError = OnErrorLight
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
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark
)

// ==================== SHAPES ====================
val ModernShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = ModernShapes,
        content = content
    )
}