package com.example.neurodeck.presentation.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

// ════════════════════════════════════════════════════════════════════════════
// AppTopBar.kt — commonMain
//
// Sprint 2 — Prioritas 3a (Navigation Infrastructure)
//
// Material 3 CenterAlignedTopAppBar yang fleksibel:
//   - Title di tengah (Material 3 standard untuk app dengan bottom nav)
//   - Navigation icon kiri: hamburger (untuk main tab) atau back arrow
//     (untuk sub-screen) — auto-decided berdasarkan canNavigateBack flag
//   - Action slot kanan: opsional, di-pass dari caller
//   - Scroll behavior support — title collapse on scroll (M3 standard)
//
// Komponen ini di-pakai oleh:
//   - AppNavHost (P3a.4) — sebagai topBar slot di Scaffold
//
// Title di-derive dari current route via BottomNavItem.findByRoute() untuk
// main tabs, fallback ke "NeuroDeck" untuk sub-screens (sub-screen lazimnya
// override title via parameter explicit).
// ════════════════════════════════════════════════════════════════════════════

/**
 * TopAppBar reusable untuk semua screen NeuroDeck.
 *
 * 2 mode berdasarkan [canNavigateBack]:
 *   - `false` (default) — MAIN TAB MODE: hamburger icon, tap = open drawer
 *   - `true`            — SUB-SCREEN MODE: back arrow, tap = popBackStack
 *
 * @param title              Judul yang ditampilkan di tengah. Caller harus
 *                           pass eksplisit (lihat helper [rememberTopBarTitle]
 *                           kalau mau auto-derive dari route).
 * @param canNavigateBack    True kalau sedang di sub-screen (back arrow).
 *                           False kalau main tab (hamburger).
 * @param onNavigationClick  Action saat icon nav (hamburger / back) di-tap.
 *                           Caller wajib pass: untuk main tab pass `openDrawer`,
 *                           untuk sub-screen pass `navController::popBackStack`.
 * @param actions            Optional composable di pojok kanan TopBar — biasanya
 *                           IconButton(s) untuk action per-screen (search, clear,
 *                           settings shortcut, dll). Default kosong.
 * @param scrollBehavior     Optional TopAppBarScrollBehavior dari M3 untuk
 *                           collapse-on-scroll. Pass null kalau tidak butuh.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                // Tidak set overflow di sini — TopAppBar M3 sudah handle
                // ellipsis internal untuk title yang terlalu panjang.
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    // automirrored.ArrowBack = auto-flip untuk RTL languages
                    // (Arabic, Hebrew). Best practice Material 3.
                    imageVector = if (canNavigateBack) {
                        Icons.AutoMirrored.Outlined.ArrowBack
                    } else {
                        Icons.Outlined.Menu
                    },
                    contentDescription = if (canNavigateBack) "Kembali" else "Buka menu",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            // surface = warna dasar non-scrolled state.
            containerColor = MaterialTheme.colorScheme.surface,
            // surfaceContainer = warna saat scroll behavior memberi elevation,
            // sedikit lebih gelap untuk visual hierarchy. M3 standard.
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        scrollBehavior = scrollBehavior,
    )
}

// ════════════════════════════════════════════════════════════════════════════
// HELPER — Derive title dari current route
// ════════════════════════════════════════════════════════════════════════════

/**
 * Derive title TopBar berdasarkan route string saat ini.
 *
 * Strategi:
 *   1. Kalau route ada di [BottomNavItem.all] (main tab), pakai label tab.
 *      Contoh: route "home" → "Beranda", route "deck_library" → "Decks".
 *   2. Kalau bukan, pakai fallback dari map [SubScreenTitles] di bawah.
 *      Contoh: route "create_deck" → "Buat Deck Baru".
 *   3. Kalau juga tidak ada di map, fallback ke default brand "NeuroDeck".
 *      Ini safety net — seharusnya tidak terjadi kalau map di-maintain.
 *
 * Dipakai di [AppNavHost] (P3a.4) untuk auto-set title TopBar sesuai
 * destination saat ini, tanpa setiap screen harus pass title manual.
 *
 * @param route  Route string dari `navController.currentBackStackEntry`.
 *               Bisa null kalau back stack kosong (startup) — fallback ke brand.
 */
fun resolveTopBarTitle(route: String?): String {
    if (route == null) return DEFAULT_BRAND_TITLE

    // Strip query params/arguments — route "card_list/{deckId}" jadi "card_list".
    // Compose Navigation route mengandung pattern path arg, jadi kita extract
    // base route saja (sebelum slash atau curly brace pertama).
    val baseRoute = route.substringBefore('/').substringBefore('{')

    // Cek main tabs dulu — pakai BottomNavItem.findByRoute yang sudah ada.
    // Note: BottomNavItem.findByRoute match by exact screen.route, jadi
    // pass `route` original (bukan baseRoute) supaya match string lengkap.
    BottomNavItem.findByRoute(route)?.let { return it.label }

    // Fallback ke sub-screen titles map.
    return SubScreenTitles[baseRoute] ?: DEFAULT_BRAND_TITLE
}

/**
 * Default title kalau route tidak dikenal atau null.
 * Pakai nama brand supaya app tetap punya identitas visual.
 */
private const val DEFAULT_BRAND_TITLE = "NeuroDeck"

/**
 * Mapping route base → title untuk sub-screens (non-main-tab).
 *
 * Maintain di sini supaya:
 *   - Single source of truth (vs. hardcode title di setiap screen)
 *   - Mudah update bahasa (kalau nanti i18n)
 *   - Test mudah (pure data, no Compose dep)
 *
 * Key = base route (tanpa argument template).
 * Value = title yang muncul di TopBar.
 *
 * Catatan: untuk route dengan argument seperti "card_list/{deckId}", base
 * route-nya "card_list" — substring before slash.
 */
private val SubScreenTitles: Map<String, String> = mapOf(
    // Sub-screens dari Decks tab
    "create_deck"      to "Buat Deck Baru",
    "import_generate"  to "AI Generate Kartu",
    "card_list"        to "Kartu di Deck",
    "add_card"         to "Tambah Kartu",
    "edit_card"        to "Edit Kartu",
    "study_session"    to "Sesi Belajar",

    // Sub-screens dari Profile tab
    "edit_profile"     to "Edit Profil",
    "settings"         to "Pengaturan",
    "about"            to "Tentang Aplikasi",
)