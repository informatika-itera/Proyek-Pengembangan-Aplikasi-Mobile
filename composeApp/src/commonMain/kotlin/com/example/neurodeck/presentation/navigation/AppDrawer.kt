package com.example.neurodeck.presentation.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import com.example.neurodeck.presentation.components.StickyNoteBadge
import org.koin.compose.koinInject

// ════════════════════════════════════════════════════════════════════════════
// AppDrawer.kt — commonMain — REDESIGNED (Sprint 3: Vivid Logic + reactive)
//
// ModalDrawerSheet untuk side navigation drawer NeuroDeck.
//
// PERUBAHAN Sprint 3:
//   1. REACTIVE profil — inject UserPreferencesRepository via Koin, observe
//      observeProfile() → nama + foto avatar auto-update saat user edit profil
//   2. Coil AsyncImage untuk foto profil (bukan placeholder icon lagi)
//   3. Desain Vivid Logic — header card primaryContainer + avatar ber-border +
//      sticky badge, nama panjang di-handle ellipsis (responsif)
//
// Struktur konten:
//   1. Profile Header Card  — avatar + nama + username + sticky badge
//   2. MAIN NAVIGATION      — 5 tab utama (mirror BottomNav)
//   3. QUICK ACTIONS        — Deck Favorit, Generate Cepat, Belajar Sekarang
//   4. EXTERNAL & INFO      — GitHub repo, About
// ════════════════════════════════════════════════════════════════════════════

/**
 * Konten Modal Navigation Drawer untuk NeuroDeck.
 *
 * Profil di header REACTIVE — di-observe langsung dari UserPreferencesRepository
 * via Koin, jadi auto-update saat user ganti nama / foto di EditProfile.
 *
 * @param navController  Untuk navigate + read current route (highlight selected).
 * @param closeDrawer    Lambda untuk close drawer setelah item action (UX standard).
 */
@Composable
fun AppDrawer(
    navController: NavHostController,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // REACTIVE: observe profil dari DataStore — auto-update nama + foto
    val userPrefs: UserPreferencesRepository = koinInject()
    val profile by userPrefs.observeProfile().collectAsState(initial = UserProfile())

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

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
        ) {
            // ════════════════════════════════════════════════════════════════
            // 1. PROFILE HEADER CARD (Vivid Logic, reactive)
            // ════════════════════════════════════════════════════════════════
            ProfileHeaderCard(
                name = profile.name,
                username = profile.username,
                avatarUri = profile.avatarUri,
                onClick = { navigateAndClose(Screen.Profile.route) },
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            DrawerQuickActionItem(
                icon = Icons.Outlined.Favorite,
                label = "Deck Favorit",
                onClick = { navigateAndClose(Screen.Decks.route) },
            )
            DrawerQuickActionItem(
                icon = Icons.Outlined.AutoAwesome,
                label = "Generate Cepat",
                onClick = { navigateAndClose(Screen.ImportGenerate.createRoute(0L)) },
            )
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PRIVATE COMPOSABLES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Profile header card — Vivid Logic style.
 *
 * Layout: card primaryContainer rounded + border, isi avatar (foto via Coil
 * atau fallback icon) + nama (bold, ellipsis kalau panjang) + username +
 * sticky badge "PROFIL". Tap = ke Profile tab.
 */
@Composable
private fun ProfileHeaderCard(
    name: String,
    username: String,
    avatarUri: String?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Avatar — foto via Coil, atau fallback icon. Ring putih + border.
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (avatarUri != null) {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = "Foto profil",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Avatar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                StickyNoteBadge(
                    text = "LIHAT PROFIL",
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                )
            }
        }
    }
}

/**
 * Label section di drawer (e.g. "NAVIGASI", "AKSI CEPAT").
 */
@Composable
private fun DrawerSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
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
 * Quick action item — NavigationDrawerItem dengan selected=false.
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