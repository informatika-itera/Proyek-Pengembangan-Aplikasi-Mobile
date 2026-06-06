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
private val PrimaryLight = Color(0xFF40513B)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFC1D8B7)
private val OnPrimaryContainerLight = Color(0xFF0F1A0B)

// Secondary
private val SecondaryLight = Color(0xFF628141)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFE2F0CD)
private val OnSecondaryContainerLight = Color(0xFF1D2E0B)

// Tertiary 
private val TertiaryLight = Color(0xFF79745A)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFEAE3C0)
private val OnTertiaryContainerLight = Color(0xFF282411)

// Background & Surface
private val BackgroundLight = Color(0xFFF9FAEB)
private val OnBackgroundLight = Color(0xFF191D17)
private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF191D17)
private val SurfaceVariantLight = Color(0xFFE0E4D6)
private val OnSurfaceVariantLight = Color(0xFF44483E)
private val ErrorLight = Color(0xFFBA1A1A)
private val OnErrorLight = Color(0xFFFFFFFF)

// ==================== DARK MODE ====================

private val PrimaryDark = Color(0xFFA5BC9C)
private val OnPrimaryDark = Color(0xFF12240F)
private val PrimaryContainerDark = Color(0xFF293A24)
private val OnPrimaryContainerDark = Color(0xFFC1D8B7)

private val SecondaryDark = Color(0xFFC6D4A9)
private val OnSecondaryDark = Color(0xFF334F16)
private val SecondaryContainerDark = Color(0xFF4A6828)
private val OnSecondaryContainerDark = Color(0xFFE2F0CD)

private val TertiaryDark = Color(0xFFCDC6A5)
private val OnTertiaryDark = Color(0xFF3B351E)
private val TertiaryContainerDark = Color(0xFF514C33)
private val OnTertiaryContainerDark = Color(0xFFEAE3C0)

private val BackgroundDark = Color(0xFF11140F)
private val OnBackgroundDark = Color(0xFFE2E3D8)
private val SurfaceDark = Color(0xFF1E201B)
private val OnSurfaceDark = Color(0xFFE2E3D8)
private val SurfaceVariantDark = Color(0xFF44483E)
private val OnSurfaceVariantDark = Color(0xFFC4C8BA)
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