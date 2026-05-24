package com.example.neurodeck.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Style
import androidx.compose.ui.graphics.vector.ImageVector

// ════════════════════════════════════════════════════════════════════════════
// BottomNavItem.kt — commonMain
//
// Sprint 2 — Prioritas 3a (Navigation Infrastructure)
//
// File ini mendefinisikan 5 item Bottom Navigation NeuroDeck:
//   🏠 Home   — Dashboard ringkas + Continue Learning
//   📚 Decks  — CRUD deck + Study session
//   💬 AI Chat — Tutor AI conversation
//   📊 Stats  — Analytics belajar mendalam
//   👤 Profile — User info + Settings + Data Management
//
// Sealed class supaya:
//   1. Compile-time exhaustive — kalau tambah tab baru, when() di NavBar
//      akan ke-flag oleh compiler.
//   2. Type-safe — tidak ada string typo saat reference tab.
//   3. Discoverable — `BottomNavItem.all` jadi single source of truth
//      untuk list tab yang dipakai di BottomNavigationBar composable.
//
// Setiap tab punya 2 icon (outlined untuk inactive, rounded/filled untuk
// active) — pattern Material 3 yang umum di Compose Multiplatform.
// ════════════════════════════════════════════════════════════════════════════

/**
 * Item bottom navigation. Setiap subclass merepresentasikan satu tab.
 *
 * @property screen     Route screen yang dituju saat tab di-tap.
 * @property label      Label teks di bawah icon. Pakai Bahasa Indonesia
 *                      (sesuai konvensi UI app — target user mahasiswa ITERA).
 * @property iconActive Icon saat tab AKTIF (rounded/filled style — terisi penuh).
 * @property iconIdle   Icon saat tab TIDAK aktif (outlined style — outline saja).
 */
sealed class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconActive: ImageVector,
    val iconIdle: ImageVector,
) {
    data object Home : BottomNavItem(
        screen     = Screen.Home,
        label      = "Beranda",
        iconActive = Icons.Rounded.Home,
        iconIdle   = Icons.Outlined.Home,
    )

    data object Decks : BottomNavItem(
        screen     = Screen.Decks,
        label      = "Decks",
        iconActive = Icons.Rounded.Style,       // Style = ikon kartu/deck
        iconIdle   = Icons.Outlined.Style,
    )

    data object AIChat : BottomNavItem(
        screen     = Screen.AIChat,
        label      = "AI Chat",
        iconActive = Icons.Rounded.AutoAwesome, // AutoAwesome = ikon AI/sparkle
        iconIdle   = Icons.Outlined.AutoAwesome,
    )

    data object Stats : BottomNavItem(
        screen     = Screen.Stats,
        label      = "Statistik",
        iconActive = Icons.Rounded.BarChart,
        iconIdle   = Icons.Outlined.BarChart,
    )

    data object Profile : BottomNavItem(
        screen     = Screen.Profile,
        label      = "Profil",
        iconActive = Icons.Rounded.Person,
        iconIdle   = Icons.Outlined.Person,
    )

    companion object {
        /**
         * URUTAN tab di Bottom Navigation. Urutan ini visual dan PENTING:
         * Home dulu (default landing), Decks (paling sering dipakai) di
         * tengah-kiri, AI Chat di center (lebih "premium feel"), Stats
         * dan Profile di kanan.
         *
         * Hanya satu list ini yang harus diubah kalau mau re-order tab.
         */
        val all: List<BottomNavItem> = listOf(
            Home,
            Decks,
            AIChat,
            Stats,
            Profile,
        )

        /**
         * Helper: cari BottomNavItem berdasarkan route string.
         * Berguna di TopBar untuk dapat label tab dari current route
         * (misal untuk set title TopBar).
         *
         * Return null kalau route bukan main tab (e.g. sedang di sub-screen
         * seperti StudySession atau AddCard).
         */
        fun findByRoute(route: String?): BottomNavItem? =
            all.firstOrNull { it.screen.route == route }
    }
}