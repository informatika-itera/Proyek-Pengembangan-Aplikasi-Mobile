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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.data.local.datastore.ThemeMode
import com.example.foodsaver.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // User Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(50.dp), 
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "FoodSaver User", 
                style = MaterialTheme.typography.headlineSmall, 
                fontWeight = FontWeight.ExtraBold, 
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "user@foodsaver.id", 
                style = MaterialTheme.typography.bodyMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Belum Login", 
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Statistik Inventory", 
                        fontWeight = FontWeight.ExtraBold, 
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("Aktif", state.totalItems.toString(), MaterialTheme.colorScheme.onSurface)
                        StatItem("Aman", state.safeCount.toString(), MaterialTheme.colorScheme.primary)
                        StatItem("Hampir", state.nearlyExpiredCount.toString(), MaterialTheme.colorScheme.secondary)
                        StatItem("Expired", state.expiredCount.toString(), MaterialTheme.colorScheme.error)
                    }
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp), 
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Total Dikonsumsi", 
                                style = MaterialTheme.typography.bodyMedium, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text("${state.consumedCount} Makanan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Total Dibuang", 
                                style = MaterialTheme.typography.bodyMedium, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text("${state.discardedCount} Makanan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "Pengaturan", 
                    fontWeight = FontWeight.ExtraBold, 
                    color = MaterialTheme.colorScheme.onBackground, 
                    modifier = Modifier.padding(bottom = 12.dp, start = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                
                SettingsItem(
                    icon = Icons.Outlined.Notifications, 
                    title = "Notifikasi & Reminder", 
                    subtitle = if (state.notificationsEnabled) "Aktif (${state.reminderDays} hari sebelum)" else "Nonaktif",
                    onClick = { showNotificationDialog = true }
                )
                SettingsItem(
                    icon = Icons.Outlined.Palette, 
                    title = "Tema Aplikasi", 
                    subtitle = when(state.themeMode) {
                        ThemeMode.LIGHT -> "Terang"
                        ThemeMode.DARK -> "Gelap"
                        ThemeMode.SYSTEM -> "Ikuti Sistem"
                    },
                    onClick = { showThemeDialog = true }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = { /* Placeholder */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Login / Sign In", fontWeight = FontWeight.ExtraBold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = state.themeMode,
            onDismiss = { showThemeDialog = false },
            onSelectMode = { 
                viewModel.setThemeMode(it)
                showThemeDialog = false
            }
        )
    }

    if (showNotificationDialog) {
        NotificationSettingsDialog(
            enabled = state.notificationsEnabled,
            reminderDays = state.reminderDays,
            onDismiss = { showNotificationDialog = false },
            onToggleEnabled = { viewModel.setNotificationsEnabled(it) },
            onSelectDays = { viewModel.setReminderDays(it) }
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentMode: ThemeMode,
    onDismiss: () -> Unit,
    onSelectMode: (ThemeMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Tema", fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.selectableGroup()) {
                ThemeOption("Terang", ThemeMode.LIGHT, currentMode == ThemeMode.LIGHT) { onSelectMode(ThemeMode.LIGHT) }
                ThemeOption("Gelap", ThemeMode.DARK, currentMode == ThemeMode.DARK) { onSelectMode(ThemeMode.DARK) }
                ThemeOption("Ikuti Sistem", ThemeMode.SYSTEM, currentMode == ThemeMode.SYSTEM) { onSelectMode(ThemeMode.SYSTEM) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup", fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
fun ThemeOption(text: String, mode: ThemeMode, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp), fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun NotificationSettingsDialog(
    enabled: Boolean,
    reminderDays: Int,
    onDismiss: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onSelectDays: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notifikasi & Reminder", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Aktifkan Notifikasi", fontWeight = FontWeight.Medium)
                    Switch(checked = enabled, onCheckedChange = onToggleEnabled)
                }
                
                if (enabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ingatkan pada:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Column(Modifier.selectableGroup().padding(top = 8.dp)) {
                        ReminderOption("Hari H Kedaluwarsa", 0, reminderDays == 0) { onSelectDays(0) }
                        ReminderOption("1 Hari Sebelum", 1, reminderDays == 1) { onSelectDays(1) }
                        ReminderOption("3 Hari Sebelum", 3, reminderDays == 3) { onSelectDays(3) }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Selesai", fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
fun ReminderOption(text: String, days: Int, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 12.dp), fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}
