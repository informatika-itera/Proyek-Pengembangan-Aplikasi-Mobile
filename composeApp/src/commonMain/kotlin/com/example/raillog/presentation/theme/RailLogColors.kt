package com.example.raillog.presentation.theme

import androidx.compose.ui.graphics.Color

object RailLogColors {

    // ── Brand ──────────────────────────────────────────────
    val Brand900 = Color(0xFF0A1628)   // deepest navy
    val Brand800 = Color(0xFF0F2044)
    val Brand700 = Color(0xFF1A3260)
    val Brand600 = Color(0xFF1E3A8A)   // primary action
    val Brand500 = Color(0xFF2563EB)   // hover/active
    val Brand400 = Color(0xFF60A5FA)   // light accent
    val Brand100 = Color(0xFFDBEAFE)   // tint surface
    val Brand50  = Color(0xFFEFF6FF)   // subtle bg

    // ── Neutral ────────────────────────────────────────────
    val Neutral950 = Color(0xFF0A0A0A)
    val Neutral900 = Color(0xFF171717)
    val Neutral800 = Color(0xFF262626)
    val Neutral700 = Color(0xFF404040)
    val Neutral600 = Color(0xFF525252)
    val Neutral500 = Color(0xFF737373)
    val Neutral400 = Color(0xFF9CA3AF)
    val Neutral300 = Color(0xFFD1D5DB)
    val Neutral200 = Color(0xFFE5E7EB)
    val Neutral100 = Color(0xFFF3F4F6)
    val Neutral50  = Color(0xFFF9FAFB)
    val White      = Color(0xFFFFFFFF)

    // ── Semantic ───────────────────────────────────────────
    val Success600 = Color(0xFF059669)
    val Success100 = Color(0xFFD1FAE5)
    val Success50  = Color(0xFFECFDF5)

    val Warning600 = Color(0xFFD97706)
    val Warning100 = Color(0xFFFDE68A)
    val Warning50  = Color(0xFFFFFBEB)

    val Danger600  = Color(0xFFDC2626)
    val Danger100  = Color(0xFFFEE2E2)
    val Danger50   = Color(0xFFFEF2F2)

    val Info600    = Color(0xFF0284C7)
    val Info100    = Color(0xFFE0F2FE)
    val Info50     = Color(0xFFF0F9FF)

    // ── Surface aliases (semantic shortcuts) ───────────────
    val Surface        = White
    val SurfaceRaised  = White
    val Background     = Neutral50
    val BackgroundDim  = Neutral100

    val BorderDefault  = Neutral200
    val BorderSubtle   = Neutral100
    val BorderStrong   = Neutral300

    val TextPrimary    = Neutral900
    val TextSecondary  = Neutral600
    val TextTertiary   = Neutral400
    val TextInverse    = White

    val PrimaryAction  = Brand600
    val PrimaryHover   = Brand700
    val PrimaryMuted   = Brand50

    // ── AI accent ─────────────────────────────────────────
    val AISurface = Color(0xFFF5F3FF)
    val AIBorder  = Color(0xFFDDD6FE)
    val AIText    = Color(0xFF5B21B6)
}