package com.example.neurodeck.presentation.theme

import androidx.compose.ui.graphics.Color

// ════════════════════════════════════════════════════════════════════════════
// NeuroDeck Color System
//
// 2 palette terpisah untuk light & dark mode (sesuai design spec Stitch):
//   - ☀️ Vivid Logic (light)  → vibrant purple + black + yellow sticky note
//   - 🌑 Midnight (dark)       → soft lavender + neon green + warm orange
//
// Naming convention: BRAND_<role>_<modifier?>
//   - Brand colors = identity (purple primary, yellow accent dll)
//   - Neutral colors = background, surface, text
//
// Tidak pakai ColorScheme.surfaceContainer (Material 3 expressive) supaya
// kompatibel dengan AGP 8.5 baseline. Pakai konvensi M3 baseline saja.
// ════════════════════════════════════════════════════════════════════════════

// ════════════════════════════════════════════════════════════════════════════
// 🎨 SHARED BRAND COLORS — sama di light & dark (constants identity)
// ════════════════════════════════════════════════════════════════════════════

object BrandPurple {
    val Primary = Color(0xFF8B5CF6)        // Vivid violet — main CTA color
    val PrimaryDark = Color(0xFFA78BFA)    // Lighter for dark mode visibility
    val Container = Color(0xFFF3E8FF)      // Light pastel container (light mode)
    val ContainerDark = Color(0xFF2A1F45)  // Deep purple container (dark mode)
    val OnContainer = Color(0xFF3B0764)    // Dark text on light container
    val OnContainerDark = Color(0xFFE9D5FF) // Light text on dark container
}

object BrandYellow {
    val StickyNote = Color(0xFFFFDE59)     // Yellow sticky note (light)
    val OnStickyNote = Color(0xFF000000)   // Black text on yellow
}

object BrandOrange {
    val WarmAccent = Color(0xFFFB923C)     // Orange (dark mode tertiary)
    val OnWarmAccent = Color(0xFF12141C)   // Dark text on orange
}

object BrandGreen {
    val NeonAccent = Color(0xFF4ADE80)     // Neon green (dark mode secondary)
    val OnNeonAccent = Color(0xFF12141C)   // Dark text on green
}

// ════════════════════════════════════════════════════════════════════════════
// ☀️ LIGHT THEME — "Vivid Logic"
// Background base #F4F4F4, headlines pure black, accent purple
// ════════════════════════════════════════════════════════════════════════════

object LightTokens {
    // Primary = purple (CTAs, active states)
    val Primary = BrandPurple.Primary
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = BrandPurple.Container
    val OnPrimaryContainer = BrandPurple.OnContainer

    // Secondary = BLACK (inverted buttons, dark CTAs)
    val Secondary = Color(0xFF000000)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFE5E5E5)
    val OnSecondaryContainer = Color(0xFF000000)

    // Tertiary = yellow sticky note
    val Tertiary = BrandYellow.StickyNote
    val OnTertiary = BrandYellow.OnStickyNote
    val TertiaryContainer = Color(0xFFFEF3C7)
    val OnTertiaryContainer = Color(0xFF422006)

    // Error = vibrant red
    val Error = Color(0xFFEF4444)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFEE2E2)
    val OnErrorContainer = Color(0xFF7F1D1D)

    // Neutral system
    val Background = Color(0xFFF4F4F4)
    val OnBackground = Color(0xFF000000)
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF000000)
    val SurfaceVariant = Color(0xFFE5E5E5)
    val OnSurfaceVariant = Color(0xFF525252)
    val Outline = Color(0xFF000000)        // Black border (Vivid Logic signature)
    val OutlineVariant = Color(0xFFD4D4D4)
}

// ════════════════════════════════════════════════════════════════════════════
// 🌑 DARK THEME — "Midnight"
// Background deep navy-black #12141C, accent soft lavender + neon green/orange
// ════════════════════════════════════════════════════════════════════════════

object DarkTokens {
    // Primary = soft lavender purple (better contrast on dark)
    val Primary = BrandPurple.PrimaryDark
    val OnPrimary = Color(0xFF12141C)
    val PrimaryContainer = BrandPurple.ContainerDark
    val OnPrimaryContainer = BrandPurple.OnContainerDark

    // Secondary = neon GREEN (dark mode accent — beda dari light yang black!)
    val Secondary = BrandGreen.NeonAccent
    val OnSecondary = BrandGreen.OnNeonAccent
    val SecondaryContainer = Color(0xFF14532D)
    val OnSecondaryContainer = Color(0xFFBBF7D0)

    // Tertiary = warm orange (dark mode warning/critical highlight)
    val Tertiary = BrandOrange.WarmAccent
    val OnTertiary = BrandOrange.OnWarmAccent
    val TertiaryContainer = Color(0xFF7C2D12)
    val OnTertiaryContainer = Color(0xFFFED7AA)

    // Error = soft red (less harsh on dark)
    val Error = Color(0xFFFCA5A5)
    val OnError = Color(0xFF7F1D1D)
    val ErrorContainer = Color(0xFF7F1D1D)
    val OnErrorContainer = Color(0xFFFECACA)

    // Neutral system
    val Background = Color(0xFF12141C)
    val OnBackground = Color(0xFFFAFAFA)
    val Surface = Color(0xFF1E2030)
    val OnSurface = Color(0xFFFAFAFA)
    val SurfaceVariant = Color(0xFF2D2F40)
    val OnSurfaceVariant = Color(0xFFA1A1AA)
    val Outline = Color(0xFF3F3F46)        // Subtle border (dark mode)
    val OutlineVariant = Color(0xFF27272A)
}