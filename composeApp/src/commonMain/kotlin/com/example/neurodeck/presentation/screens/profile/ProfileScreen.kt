package com.example.neurodeck.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.presentation.components.ConfirmDialog
import com.example.neurodeck.presentation.components.LoadingIndicator
import com.example.neurodeck.presentation.components.SectionTitle
import com.example.neurodeck.presentation.theme.NeurodeckTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * @param onEditProfile  Navigate ke EditProfileScreen.
 * @param onAbout        Navigate ke AboutScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onAbout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetConfirm by remember { mutableStateOf(false) }
    var showFinalResetConfirm by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe snackbarMessage dari ViewModel (ganti tema, reset, error)
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    // Tidak pakai Scaffold — TopAppBar sudah dihandle AppNavHost (main tab)
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1. HEADER
            item {
                ProfileHeader(
                    name = uiState.profile.name,
                    username = uiState.profile.username,
                    bio = uiState.profile.bio,
                    avatarUri = uiState.profile.avatarUri,
                    onEdit = onEditProfile,
                )
            }

            // 2. ACHIEVEMENT STATS
            item { SectionTitle(text = "Pencapaian") }
            item {
                AchievementGrid(
                    totalDecks = uiState.totalDecks,
                    totalCards = uiState.totalCards,
                    totalReviews = uiState.totalReviews,
                    streakDays = uiState.streakDays,
                )
            }

            // 3. SETTINGS
            item { SectionTitle(text = "Pengaturan") }
            item {
                ThemeModeSelector(
                    currentMode = uiState.themeMode,
                    onModeChange = viewModel::setThemeMode,
                )
            }
            item {
                ReminderRow(
                    settings = uiState.reminderSettings,
                    onToggle = { enabled ->
                        viewModel.setReminder(
                            enabled = enabled,
                            hour = uiState.reminderSettings.hour,
                            minute = uiState.reminderSettings.minute,
                        )
                    },
                    onClickTime = { showTimePicker = true },
                )
            }
            item {
                SettingRow(
                    icon = Icons.Outlined.Language,
                    label = "Bahasa",
                    subtitle = "Bahasa Indonesia",
                    trailing = "ID",
                    onClick = { },
                )
            }

            // 4. DATA MANAGEMENT
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

            // 5. ABOUT
            item { SectionTitle(text = "Tentang") }
            item {
                SettingRow(
                    icon = Icons.Outlined.Info,
                    label = "Tentang NeuroDeck",
                    subtitle = "Versi, tim, repositori",
                    onClick = onAbout,
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // SNACKBAR — manual di pojok bawah (tidak pakai Scaffold supaya tidak double padding)
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }

    // RESET CONFIRMATION DIALOGS (double-confirm)
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

    // TIME PICKER DIALOG — atur jam reminder
    if (showTimePicker) {
        ReminderTimePickerDialog(
            initialHour = uiState.reminderSettings.hour,
            initialMinute = uiState.reminderSettings.minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                showTimePicker = false
                viewModel.setReminder(
                    enabled = true, // memilih jam = otomatis mengaktifkan
                    hour = hour,
                    minute = minute,
                )
            },
        )
    }
}

// ── Private components ────────────────────────────────────────────────────────

/**
 * Baris pengaturan reminder belajar harian: ikon + label + jam + switch.
 * Tap area jam → buka time picker. Switch → aktif/nonaktif.
 */
@Composable
private fun ReminderRow(
    settings: com.example.neurodeck.domain.model.ReminderSettings,
    onToggle: (Boolean) -> Unit,
    onClickTime: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = settings.enabled, onClick = onClickTime),
            ) {
                Text(
                    text = "Pengingat Belajar Harian",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (settings.enabled) {
                        "Setiap hari jam ${settings.formatted} · ketuk untuk ubah"
                    } else {
                        "Nonaktif"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = settings.enabled,
                onCheckedChange = onToggle,
            )
        }
    }
}

/**
 * Dialog Material 3 TimePicker untuk memilih jam reminder.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
) {
    val timeState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Pilih Jam Pengingat",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(20.dp))
                TimePicker(state = timeState)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onConfirm(timeState.hour, timeState.minute) },
                    ) { Text("Simpan") }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    username: String,
    bio: String,
    avatarUri: String?,
    onEdit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(NeurodeckTheme.extras.heroBrush)
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Foto profil",
                            modifier = Modifier.size(72.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color.White,
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
                        color = Color.White,
                    )
                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit profil",
                        tint = Color.White,
                    )
                }
            }
            if (bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
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
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AchievementCard("$totalDecks", "Total Decks", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            AchievementCard("$totalCards", "Total Kartu", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AchievementCard("$totalReviews", "Total Review", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
            AchievementCard("$streakDays hari", "Streak", NeurodeckTheme.extras.streakIcon, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AchievementCard(
    value: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = accentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ThemeModeSelector(currentMode: ThemeMode, onModeChange: (ThemeMode) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DarkMode, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Mode Tampilan", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChip("Light", Icons.Outlined.LightMode, currentMode == ThemeMode.Light) { onModeChange(ThemeMode.Light) }
                ThemeChip("Dark", Icons.Outlined.DarkMode, currentMode == ThemeMode.Dark) { onModeChange(ThemeMode.Dark) }
                ThemeChip("System", Icons.Outlined.SettingsBrightness, currentMode == ThemeMode.System) { onModeChange(ThemeMode.System) }
            }
        }
    }
}

@Composable
private fun ThemeChip(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
    )
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: String? = null,
    isDestructive: Boolean = false,
) {
    val tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val labelColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleMedium, color = labelColor)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (trailing != null) {
                Text(trailing, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }
    }
}