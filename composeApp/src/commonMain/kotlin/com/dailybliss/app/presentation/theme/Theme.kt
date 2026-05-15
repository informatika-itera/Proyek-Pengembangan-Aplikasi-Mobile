package com.dailybliss.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SageGreenColorScheme = lightColorScheme(
    primary = Color(0xFF6B8E23), // Sage Green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3EBD6), // Light Sage
    onPrimaryContainer = Color(0xFF1B2B00),
    secondary = Color(0xFF5D624E),
    onSecondary = Color.White,
    background = Color(0xFFFAFAFA), // Soft Off-White
    onBackground = Color(0xFF1A1C18),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1A1C18),
    surfaceVariant = Color(0xFFE1E4D5),
    onSurfaceVariant = Color(0xFF44483D),
    outline = Color(0xFF75796C)
)

private val OceanBlueColorScheme = lightColorScheme(
    primary = Color(0xFF006494),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCAE6FF),
    onPrimaryContainer = Color(0xFF001E30),
    secondary = Color(0xFF50606E),
    onSecondary = Color.White,
    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF191C1E),
    surface = Color(0xFFF8F9FF),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDDE3EA),
    onSurfaceVariant = Color(0xFF41484D),
    outline = Color(0xFF71787E)
)

private val RosePinkColorScheme = lightColorScheme(
    primary = Color(0xFF9C4146),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD9),
    onPrimaryContainer = Color(0xFF41000A),
    secondary = Color(0xFF775656),
    onSecondary = Color.White,
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A1A),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A1A),
    surfaceVariant = Color(0xFFF4DDDD),
    onSurfaceVariant = Color(0xFF524343),
    outline = Color(0xFF857373)
)

private val LavenderColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EB),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

private val MonochromeColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1E1E1),
    onPrimaryContainer = Color(0xFF1B1B1B),
    secondary = Color(0xFF5E5E5E),
    onSecondary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1B1B1B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1B),
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF444444),
    outline = Color(0xFF767676)
)

@Composable
fun DailyBlissTheme(
    darkTheme: Boolean = false,
    themeName: String = "Sage Green",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeName) {
        "Ocean Blue" -> OceanBlueColorScheme
        "Rose Pink" -> RosePinkColorScheme
        "Lavender" -> LavenderColorScheme
        "Monochrome" -> MonochromeColorScheme
        else -> SageGreenColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
