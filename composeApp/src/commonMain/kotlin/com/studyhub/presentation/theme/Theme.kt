package com.studyhub.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ==================== COLORS ====================

val AppBlack = Color(0xFF000000)
val AppWhite = Color(0xFFFFFFFF)
val AppGray = Color(0xFFF5F5F5)
val AppTextGray = Color(0xFF757575)

val PastelPurple = Color(0xFFEADDFF)
val PastelGreen = Color(0xFFE2F2E9)
val PastelOrange = Color(0xFFFFEADA)
val PastelBlue = Color(0xFFD8EEFF)

private val LightColorScheme = lightColorScheme(
    primary = AppBlack,
    onPrimary = AppWhite,
    primaryContainer = AppGray,
    onPrimaryContainer = AppBlack,
    secondary = PastelPurple,
    onSecondary = AppBlack,
    background = AppWhite,
    surface = AppWhite,
    onBackground = AppBlack,
    onSurface = AppBlack
)

private val DarkColorScheme = darkColorScheme(
    primary = AppWhite,
    onPrimary = AppBlack,
    primaryContainer = Color(0xFF2C2C2C),
    onPrimaryContainer = AppWhite,
    secondary = PastelPurple,
    onSecondary = AppBlack,
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onBackground = AppWhite,
    onSurface = AppWhite
)

val StudyHubTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun StudyHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = StudyHubTypography,
        content = content
    )
}
