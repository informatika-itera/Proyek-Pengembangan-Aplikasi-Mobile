package com.example.tabungin.presentation.theme

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



private val AestheticPrimary = Color(0xFF8B5CF6)
private val AestheticOnPrimary = Color(0xFFFFFFFF)
private val AestheticPrimaryContainer = Color(0xFFF3E8FF)
private val AestheticOnPrimaryContainer = Color(0xFF2E1065)


private val AestheticSecondary = Color(0xFFEC4899)
private val AestheticOnSecondary = Color(0xFFFFFFFF)
private val AestheticSecondaryContainer = Color(0xFFFCE7F3)
private val AestheticOnSecondaryContainer = Color(0xFF500724)


private val AestheticTertiary = Color(0xFF14B8A6)
private val AestheticOnTertiary = Color(0xFFFFFFFF)
private val AestheticTertiaryContainer = Color(0xFFCCFBF1)
private val AestheticOnTertiaryContainer = Color(0xFF0F3D38)


private val AestheticError = Color(0xFFEF4444)
private val AestheticOnError = Color(0xFFFFFFFF)
private val AestheticErrorContainer = Color(0xFFFEE2E2)
private val AestheticOnErrorContainer = Color(0xFF7F1D1D)

// Surface & Background - Soft Cream/White
private val AestheticBackgroundLight = Color(0xFFFAF5FF)
private val AestheticOnBackgroundLight = Color(0xFF1E1B4B)
private val AestheticSurfaceLight = Color(0xFFFFFFFF)
private val AestheticOnSurfaceLight = Color(0xFF1E1B4B)
private val AestheticSurfaceVariantLight = Color(0xFFF3F4F6)
private val AestheticOnSurfaceVariantLight = Color(0xFF4B5563)
private val AestheticOutlineLight = Color(0xFFCBD5E1)



private val AestheticPrimaryDark = Color(0xFFA78BFA)
private val AestheticOnPrimaryDark = Color(0xFF1E1B4B)
private val AestheticPrimaryContainerDark = Color(0xFF4C1D95)
private val AestheticOnPrimaryContainerDark = Color(0xFFF3E8FF)


private val AestheticSecondaryDark = Color(0xFFF472B6)
private val AestheticOnSecondaryDark = Color(0xFF500724)
private val AestheticSecondaryContainerDark = Color(0xFF831843)
private val AestheticOnSecondaryContainerDark = Color(0xFFFCE7F3)


private val AestheticTertiaryDark = Color(0xFF5EEAD4)
private val AestheticOnTertiaryDark = Color(0xFF0F3D38)
private val AestheticTertiaryContainerDark = Color(0xFF134E4A)
private val AestheticOnTertiaryContainerDark = Color(0xFFCCFBF1)


private val AestheticErrorDark = Color(0xFFFCA5A5)
private val AestheticOnErrorDark = Color(0xFF7F1D1D)
private val AestheticErrorContainerDark = Color(0xFF991B1B)
private val AestheticOnErrorContainerDark = Color(0xFFFEE2E2)


private val AestheticBackgroundDark = Color(0xFF0F0A1F)
private val AestheticOnBackgroundDark = Color(0xFFF3F4F6)
private val AestheticSurfaceDark = Color(0xFF1A1330)
private val AestheticOnSurfaceDark = Color(0xFFF3F4F6)
private val AestheticSurfaceVariantDark = Color(0xFF312E6B)
private val AestheticOnSurfaceVariantDark = Color(0xFFC4B5FD)
private val AestheticOutlineDark = Color(0xFF6366F1)




internal val AestheticTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)


private val AestheticLightColorScheme = lightColorScheme(
    primary = AestheticPrimary,
    onPrimary = AestheticOnPrimary,
    primaryContainer = AestheticPrimaryContainer,
    onPrimaryContainer = AestheticOnPrimaryContainer,
    secondary = AestheticSecondary,
    onSecondary = AestheticOnSecondary,
    secondaryContainer = AestheticSecondaryContainer,
    onSecondaryContainer = AestheticOnSecondaryContainer,
    tertiary = AestheticTertiary,
    onTertiary = AestheticOnTertiary,
    tertiaryContainer = AestheticTertiaryContainer,
    onTertiaryContainer = AestheticOnTertiaryContainer,
    error = AestheticError,
    onError = AestheticOnError,
    errorContainer = AestheticErrorContainer,
    onErrorContainer = AestheticOnErrorContainer,
    background = AestheticBackgroundLight,
    onBackground = AestheticOnBackgroundLight,
    surface = AestheticSurfaceLight,
    onSurface = AestheticOnSurfaceLight,
    surfaceVariant = AestheticSurfaceVariantLight,
    onSurfaceVariant = AestheticOnSurfaceVariantLight,
    outline = AestheticOutlineLight
)

private val AestheticDarkColorScheme = darkColorScheme(
    primary = AestheticPrimaryDark,
    onPrimary = AestheticOnPrimaryDark,
    primaryContainer = AestheticPrimaryContainerDark,
    onPrimaryContainer = AestheticOnPrimaryContainerDark,
    secondary = AestheticSecondaryDark,
    onSecondary = AestheticOnSecondaryDark,
    secondaryContainer = AestheticSecondaryContainerDark,
    onSecondaryContainer = AestheticOnSecondaryContainerDark,
    tertiary = AestheticTertiaryDark,
    onTertiary = AestheticOnTertiaryDark,
    tertiaryContainer = AestheticTertiaryContainerDark,
    onTertiaryContainer = AestheticOnTertiaryContainerDark,
    error = AestheticErrorDark,
    onError = AestheticOnErrorDark,
    errorContainer = AestheticErrorContainerDark,
    onErrorContainer = AestheticOnErrorContainerDark,
    background = AestheticBackgroundDark,
    onBackground = AestheticOnBackgroundDark,
    surface = AestheticSurfaceDark,
    onSurface = AestheticOnSurfaceDark,
    surfaceVariant = AestheticSurfaceVariantDark,
    onSurfaceVariant = AestheticOnSurfaceVariantDark,
    outline = AestheticOutlineDark
)


@Composable
fun TabungInTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AestheticDarkColorScheme else AestheticLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AestheticTypography,
        content = content
    )
}