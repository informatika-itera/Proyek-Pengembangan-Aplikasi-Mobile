package com.soundletter.app.presentation.theme

import androidx.compose.ui.graphics.Color

object SoundLetterColors {
    // Brand Accent Color (Sky Blue)
    val SkyBlue = Color(0xFF00BFFF)
    val SkyBlueDark = Color(0xFF0099CC)
    
    // Core Backgrounds (Strict requirements: Pure White & Pure Black)
    val PureWhite = Color(0xFFFFFFFF)
    val PureBlack = Color(0xFF000000)
    val DarkSurface = Color(0xFF121212)
    
    // Text Colors
    val TextPrimaryLight = Color(0xFF000000)
    val TextSecondaryLight = Color(0xFF424242)
    val TextPrimaryDark = Color(0xFFFFFFFF)
    val TextSecondaryDark = Color(0xFFBDBDBD)
    
    // UI Elements (GlassCard)
    val GlassBackground = Color(0x0D00BFFF)
    val GlassBackgroundLight = Color(0x0D00BFFF)
    val GlassBackgroundDark = Color(0x1AFFFFFF)
    val GlassBorder = Color(0x1A00BFFF)

    // Helper for screen compatibility
    fun getBackgroundGradient(isDarkMode: Boolean): List<Color> {
        return if (isDarkMode) listOf(PureBlack, PureBlack) else listOf(PureWhite, PureWhite)
    }
}
