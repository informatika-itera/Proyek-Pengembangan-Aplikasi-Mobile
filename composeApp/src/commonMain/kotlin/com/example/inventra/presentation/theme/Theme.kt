package com.example.inventra.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== COLORS ====================

// InventRa Custom Color Palette based on PRD
private val Primary        = Color(0xFFCBCF1A)   // Kuning HMIF
private val OnPrimary      = Color(0xFF313300)
private val PrimaryContainer    = Color(0xFFEEF284)
private val OnPrimaryContainer  = Color(0xFF1F2100)

private val Secondary      = Color(0xFF17579F)   // Biru HMIF
private val OnSecondary    = Color(0xFFFFFFFF)
private val SecondaryContainer  = Color(0xFFD2E4FF)
private val OnSecondaryContainer = Color(0xFF001B3E)

private val Tertiary       = Color(0xFF4D5B37)   // Hijau HMIF
private val OnTertiary     = Color(0xFFFFFFFF)
private val TertiaryContainer   = Color(0xFFCBD9AA)
private val OnTertiaryContainer = Color(0xFF111E00)

private val Error = Color(0xFFB3261E)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFF9DEDC)
private val OnErrorContainer = Color(0xFF410E0B)

private val BackgroundLight = Color(0xFFFFFBFE)
private val OnBackgroundLight = Color(0xFF1C1B1F)
private val SurfaceLight = Color(0xFFFFFBFE)
private val OnSurfaceLight = Color(0xFF1C1B1F)
private val SurfaceVariantLight = Color(0xFFE7E0EC)
private val OnSurfaceVariantLight = Color(0xFF49454F)

private val BackgroundDark = Color(0xFF1C1B1F)
private val OnBackgroundDark = Color(0xFFE6E1E5)
private val SurfaceDark = Color(0xFF1C1B1F)
private val OnSurfaceDark = Color(0xFFE6E1E5)
private val SurfaceVariantDark = Color(0xFF49454F)
private val OnSurfaceVariantDark = Color(0xFFCAC4D0)

private val OutlineLight = Color(0xFF79747E)
private val OutlineDark = Color(0xFF938F99)

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
    primary = Color(0xFFEEF284),
    onPrimary = Color(0xFF313300),
    primaryContainer = Color(0xFF484A00),
    onPrimaryContainer = Color(0xFFEEF284),
    secondary = Color(0xFFADC6FF),
    onSecondary = Color(0xFF002E69),
    secondaryContainer = Color(0xFF004494),
    onSecondaryContainer = Color(0xFFD2E4FF),
    tertiary = Color(0xFFB0CD81),
    onTertiary = Color(0xFF1F3700),
    tertiaryContainer = Color(0xFF354E0B),
    onTertiaryContainer = Color(0xFFB0CD81),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
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
fun InventRaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
