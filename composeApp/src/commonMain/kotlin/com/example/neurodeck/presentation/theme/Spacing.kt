package com.example.neurodeck.presentation.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Token spacing terpusat untuk konsistensi layout di seluruh aplikasi
 * (Sprint 4 — UI Polish).
 *
 * Tujuan: menghilangkan "magic number" `.padding(16.dp)` yang tersebar di
 * banyak screen. Dengan token ini, jarak antar-elemen mengikuti skala 4dp
 * yang konsisten (Material Design 8dp grid–friendly):
 *
 *   xs = 4dp   → jarak super rapat (mis. antara ikon dan teks kecil)
 *   sm = 8dp   → jarak rapat (chip, badge)
 *   md = 16dp  → jarak standar konten (padding layar default)
 *   lg = 24dp  → pemisah antar-section
 *   xl = 32dp  → ruang besar (header hero, empty state)
 *
 * Cara pakai:
 *   Modifier.padding(Spacing.md)
 *   Arrangement.spacedBy(Spacing.sm)
 *
 * Catatan: token ini additive — screen lama tetap kompatibel. Migrasi bertahap
 * mengganti angka literal ke token ini sambil melakukan polish.
 */
object Spacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
}
