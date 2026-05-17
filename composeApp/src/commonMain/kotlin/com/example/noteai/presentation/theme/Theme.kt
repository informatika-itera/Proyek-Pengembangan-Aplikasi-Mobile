package com.example.noteai.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS (FinTrack Custom Branding) ====================

// Light Mode Colors (Emerald Green & Light Slate)
private val Primary = Color(0xFF00B48A)           // Hijau Uang / Saldo Utama
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFD1FAE5)  // Hijau Mint Muda
private val OnPrimaryContainer = Color(0xFF064E3B)

private val Secondary = Color(0xFF1E293B)         // Dark Slate
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFE2E8F0)
private val OnSecondaryContainer = Color(0xFF0F172A)

private val Tertiary = Color(0xFF0EA5E9)          // Sky Blue (Aksen Pemasukan)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFE0F2FE)
private val OnTertiaryContainer = Color(0xFF0369A1)

private val Error = Color(0xFFEF4444)             // Merah Pengeluaran / Error
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFEE2E2)
private val OnErrorContainer = Color(0xFF7F1D1D)

private val BackgroundLight = Color(0xFFF8FAFC)   // Background Off-White Bersih
private val OnBackgroundLight = Color(0xFF0F172A)
private val SurfaceLight = Color(0xFFFFFFFF)      // Putih Bersih untuk Card UI
private val OnSurfaceLight = Color(0xFF0F172A)
private val SurfaceVariantLight = Color(0xFFF1F5F9)
private val OnSurfaceVariantLight = Color(0xFF475569)

// Dark Mode Colors (Deep Slate Night)
private val BackgroundDark = Color(0xFF0F172A)    // Deep Dark Slate
private val OnBackgroundDark = Color(0xFFF8FAFC)
private val SurfaceDark = Color(0xFF1E293B)       // Card Dark Slate
private val OnSurfaceDark = Color(0xFFF8FAFC)
private val SurfaceVariantDark = Color(0xFF334155)
private val OnSurfaceVariantDark = Color(0xFF94A3B8)

private val OutlineLight = Color(0xFF94A3B8)
private val OutlineDark = Color(0xFF475569)

// ==================== COLOR SCHEMES ====================

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF34D399),                 // Mint Green untuk Dark Mode
    onPrimary = Color(0xFF064E3B),
    primaryContainer = Color(0xFF047857),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF1E293B),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF7DD3FC),
    onTertiary = Color(0xFF0369A1),
    tertiaryContainer = Color(0xFF0369A1),
    onTertiaryContainer = Color(0xFFE0F2FE),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF7F1D1D),
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFEE2E2),
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

// ==================== THEME ====================

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}