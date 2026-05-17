package com.example.raillog.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import raillognusantara.composeapp.generated.resources.*

// Definisi Font Family (Pastikan file font sudah ada di composeResources/font)
@Composable
fun getGeistFontFamily() = FontFamily(
    Font(Res.font.Geist_Regular, FontWeight.Normal),
    Font(Res.font.Geist_Medium, FontWeight.Medium),
    Font(Res.font.Geist_SemiBold, FontWeight.SemiBold),
    Font(Res.font.Geist_Bold, FontWeight.Bold)
)

@Composable
fun getGeistMonoFontFamily() = FontFamily(
    Font(Res.font.GeistMono_Medium, FontWeight.Medium)
)

val RailLogTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

// Style khusus untuk Technical ID (PRT-XXXX-000) sesuai DESIGN (3).md
@Composable
fun technicalIdStyle() = TextStyle(
    fontFamily = getGeistMonoFontFamily(),
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.02.sp
)