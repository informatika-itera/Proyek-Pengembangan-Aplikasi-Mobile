package com.example.todomaster.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS ====================

val PrimaryBlue = Color(0xFF4A90E2)
private val OnPrimaryBlue = Color(0xFFFFFFFF)
private val PrimaryContainerBlue = Color(0xFFD6E4F0)
private val OnPrimaryContainerBlue = Color(0xFF1C3A5A)

val SecondarySage = Color(0xFF8FBC8F)
private val OnSecondarySage = Color(0xFFFFFFFF)
private val SecondaryContainerSage = Color(0xFFE2F0E2)
private val OnSecondaryContainerSage = Color(0xFF2E4F2E)

private val ErrorColor = Color(0xFFE57373)
private val OnErrorColor = Color(0xFFFFFFFF)

private val BackgroundLight = Color(0xFFF8F9FA)
private val SurfaceLight = Color(0xFFFFFFFF)
private val OnBackgroundLight = Color(0xFF2C3E50)
private val SurfaceVariantLight = Color(0xFFE9ECEF)
private val OnSurfaceVariantLight = Color(0xFF495057)
private val OutlineLight = Color(0xFFCED4DA)

private val BackgroundDark = Color(0xFF121212)
private val SurfaceDark = Color(0xFF1E1E1E)
private val OnBackgroundDark = Color(0xFFE0E0E0)
private val SurfaceVariantDark = Color(0xFF2D2D2D)
private val OnSurfaceVariantDark = Color(0xFFB0B0B0)
private val OutlineDark = Color(0xFF5C5C5C)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = OnPrimaryContainerBlue,
    secondary = SecondarySage,
    onSecondary = OnSecondarySage,
    secondaryContainer = SecondaryContainerSage,
    onSecondaryContainer = OnSecondaryContainerSage,
    error = ErrorColor,
    onError = OnErrorColor,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6EABEE), // Biru lebih terang untuk Dark Mode
    onPrimary = Color(0xFF003060),
    primaryContainer = Color(0xFF2A5C8E),
    onPrimaryContainer = Color(0xFFD6E4F0),
    secondary = Color(0xFFA5D6A5), // Sage lebih terang untuk Dark Mode
    onSecondary = Color(0xFF1B3B1B),
    secondaryContainer = Color(0xFF3E6B3E),
    onSecondaryContainer = Color(0xFFE2F0E2),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFF4A0000),
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

// ==================== THEME ====================

@Composable
fun TodoMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// ==================== EISENHOWER MATRIX COLORS ====================
val ColorDoFirst = Color(0xFFFF6B6B)
val ColorSchedule = PrimaryBlue
val ColorDelegate = Color(0xFFF3A683)
val ColorDontDo = Color(0xFFA5B1C2)