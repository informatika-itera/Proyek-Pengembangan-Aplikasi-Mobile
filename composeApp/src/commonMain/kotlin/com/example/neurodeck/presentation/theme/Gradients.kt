package com.example.neurodeck.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class NeurodeckExtras(
    // ── Gradients (Brush) ──
    val heroBrush: Brush,
    val navActiveBrush: Brush,
    val deckBrushPrimary: Brush,
    val deckBrushPink: Brush,
    val progressBrushPrimary: Brush,
    val progressBrushPink: Brush,

    // ── Extra accent solid (stat cards) ──
    val streakContainer: Color,
    val onStreakContainer: Color,
    val streakIcon: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val successIcon: Color,
)

// Gradient brushes
private val HeroBrush = Brush.linearGradient(
    listOf(Brand.Violet500, Brand.Indigo500, Brand.Indigo600),
)
private val NavActiveBrush = Brush.linearGradient(
    listOf(Brand.Violet500, Brand.Indigo500),
)
private val DeckBrushPrimary = Brush.linearGradient(
    listOf(Color(0xFFA855F7), Brand.Indigo500),
)
private val DeckBrushPink = Brush.linearGradient(
    listOf(Brand.Pink400, Color(0xFFA855F7)),
)
private val ProgressBrushPrimary = Brush.linearGradient(
    listOf(Brand.Violet500, Brand.Indigo500),
)
private val ProgressBrushPink = Brush.linearGradient(
    listOf(Brand.Pink400, Color(0xFFA855F7)),
)

/** Extras untuk LIGHT mode. */
fun lightExtras(): NeurodeckExtras = NeurodeckExtras(
    heroBrush = HeroBrush,
    navActiveBrush = NavActiveBrush,
    deckBrushPrimary = DeckBrushPrimary,
    deckBrushPink = DeckBrushPink,
    progressBrushPrimary = ProgressBrushPrimary,
    progressBrushPink = ProgressBrushPink,
    streakContainer = StreakAccentLight.Container,
    onStreakContainer = StreakAccentLight.OnContainer,
    streakIcon = StreakAccentLight.Icon,
    successContainer = SuccessAccentLight.Container,
    onSuccessContainer = SuccessAccentLight.OnContainer,
    successIcon = SuccessAccentLight.Icon,
)

/** Extras untuk DARK mode */
fun darkExtras(): NeurodeckExtras = NeurodeckExtras(
    heroBrush = HeroBrush,
    navActiveBrush = NavActiveBrush,
    deckBrushPrimary = DeckBrushPrimary,
    deckBrushPink = DeckBrushPink,
    progressBrushPrimary = ProgressBrushPrimary,
    progressBrushPink = ProgressBrushPink,
    streakContainer = StreakAccentDark.Container,
    onStreakContainer = StreakAccentDark.OnContainer,
    streakIcon = StreakAccentDark.Icon,
    successContainer = SuccessAccentDark.Container,
    onSuccessContainer = SuccessAccentDark.OnContainer,
    successIcon = SuccessAccentDark.Icon,
)

/**
 CompositionLocal untuk NeurodeckExtras. Di-provide di neurodeckTheme().
 */
val LocalNeurodeckExtras = staticCompositionLocalOf<NeurodeckExtras> {
    error("NeurodeckExtras belum di-provide. Pastikan UI dibungkus neurodeckTheme().")
}

/**
 Accessor object (pakai NeurodeckTheme.extras di composable manapun)
 */
object NeurodeckTheme {
    val extras: NeurodeckExtras
        @Composable
        get() = LocalNeurodeckExtras.current
}
