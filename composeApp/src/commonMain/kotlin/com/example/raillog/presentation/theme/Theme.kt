package com.example.raillog.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Warna Utama dari DESIGN (3).md
private val PrimaryNavy = Color(0xFF00236F)
private val PrimaryContainer = Color(0xFF1E3A8A)
private val SuccessEmerald = Color(0xFF006C49)
private val SurfaceSlate = Color(0xFFF7F9FB)
private val OnSurface = Color(0xFF191C1E)
private val ErrorRed = Color(0xFFBA1A1A)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Color(0xFF90A8FF),
    secondary = SuccessEmerald,
    onSecondary = Color.White,
    background = SurfaceSlate,
    onBackground = OnSurface,
    surface = SurfaceSlate,
    onSurface = OnSurface,
    surfaceVariant = Color(0xFFE0E3E5),
    onSurfaceVariant = Color(0xFF444651),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF757682)
)

// Gunakan palet gelap yang diredam untuk lingkungan industri
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB6C4FF),
    onPrimary = Color(0xFF00164E),
    primaryContainer = Color(0xFF1E3A8A),
    background = Color(0xFF191C1E),
    surface = Color(0xFF191C1E),
    onSurface = Color(0xFFEFF1F3)
)

@Composable
fun RailLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Terapkan Radius sesuai DESIGN (3).md: Standard 4px, Cards 8px
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(12.dp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RailLogTypography,
        shapes = shapes,
        content = content
    )
}