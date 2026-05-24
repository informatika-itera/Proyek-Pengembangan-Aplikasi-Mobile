package com.example.neurodeck.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// ════════════════════════════════════════════════════════════════════════════
// AppDrawer.kt — commonMain
//
// Sprint 2 — Prioritas 3a (Navigation Infrastructure)
//
// ModalDrawerSheet untuk side navigation drawer NeuroDeck.
//
// Struktur konten (dari atas ke bawah):
//   1. Mini Profile Header  — avatar circle + nama + tagline
//   2. MAIN NAVIGATION      — 5 tab utama (sama persis dengan BottomNav)
//   3. QUICK ACTIONS        — shortcut ke Favorite Decks, Quick Generate, Study Due
//   4. EXTERNAL & INFO      — GitHub repo, About
//
// Mengapa duplikasi MAIN NAVIGATION dengan BottomNav?
// - Material 3 guideline: drawer adalah ALTERNATIVE entry point, bukan replacement.
// - User landscape mode atau tablet: drawer lebih nyaman dari bottom bar.
// - Accessibility: user dengan motor impairment lebih mudah pakai drawer
//   karena items lebih besar dan vertikal.
//
// Navigation pakai pattern yang SAMA dengan BottomNavBar: popUpTo startDest +
// saveState + launchSingleTop + restoreState. Plus closeDrawer() callback
// supaya drawer otomatis tutup setelah pilih item (UX standard).
// ════════════════════════════════════════════════════════════════════════════

/**
 * Konten Modal Navigation Drawer untuk NeuroDeck.
 *
 * Composable ini di-wrap oleh `ModalNavigationDrawer` di [AppNavHost] —
 * bukan stand-alone screen.
 *
 * @param navController  Untuk navigate saat item di-tap dan untuk read
 *                       current route (highlight selected item).
 * @param closeDrawer    Lambda untuk close drawer (biasanya
 *                       `{ scope.launch { drawerState.close() } }`).
 *                       Dipanggil setelah setiap item action — UX standard:
 *                       drawer auto-close setelah user pilih sesuatu.
 * @param userName       Nama user untuk Mini Profile Header. Default "Mahasiswa
 *                       NeuroDeck" — akan di-replace data real dari
 *                       UserPreferencesRepository di P3e (Profile tab).
 */
@Composable
fun AppDrawer(
    navController: NavHostController,
    closeDrawer: () -> Unit,
    userName: String = "Mahasiswa NeuroDeck",
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    /**
     * Helper internal — navigate ke route + tutup drawer.
     * Pakai pattern yang sama dengan BottomNavBar untuk konsistensi back stack.
     */
    fun navigateAndClose(targetRoute: String) {
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
        closeDrawer()
    }

    // ModalDrawerSheet provides Material 3 surface, elevation, dan dimensi
    // drawer yang sesuai guideline (~280dp width, full height).
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        // Scrollable column — kalau menu items jadi panjang nanti
        // (banyak quick actions, dll), drawer tetap bisa di-scroll.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
        ) {
            // ════════════════════════════════════════════════════════════════
            // 1. MINI PROFILE HEADER
            // ════════════════════════════════════════════════════════════════
            MiniProfileHeader(
                userName = userName,
                onClick = { navigateAndClose(Screen.Profile.route) },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // ════════════════════════════════════════════════════════════════
            // 2. MAIN NAVIGATION (mirror BottomNav)
            // ════════════════════════════════════════════════════════════════
            DrawerSectionLabel("Navigasi")

            BottomNavItem.all.forEach { item ->
                val selected = currentRoute == item.screen.route
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (selected) item.iconActive else item.iconIdle,
                            contentDescription = null,
                        )
                    },
                    label = { Text(text = item.label) },
                    selected = selected,
                    onClick = { navigateAndClose(item.screen.route) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // ════════════════════════════════════════════════════════════════
            // 3. QUICK ACTIONS
            // ════════════════════════════════════════════════════════════════
            DrawerSectionLabel("Aksi Cepat")

            // ⭐ Favorite Decks — navigate ke Decks tab.
            // (Filter "Favorite" akan auto-applied via shared ViewModel
            //  atau navigation argument di Sprint 3 — sekarang minimal:
            //  cukup buka tab Decks, user pilih filter manual.)
            DrawerQuickActionItem(
                icon = Icons.Outlined.Favorite,
                label = "Deck Favorit",
                onClick = { navigateAndClose(Screen.Decks.route) },
            )

            // ✨ Quick Generate — langsung ke layar AI generate flashcards.
            // ImportGenerate dipanggil dengan deckId=0L = "buat deck baru
            // sambil generate" (vs deckId real = "tambah ke deck existing").
            DrawerQuickActionItem(
                icon = Icons.Outlined.AutoAwesome,
                label = "Generate Cepat",
                onClick = { navigateAndClose(Screen.ImportGenerate.createRoute(0L)) },
            )

            // ▶️ Study Due — quick entry ke study session.
            // Karena belum ada concept "global due session" yang tidak terikat deck,
            // untuk sekarang arahkan ke Decks tab (user pilih deck → study).
            // Sprint 3+ bisa dibuat "Smart Session" yang campur kartu due dari semua deck.
            DrawerQuickActionItem(
                icon = Icons.Outlined.PlayArrow,
                label = "Belajar Sekarang",
                onClick = { navigateAndClose(Screen.Decks.route) },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // ════════════════════════════════════════════════════════════════
            // 4. EXTERNAL & INFO
            // ════════════════════════════════════════════════════════════════
            DrawerSectionLabel("Lainnya")

            // GitHub Repo — placeholder onClick.
            // External URL handling butuh platform-specific code (URI handler):
            //   androidx.compose.ui.platform.LocalUriHandler
            // Akan di-wire saat polish Sprint 4 atau pas implementasi About.
            // Untuk Sprint 2, route ke About screen yang akan punya link clickable.
            DrawerQuickActionItem(
                icon = Icons.Outlined.Code,
                label = "GitHub Repo",
                onClick = { navigateAndClose(Screen.About.route) },
            )

            DrawerQuickActionItem(
                icon = Icons.Outlined.Info,
                label = "Tentang Aplikasi",
                onClick = { navigateAndClose(Screen.About.route) },
            )

            // Bottom spacer — hindari item terakhir nempel ke bottom nav bar
            // saat drawer di-scroll ke paling bawah.
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PRIVATE COMPOSABLES — Internal building blocks
// ════════════════════════════════════════════════════════════════════════════

/**
 * Mini profile header di paling atas drawer.
 *
 * Layout: avatar bulat + (nama + tagline) horizontal.
 * Tap = navigate ke Profile tab (consistent UX dengan GitHub/Twitter app).
 *
 * Avatar placeholder pakai Icon Person dalam circle berwarna primaryContainer.
 * Akan di-replace dengan Coil AsyncImage (avatarUri dari DataStore) di P3e.
 */
@Composable
private fun MiniProfileHeader(
    userName: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Avatar placeholder — circle dengan ikon orang di tengah.
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Belajar dengan SM-2 ✨",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Label section di drawer (e.g. "NAVIGASI", "AKSI CEPAT").
 *
 * Pakai typography labelMedium + warna onSurfaceVariant supaya tidak
 * dominan — sekedar visual divider antar group items.
 */
@Composable
private fun DrawerSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            start = 28.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 8.dp,
        ),
    )
}

/**
 * Quick action item — versi simplified dari NavigationDrawerItem.
 *
 * Beda dengan NavigationDrawerItem dari M3:
 * - Tidak punya selected state (quick actions adalah action, bukan destination)
 * - Lebih compact (tidak ada selected indicator background)
 *
 * Implementasi pakai NavigationDrawerItem dengan selected=false biar konsisten
 * touch target & ripple effect.
 */
@Composable
private fun DrawerQuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(text = label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
}