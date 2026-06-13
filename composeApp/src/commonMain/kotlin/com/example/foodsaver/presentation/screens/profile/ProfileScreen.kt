package com.example.foodsaver.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.data.local.datastore.ThemeMode
import com.example.foodsaver.presentation.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.testTag("profile_screen"),
        topBar = {
            ProfileTopBar(onBackClick = onNavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            UserProfileCard()

            Spacer(modifier = Modifier.height(24.dp))

            InventoryStatsCard(state)

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(
                themeMode = state.themeMode,
                onThemeClick = { showThemeDialog = true },
                onNotificationClick = { showNotificationDialog = true }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showThemeDialog) {
        ThemeDialog(
            currentMode = state.themeMode,
            onDismiss = { showThemeDialog = false },
            onSelectMode = { 
                viewModel.setThemeMode(it)
                showThemeDialog = false
                scope.launch { snackbarHostState.showSnackbar("Tema aplikasi berhasil diubah") }
            }
        )
    }

    if (showNotificationDialog) {
        NotificationDialog(
            enabled = state.notificationsEnabled,
            reminderDays = state.reminderDays,
            onDismiss = { showNotificationDialog = false },
            onToggleEnabled = { viewModel.setNotificationsEnabled(it) },
            onSelectDays = { 
                viewModel.setReminderDays(it)
                scope.launch { snackbarHostState.showSnackbar("Pengaturan notifikasi diperbarui") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.testTag("profile_header"),
        title = { 
            Column {
                Text("Profil & Pengaturan", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("Atur preferensi FoodSaver kamu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun UserProfileCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("profile_user_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(32.dp), 
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    "FoodSaver User", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "user@foodsaver.id", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.alpha(0.8f)
                ) {
                    Text(
                        "Belum Login", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        Text(
            "Login belum tersedia pada versi ini. Fitur login akan tersedia pada pengembangan berikutnya.",
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun InventoryStatsCard(state: ProfileUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("profile_stats_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Statistik Inventory", 
                fontWeight = FontWeight.Bold, 
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(
                    label = "Aktif", 
                    value = state.totalItems.toString(), 
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).testTag("profile_active_count")
                )
                StatItem(
                    label = "Aman", 
                    value = state.safeCount.toString(), 
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f).testTag("profile_safe_count")
                )
                StatItem(
                    label = "Segera Masak", 
                    value = state.nearlyExpiredCount.toString(), 
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f).testTag("profile_expiring_count")
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(
                    label = "Expired", 
                    value = state.expiredCount.toString(), 
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f).testTag("profile_expired_count")
                )
                StatItem(
                    label = "Dikonsumsi", 
                    value = state.consumedCount.toString(), 
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Dibuang", 
                    value = state.discardedCount.toString(), 
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            value, 
            fontWeight = FontWeight.Black, 
            fontSize = 22.sp, 
            color = color,
            textAlign = TextAlign.Center
        )
        Text(
            label, 
            style = MaterialTheme.typography.labelSmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SettingsSection(
    themeMode: ThemeMode,
    onThemeClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Pengaturan", 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(bottom = 12.dp, start = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        ProfileSettingItem(
            icon = Icons.Outlined.Notifications, 
            title = "Notifikasi & Reminder", 
            subtitle = "Atur pengingat kedaluwarsa makanan",
            onClick = onNotificationClick,
            testTag = "notification_setting_button"
        )
        ProfileSettingItem(
            icon = Icons.Outlined.Palette, 
            title = "Tema Aplikasi", 
            subtitle = when(themeMode) {
                ThemeMode.LIGHT -> "Terang"
                ThemeMode.DARK -> "Gelap"
                ThemeMode.SYSTEM -> "Ikuti Sistem"
            },
            onClick = onThemeClick,
            testTag = "theme_setting_button"
        )
    }
}

@Composable
fun ProfileSettingItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit, testTag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun ThemeDialog(currentMode: ThemeMode, onDismiss: () -> Unit, onSelectMode: (ThemeMode) -> Unit) {
    AlertDialog(
        modifier = Modifier.testTag("theme_dialog"),
        onDismissRequest = onDismiss,
        title = { Text("Pilih Tema", fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.selectableGroup()) {
                ThemeOption("Terang", currentMode == ThemeMode.LIGHT) { onSelectMode(ThemeMode.LIGHT) }
                ThemeOption("Gelap", currentMode == ThemeMode.DARK) { onSelectMode(ThemeMode.DARK) }
                ThemeOption("Ikuti Sistem", currentMode == ThemeMode.SYSTEM) { onSelectMode(ThemeMode.SYSTEM) }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun ThemeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
fun NotificationDialog(enabled: Boolean, reminderDays: Int, onDismiss: () -> Unit, onToggleEnabled: (Boolean) -> Unit, onSelectDays: (Int) -> Unit) {
    AlertDialog(
        modifier = Modifier.testTag("notification_dialog"),
        onDismissRequest = onDismiss,
        title = { Text("Notifikasi & Pengingat", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Notifikasi Kedaluwarsa", fontWeight = FontWeight.Medium)
                        Text("Dapatkan pengingat stok makanan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = enabled, onCheckedChange = onToggleEnabled)
                }
                
                if (enabled) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Ingatkan saya pada:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(Modifier.selectableGroup()) {
                        ReminderOption("Hari H Kedaluwarsa", reminderDays == 0) { onSelectDays(0) }
                        ReminderOption("1 Hari Sebelum", reminderDays == 1) { onSelectDays(1) }
                        ReminderOption("3 Hari Sebelum", reminderDays == 3) { onSelectDays(3) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Waktu pengingat default: 08:00", 
                        style = MaterialTheme.typography.bodySmall, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Selesai") } }
    )
}

@Composable
fun ReminderOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 12.dp))
    }
}
