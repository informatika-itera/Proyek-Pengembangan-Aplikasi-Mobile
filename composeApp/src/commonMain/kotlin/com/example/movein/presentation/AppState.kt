package com.example.movein.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppState {
    NEUTRAL, OVERWHELMED, RECOVERING
}

val AppState.displayName: String
    get() = when (this) {
        AppState.NEUTRAL -> "Normal Space"
        AppState.OVERWHELMED -> "Heavy Space"
        AppState.RECOVERING -> "Breathing Space"
    }

data class MentalTheme(
    val name: String,
    val bgDark: Color,
    val bgLight: Color,
    val cardDark: Color,
    val cardLight: Color,
    val borderDark: Color,
    val borderLight: Color,
    val accentDark: Color,
    val accentLight: Color,
    val gap: Dp,
    val glow: Color,
    val anim: String = ""
)

data class JourneyLog(
    val time: String,
    val mood: String,
    val task: String,
    val result: String,
    val type: String,
    val appState: AppState,
    val color: Color = Color.Unspecified,
    val bgColor: Color = Color.Unspecified
)

data class GrowthPhase(
    val phase: String,
    val desc: String,
    val max: Int,
    val color: Color,
    val glow: Color
)

fun getGrowthPhase(momentum: Int): GrowthPhase {
    return when {
        momentum < 50 -> GrowthPhase(
            phase = "Surviving",
            desc = "Satu langkah kecil tidak apa-apa.",
            max = 50,
            color = Color(0xFFA3A3A3),
            glow = Color(0xFFA3A3A3).copy(alpha = 0.5f)
        )
        momentum < 150 -> GrowthPhase(
            phase = "Breathing",
            desc = "Mulai menemukan ritme napas.",
            max = 150,
            color = Color(0xFF60A5FA),
            glow = Color(0xFF3B82F6).copy(alpha = 0.5f)
        )
        momentum < 300 -> GrowthPhase(
            phase = "Recovering",
            desc = "Menyusun kembali energi yang hilang.",
            max = 300,
            color = Color(0xFF34D399),
            glow = Color(0xFF10B981).copy(alpha = 0.5f)
        )
        else -> GrowthPhase(
            phase = "Flowing",
            desc = "Kamu telah kembali memegang kendali.",
            max = 600,
            color = Color(0xFF22D3EE),
            glow = Color(0xFF06B6D4).copy(alpha = 0.5f)
        )
    }
}

val MENTAL_THEMES = mapOf(
    AppState.NEUTRAL to MentalTheme(
        name = "Normal Space",
        bgDark = Color(0xFF050505),
        bgLight = Color(0xFFF8F9FA),
        cardDark = Color(0xFF171717).copy(alpha = 0.4f),
        cardLight = Color.White.copy(alpha = 0.6f),
        borderDark = Color.White.copy(alpha = 0.05f),
        borderLight = Color.Black.copy(alpha = 0.05f),
        accentDark = Color(0xFF22D3EE),
        accentLight = Color(0xFF0891B2),
        gap = 16.dp,
        glow = Color(0xFF06B6D4).copy(alpha = 0.1f)
    ),
    AppState.OVERWHELMED to MentalTheme(
        name = "Heavy Space",
        bgDark = Color(0xFF140000),
        bgLight = Color(0xFFFFF0F0),
        cardDark = Color(0xFF2A0000).copy(alpha = 0.2f),
        cardLight = Color.White.copy(alpha = 0.8f),
        borderDark = Color(0xFF450A0A).copy(alpha = 0.3f),
        borderLight = Color(0xFFFECACA),
        accentDark = Color(0xFFF87171),
        accentLight = Color(0xFFDC2626),
        gap = 8.dp,
        glow = Color(0xFFDC2626).copy(alpha = 0.2f),
        anim = "pulse"
    ),
    AppState.RECOVERING to MentalTheme(
        name = "Breathing Space",
        bgDark = Color(0xFF000814),
        bgLight = Color(0xFFF0F7FF),
        cardDark = Color(0xFF001E3C).copy(alpha = 0.2f),
        cardLight = Color.White.copy(alpha = 0.8f),
        borderDark = Color(0xFF1E3A8A).copy(alpha = 0.3f),
        borderLight = Color(0xFFBFDBFE),
        accentDark = Color(0xFF93C5FD),
        accentLight = Color(0xFF2563EB),
        gap = 24.dp,
        glow = Color(0xFF3B82F6).copy(alpha = 0.2f),
        anim = "fade-in"
    )
)
