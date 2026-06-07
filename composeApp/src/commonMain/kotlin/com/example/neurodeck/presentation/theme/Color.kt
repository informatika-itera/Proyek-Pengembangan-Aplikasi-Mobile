package com.example.neurodeck.presentation.theme

import androidx.compose.ui.graphics.Color

// BRAND PALETTE
object Brand {
    val Violet500 = Color(0xFF8B5CF6)
    val Violet600 = Color(0xFF7C3AED)
    val Violet300 = Color(0xFFC4B5FD)
    val Violet200 = Color(0xFFCECBF6)
    val Violet100 = Color(0xFFEDE9FE)
    val Violet900 = Color(0xFF4C1D95)

    val Indigo500 = Color(0xFF6366F1)
    val Indigo600 = Color(0xFF4F46E5)
    val Indigo300 = Color(0xFF818CF8)
    val Indigo100 = Color(0xFFE0E7FF)
    val Indigo900 = Color(0xFF312E81)

    val Pink400 = Color(0xFFF472B6)
    val Pink600 = Color(0xFFDB2777)
    val Pink100 = Color(0xFFFCE7F3)
    val Pink900 = Color(0xFF831843)

    val Lavender = Color(0xFFA78BFA)
}

// LIGHT THEME

object LightTokens {
    val Primary = Brand.Violet600
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Brand.Violet100
    val OnPrimaryContainer = Brand.Violet900

    val Secondary = Brand.Indigo500
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Brand.Indigo100
    val OnSecondaryContainer = Brand.Indigo900

    val Tertiary = Brand.Pink600
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Brand.Pink100
    val OnTertiaryContainer = Brand.Pink900

    val Error = Color(0xFFEF4444)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFEE2E2)
    val OnErrorContainer = Color(0xFF7F1D1D)

    // Neutral system — soft, clean
    val Background = Color(0xFFF6F5FB)
    val OnBackground = Color(0xFF1A1A2E)
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF1A1A2E)
    val SurfaceVariant = Color(0xFFF1F0F7)
    val OnSurfaceVariant = Color(0xFF6B7280)
    val Outline = Color(0xFFE3E1EE)
    val OutlineVariant = Color(0xFFECECF1)
}

// DARK THEME

object DarkTokens {
    val Primary = Brand.Lavender
    val OnPrimary = Color(0xFF1A1726)
    val PrimaryContainer = Color(0xFF2E2747)
    val OnPrimaryContainer = Color(0xFFE9D5FF)

    // Secondary = indigo terang
    val Secondary = Brand.Indigo300
    val OnSecondary = Color(0xFF1A1726)
    val SecondaryContainer = Brand.Indigo900
    val OnSecondaryContainer = Color(0xFFC7D2FE)

    val Tertiary = Brand.Pink400
    val OnTertiary = Color(0xFF1A1726)
    val TertiaryContainer = Brand.Pink900
    val OnTertiaryContainer = Color(0xFFFBCFE8)

    val Error = Color(0xFFFCA5A5)
    val OnError = Color(0xFF7F1D1D)
    val ErrorContainer = Color(0xFF7F1D1D)
    val OnErrorContainer = Color(0xFFFECACA)

    val Background = Color(0xFF131019)
    val OnBackground = Color(0xFFF5F3FF)
    val Surface = Color(0xFF211E2E)
    val OnSurface = Color(0xFFF5F3FF)
    val SurfaceVariant = Color(0xFF2A2738)
    val OnSurfaceVariant = Color(0xFF9A95B5)
    val Outline = Color(0xFF332F44)
    val OutlineVariant = Color(0xFF252233)
}

// EXTRA ACCENT COLORS : untuk stat cards (streak amber, success green)

object StreakAccentLight {
    val Container = Color(0xFFFEF3C7)
    val OnContainer = Color(0xFF92400E)
    val Icon = Color(0xFFB45309)
}

object StreakAccentDark {
    val Container = Color(0xFF2E2410)
    val OnContainer = Color(0xFFFDE68A)
    val Icon = Color(0xFFFBBF24)
}

object SuccessAccentLight {
    val Container = Color(0xFFDCFCE7)
    val OnContainer = Color(0xFF14532D)
    val Icon = Color(0xFF15803D)
}

object SuccessAccentDark {
    val Container = Color(0xFF122A1B)
    val OnContainer = Color(0xFF86EFAC)
    val Icon = Color(0xFF4ADE80)
}
