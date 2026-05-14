package com.kelazzz.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== KELAZZZ COLORS ====================
// Warna khas akademik ITERA — biru-teal + aksen warm

private val Primary = Color(0xFF1565C0)          // Biru ITERA
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFD1E4FF)
private val OnPrimaryContainer = Color(0xFF001D36)

private val Secondary = Color(0xFF00897B)        // Teal — kesan akademik
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFB2DFDB)
private val OnSecondaryContainer = Color(0xFF00201E)

private val Tertiary = Color(0xFFFF8F00)         // Amber — aksen warm
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFFFE082)
private val OnTertiaryContainer = Color(0xFF261A00)

private val Error = Color(0xFFB3261E)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFF9DEDC)
private val OnErrorContainer = Color(0xFF410E0B)

private val BackgroundLight = Color(0xFFFAFCFF)
private val OnBackgroundLight = Color(0xFF1A1C1E)
private val SurfaceLight = Color(0xFFFAFCFF)
private val OnSurfaceLight = Color(0xFF1A1C1E)
private val SurfaceVariantLight = Color(0xFFDFE3EB)
private val OnSurfaceVariantLight = Color(0xFF43474E)

private val BackgroundDark = Color(0xFF1A1C1E)
private val OnBackgroundDark = Color(0xFFE2E2E6)
private val SurfaceDark = Color(0xFF1A1C1E)
private val OnSurfaceDark = Color(0xFFE2E2E6)
private val SurfaceVariantDark = Color(0xFF43474E)
private val OnSurfaceVariantDark = Color(0xFFC3C6CF)

private val OutlineLight = Color(0xFF73777F)
private val OutlineDark = Color(0xFF8D9199)

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
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF00504B),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFFFCA28),
    onTertiary = Color(0xFF3E2E00),
    tertiaryContainer = Color(0xFF5A4300),
    onTertiaryContainer = Color(0xFFFFE082),
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
fun KelazZzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
