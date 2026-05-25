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

// ==================== WARNA BIRU & OREN ====================

// Primary: Biru (Fokus, Produktivitas)
private val PrimaryLight = Color(0xFF2563EB)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFDBEAFE)
private val OnPrimaryContainerLight = Color(0xFF1E3A8A)

// Secondary: Oren (Aksen, Semangat, Tenggat Waktu)
private val SecondaryLight = Color(0xFFEA580C)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFFFEDD5)
private val OnSecondaryContainerLight = Color(0xFF7C2D12)

// Tertiary: Kuning/Amber (Peringatan/Pelengkap)
private val TertiaryLight = Color(0xFFD97706)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFFEF3C7)
private val OnTertiaryContainerLight = Color(0xFF92400E)

// Background & Surface: Slate Terang (Bersih & Elegan)
private val BackgroundLight = Color(0xFFF8FAFC)
private val OnBackgroundLight = Color(0xFF0F172A)
private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF0F172A)
private val SurfaceVariantLight = Color(0xFFF1F5F9)
private val OnSurfaceVariantLight = Color(0xFF475569)
private val ErrorLight = Color(0xFFDC2626)
private val OnErrorLight = Color(0xFFFFFFFF)

// ==================== DARK MODE ====================

private val PrimaryDark = Color(0xFF60A5FA)
private val OnPrimaryDark = Color(0xFF1E3A8A)
private val PrimaryContainerDark = Color(0xFF1D4ED8)
private val OnPrimaryContainerDark = Color(0xFFDBEAFE)

private val SecondaryDark = Color(0xFFFB923C)
private val OnSecondaryDark = Color(0xFF431407)
private val SecondaryContainerDark = Color(0xFF9A3412)
private val OnSecondaryContainerDark = Color(0xFFFFEDD5)

private val TertiaryDark = Color(0xFFFBBF24)
private val OnTertiaryDark = Color(0xFF451A03)
private val TertiaryContainerDark = Color(0xFFB45309)
private val OnTertiaryContainerDark = Color(0xFFFEF3C7)

private val BackgroundDark = Color(0xFF0F172A)
private val OnBackgroundDark = Color(0xFFF8FAFC)
private val SurfaceDark = Color(0xFF1E293B)
private val OnSurfaceDark = Color(0xFFF8FAFC)
private val SurfaceVariantDark = Color(0xFF334155)
private val OnSurfaceVariantDark = Color(0xFFCBD5E1)
private val ErrorDark = Color(0xFFEF4444)
private val OnErrorDark = Color(0xFF450A0A)

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
// Menambahkan sudut membulat untuk tampilan modern
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
        shapes = ModernShapes, // Menerapkan shape modern
        content = content
    )
}