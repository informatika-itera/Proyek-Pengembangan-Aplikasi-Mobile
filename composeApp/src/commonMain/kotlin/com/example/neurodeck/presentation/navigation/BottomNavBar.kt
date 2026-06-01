package com.example.neurodeck.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// ════════════════════════════════════════════════════════════════════════════
// BottomNavBar.kt — commonMain
//
// Sprint 2 — Prioritas 3a (Navigation Infrastructure)
//
// Composable yang render Material 3 `NavigationBar` dengan 5 tab NeuroDeck.
// Selected tab di-derive otomatis dari current route NavController, jadi
// kalau user navigate via Drawer atau deep link, tab indicator tetap update.
//
// Navigasi pakai pattern STANDARD Compose Navigation untuk bottom tabs:
//   - popUpTo(startDestination) { saveState = true } — kembali ke root tab,
//     simpan state subtree (scroll position, form input dll)
//   - launchSingleTop = true — jangan stack ulang kalau sudah di tab tsb
//   - restoreState = true — restore state subtree yang sebelumnya disimpan
//
// Tanpa 3 opsi di atas, tap Home → Decks → Home akan menumpuk Home 2x di
// back stack — back button jadi aneh (harus tekan 3x untuk exit).
// ════════════════════════════════════════════════════════════════════════════

/**
 * Bottom Navigation Bar dengan 5 tab NeuroDeck.
 *
 * @param navController NavController dari [AppNavHost] — dipakai untuk:
 *                      (a) membaca current route untuk highlight selected tab,
 *                      (b) navigate saat tab di-tap.
 * @param modifier      Modifier opsional. Biasanya tidak perlu diisi karena
 *                      sudah di-place oleh Scaffold's bottomBar slot.
 */
@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // currentBackStackEntryAsState() = recompose tiap kali back stack berubah,
    // jadi `currentRoute` selalu up-to-date dengan screen yang sedang aktif.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        // Tonal elevation 3.dp memberi sedikit shadow agar nav bar
        // visually terpisah dari content di atasnya (Material 3 guideline).
        tonalElevation = 3.dp,
    ) {
        BottomNavItem.all.forEach { item ->
            val selected = currentRoute == item.screen.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // Avoid re-navigate kalau user tap tab yang sedang aktif —
                    // ini behavior standard Android (tap tab aktif = no-op).
                    if (!selected) {
                        navController.navigate(item.screen.route) {
                            // Kembali ke start destination di stack sambil
                            // SAVE state dari subtree saat ini (penting untuk UX —
                            // scroll position di Decks tab tidak hilang saat
                            // user pindah ke Profile lalu balik ke Decks).
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Hindari multiple copy dari destination yang sama
                            // di top of stack.
                            launchSingleTop = true
                            // Restore state subtree yang sebelumnya saved.
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.iconActive else item.iconIdle,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        // Label tab aktif sedikit lebih tebal — visual cue
                        // tambahan selain warna untuk accessibility.
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                // Vivid Logic style — selected pill purple, unselected outlined
                colors = NavigationBarItemDefaults.colors(
                    // Selected: filled purple pill
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    // Unselected: muted onSurface
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}