package com.example.Feelia.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.Feelia.data.local.datastore.ThemeMode

private val FeeliaLightColors = lightColorScheme(
    primary = Color(0xFF7B4FBE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDD9FF),
    onPrimaryContainer = Color(0xFF2D0060),
    secondary = Color(0xFF9C7BB8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5FF),
    onSecondaryContainer = Color(0xFF1E0040),
    background = Color(0xFFFFF8FE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFF8FE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF2E8FF),
    onSurfaceVariant = Color(0xFF4A4458),
    error = Color(0xFFB3261E),
)

private val FeeliaaDarkColors = darkColorScheme(
    primary = Color(0xFFD4AAFF),
    onPrimary = Color(0xFF3D0077),
    primaryContainer = Color(0xFF5A2FA3),
    onPrimaryContainer = Color(0xFFEDD9FF),
    secondary = Color(0xFFCFB4EE),
    onSecondary = Color(0xFF341960),
    secondaryContainer = Color(0xFF4B3078),
    onSecondaryContainer = Color(0xFFEDD9FF),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0EC),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0EC),
    surfaceVariant = Color(0xFF2B2535),
    onSurfaceVariant = Color(0xFFCAC4D4),
    error = Color(0xFFF2B8B5),
)

@Composable
fun FeeliaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) FeeliaaDarkColors else FeeliaLightColors,
        content = content
    )
}