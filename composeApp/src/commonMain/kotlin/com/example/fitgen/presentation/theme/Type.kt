package com.example.fitgen.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import noteai.composeapp.generated.resources.Res
import noteai.composeapp.generated.resources.poppins_bold
import noteai.composeapp.generated.resources.poppins_medium
import noteai.composeapp.generated.resources.poppins_regular
import noteai.composeapp.generated.resources.poppins_semibold
import org.jetbrains.compose.resources.Font

// ==================== POPPINS FONT FAMILY ====================

@Composable
fun getPoppinsFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.poppins_regular,  FontWeight.Normal),
        Font(Res.font.poppins_medium,   FontWeight.Medium),
        Font(Res.font.poppins_semibold, FontWeight.SemiBold),
        Font(Res.font.poppins_bold,     FontWeight.Bold)
    )
}

// ==================== TYPOGRAPHY ====================

@Composable
fun getTypography(): Typography {
    val poppins = getPoppinsFontFamily()

    return Typography(
        displayLarge = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Normal,
            fontSize     = 57.sp,
            lineHeight   = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Normal,
            fontSize     = 45.sp,
            lineHeight   = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Normal,
            fontSize     = 36.sp,
            lineHeight   = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.SemiBold,
            fontSize     = 32.sp,
            lineHeight   = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.SemiBold,
            fontSize     = 28.sp,
            lineHeight   = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.SemiBold,
            fontSize     = 24.sp,
            lineHeight   = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Bold,
            fontSize     = 22.sp,
            lineHeight   = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.SemiBold,
            fontSize     = 16.sp,
            lineHeight   = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Medium,
            fontSize     = 14.sp,
            lineHeight   = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Normal,
            fontSize     = 16.sp,
            lineHeight   = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Normal,
            fontSize     = 14.sp,
            lineHeight   = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Normal,
            fontSize     = 12.sp,
            lineHeight   = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Medium,
            fontSize     = 14.sp,
            lineHeight   = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Medium,
            fontSize     = 12.sp,
            lineHeight   = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily   = poppins,
            fontWeight   = FontWeight.Medium,
            fontSize     = 11.sp,
            lineHeight   = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}
