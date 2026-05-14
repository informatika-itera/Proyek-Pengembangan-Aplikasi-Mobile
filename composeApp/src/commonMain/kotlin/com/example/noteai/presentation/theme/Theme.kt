package com.example.noteai.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== VULNLOG COLORS ====================
// Cybersecurity-themed palette: dark backgrounds + neon green accents

// Neon green (terminal-style)
private val NeonGreen = Color(0xFF00FF41)
private val DarkGreen = Color(0xFF00C832)
private val DeepGreen = Color(0xFF003B12)

// Dark backgrounds
private val DarkBg = Color(0xFF0D1117)
private val DarkSurface = Color(0xFF161B22)
private val DarkSurfaceVariant = Color(0xFF21262D)
private val DarkBorder = Color(0xFF30363D)

// Severity colors (bug bounty style)
private val Critical = Color(0xFFFF4444)
private val High = Color(0xFFFF8C00)
private val Medium = Color(0xFFFFD700)
private val Low = Color(0xFF58A6FF)

// Text
private val TextPrimary = Color(0xFFE6EDF3)
private val TextSecondary = Color(0xFF8B949E)
private val TextMuted = Color(0xFF484F58)

// ==================== COLOR SCHEMES ====================

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DarkBg,
    primaryContainer = DeepGreen,
    onPrimaryContainer = NeonGreen,
    secondary = Low,
    onSecondary = DarkBg,
    secondaryContainer = Color(0xFF0D2240),
    onSecondaryContainer = Low,
    tertiary = Medium,
    onTertiary = DarkBg,
    tertiaryContainer = Color(0xFF3D3200),
    onTertiaryContainer = Medium,
    error = Critical,
    onError = Color(0xFF1A0000),
    errorContainer = Color(0xFF3D0000),
    onErrorContainer = Color(0xFFFFB4AB),
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

// Light mode tetap ada tapi juga gelap-ish (cybersecurity feel)
private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F5C8),
    onPrimaryContainer = Color(0xFF002110),
    secondary = Color(0xFF3B6EA5),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1E4FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = Color(0xFF6D5E0F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF8E287),
    onTertiaryContainer = Color(0xFF221B00),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF6FFF6),
    onBackground = Color(0xFF1A1C1A),
    surface = Color(0xFFF6FFF6),
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFDDE5DB),
    onSurfaceVariant = Color(0xFF414941),
    outline = Color(0xFF717971)
)

// ==================== THEME ====================

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // VulnLog defaults to dark theme for that hacker aesthetic
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
