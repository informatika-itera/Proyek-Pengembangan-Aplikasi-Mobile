package com.example.neurodeck.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import neurodeck.composeapp.generated.resources.Res
import neurodeck.composeapp.generated.resources.bricolage_grotesque_bold
import neurodeck.composeapp.generated.resources.bricolage_grotesque_medium
import neurodeck.composeapp.generated.resources.bricolage_grotesque_regular
import neurodeck.composeapp.generated.resources.bricolage_grotesque_semibold
import org.jetbrains.compose.resources.Font

@Composable
private fun bricolageFontFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.bricolage_grotesque_regular, weight = FontWeight.Normal),
    Font(resource = Res.font.bricolage_grotesque_medium, weight = FontWeight.Medium),
    Font(resource = Res.font.bricolage_grotesque_semibold, weight = FontWeight.SemiBold),
    Font(resource = Res.font.bricolage_grotesque_bold, weight = FontWeight.Bold),
)
@Composable
fun neuroDeckTypography(): Typography {
    val f = bricolageFontFamily()

    return Typography(
        // DISPLAY
        displayLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
        displayMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 45.sp, lineHeight = 52.sp),
        displaySmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),

        // HEADLINE
        headlineLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
        headlineMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
        headlineSmall = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),

        // TITLE
        titleLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
        titleMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
        titleSmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),

        // BODY
        bodyLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
        bodyMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
        bodySmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),

        // LABEL
        labelLarge = TextStyle(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
        labelMedium = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
        labelSmall = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    )
}
