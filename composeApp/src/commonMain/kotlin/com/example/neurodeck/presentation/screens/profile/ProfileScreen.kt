package com.example.neurodeck.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.presentation.components.ConfirmDialog
import com.example.neurodeck.presentation.components.LoadingIndicator
import com.example.neurodeck.presentation.components.SectionTitle
import org.koin.compose.viewmodel.koinViewModel

// ════════════════════════════════════════════════════════════════════════════
// ProfileScreen.kt — Sprint 2 P3e.3
//
// 👤 Profile Tab — user info + achievement + settings + data management + about.
//
// Layout (LazyColumn, top to bottom):
//   1. ProfileHeader      — avatar + name + bio + tombol Edit
//   2. Achievement Stats  — 4 stat: Decks / Cards / Reviews / Streak
//   3. Settings Section
//        - Dark Mode (3 chip: Light/Dark/System)
//        - Notifications toggle (UI only Sprint 2)
//        - Language (UI only, ID default)
//   4. Data Management Section
//        - Reset All Data (double-confirm dialog)
//   5. About Section
//        - App version + tap → AboutScreen
//
// NO Scaffold/TopBar — chrome dari AppNavHost.
// ════════════════════════════════════════════════════════════════════════════

/**
 * Profile Tab screen.
 *
 * @param onEditProfile  Navigate ke EditProfileScreen.
 * @param onAbout        Navigate ke AboutScreen.
 */
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onAbout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetConfirm by remember { mutableStateOf(false) }
    var showFinalResetConfirm by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ════════════════════════════════════════════════════════════════
            // 1. HEADER (avatar + name + bio + Edit button)
            // ════════════════════════════════════════════════════════════════
            item {
                ProfileHeader(
                    name = uiState.profile.name,
                    username = uiState.profile.username,
                    bio = uiState.profile.bio,
                    avatarUri = uiState.profile.avatarUri,
                    onEdit = onEditProfile,
                )
            }

            // ════════════════════════════════════════════════════════════════
            // 2. ACHIEVEMENT STATS
            // ════════════════════════════════════════════════════════════════
            item { SectionTitle(text = "Pencapaian") }
            item {
                AchievementGrid(
                    totalDecks = uiState.totalDecks,
                    totalCards = uiState.totalCards,
                    totalReviews = uiState.totalReviews,
                    streakDays = uiState.streakDays,
                )
            }

            // ════════════════════════════════════════════════════════════════
            // 3. SETTINGS
            // ════════════════════════════════════════════════════════════════
            item { SectionTitle(text = "Pengaturan") }
            item {
                ThemeModeSelector(
                    currentMode = uiState.themeMode,
                    onModeChange = viewModel::setThemeMode,
                )
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Notifications,
                    label = "Notifikasi",
                    subtitle = "Reminder belajar harian",
                    trailing = "Coming Sprint 4",
                    onClick = { /* no-op Sprint 2 */ },
                )
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Language,
                    label = "Bahasa",
                    subtitle = "Bahasa Indonesia",
                    trailing = "ID",
                    onClick = { /* no-op Sprint 2 */ },
                )
            }

            // ════════════════════════════════════════════════════════════════
            // 4. DATA MANAGEMENT
            // ════════════════════════════════════════════════════════════════
            item { SectionTitle(text = "Data") }
            item {
                SettingRow(
                    icon = Icons.Outlined.DeleteForever,
                    label = "Reset Semua Data",
                    subtitle = "Hapus profil & pengaturan",
                    isDestructive = true,
                    onClick = { showResetConfirm = true },
                )
            }

            // ════════════════════════════════════════════════════════════════
            // 5. ABOUT
            // ════════════════════════════════════════════════════════════════
            item { SectionTitle(text = "Tentang") }
            item {
                SettingRow(
                    icon = Icons.Outlined.Info,
                    label = "Tentang NeuroDeck",
                    subtitle = "Versi, tim, repositori",
                    onClick = onAbout,
                )
            }

            // Bottom spacer untuk hindari nempel bottom nav
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Snackbar untuk feedback action (reset success, error, dll)
        uiState.snackbarMessage?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3000)
                viewModel.consumeSnackbar()
            }
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            ) {
                Text(text = msg)
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // RESET CONFIRMATION DIALOGS (double-confirm)
    // ════════════════════════════════════════════════════════════════════════
    // Pattern Material 3 untuk destructive action:
    //   1st dialog: "Yakin reset?"
    //   2nd dialog: "Beneran beneran yakin? Tidak bisa di-undo"
    // Mencegah accidental tap.

    if (showResetConfirm) {
        ConfirmDialog(
            title = "Reset Semua Data?",
            message = "Semua profil dan pengaturan akan dihapus. Data deck & kartu masih aman.\n\nTindakan ini tidak bisa di-undo.",
            confirmLabel = "Lanjutkan",
            isDestructive = true,
            onConfirm = {
                showResetConfirm = false
                showFinalResetConfirm = true
            },
            onDismiss = { showResetConfirm = false },
        )
    }

    if (showFinalResetConfirm) {
        ConfirmDialog(
            title = "Konfirmasi Terakhir",
            message = "Ketik dalam hati: 'Saya yakin.'\n\nLalu tap RESET untuk konfirmasi.",
            confirmLabel = "RESET",
            isDestructive = true,
            onConfirm = {
                showFinalResetConfirm = false
                viewModel.resetAllData()
            },
            onDismiss = { showFinalResetConfirm = false },
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PRIVATE COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileHeader(
    name: String,
    username: String,
    bio: String,
    avatarUri: String?,
    onEdit: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar — render foto user kalau ada, fallback ke icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Foto profil",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit profil",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            if (bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun AchievementGrid(
    totalDecks: Int,
    totalCards: Int,
    totalReviews: Int,
    streakDays: Int,
) {
    // 2x2 grid pakai 2 Row supaya tidak butuh dependency LazyVerticalGrid
    // (yang punya issue di KMP versi lama).
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AchievementCard(
                value = totalDecks.toString(),
                label = "Total Decks",
                modifier = Modifier.weight(1f),
            )
            AchievementCard(
                value = totalCards.toString(),
                label = "Total Kartu",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AchievementCard(
                value = totalReviews.toString(),
                label = "Total Review",
                modifier = Modifier.weight(1f),
            )
            AchievementCard(
                value = "$streakDays hari",
                label = "Streak",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AchievementCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ThemeModeSelector(
    currentMode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Mode Tampilan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChip(
                    label = "Light",
                    icon = Icons.Outlined.LightMode,
                    selected = currentMode == ThemeMode.Light,
                    onClick = { onModeChange(ThemeMode.Light) },
                )
                ThemeChip(
                    label = "Dark",
                    icon = Icons.Outlined.DarkMode,
                    selected = currentMode == ThemeMode.Dark,
                    onClick = { onModeChange(ThemeMode.Dark) },
                )
                ThemeChip(
                    label = "System",
                    icon = Icons.Outlined.SettingsBrightness,
                    selected = currentMode == ThemeMode.System,
                    onClick = { onModeChange(ThemeMode.System) },
                )
            }
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        },
    )
}

/**
 * Generic setting row dengan icon kiri + label + subtitle + trailing (text atau arrow).
 *
 * @param isDestructive Tint icon & label dengan error color (untuk Reset action).
 */
@Composable
private fun SettingRow(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: String? = null,
    isDestructive: Boolean = false,
) {
    val tint = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    val labelColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = labelColor,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (trailing != null) {
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}