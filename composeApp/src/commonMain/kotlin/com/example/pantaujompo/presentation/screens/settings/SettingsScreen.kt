package com.example.pantaujompo.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.presentation.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.example.pantaujompo.core.util.AppStrings

/**
 * Layar Pengaturan aplikasi Pantau Jompo.
 * Berisi pengaturan tampilan, bahasa, satuan, dan privasi.
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    userPreferences: UserPreferences = koinInject()
) {
    // Ambil semua preferensi dari DataStore
    val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = true)
    val textScale by userPreferences.textSizeScale.collectAsState(initial = 1.0f)
    val language by userPreferences.language.collectAsState(initial = "id")
    val coroutineScope = rememberCoroutineScope()

    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDark) Color(0xFF151515) else MaterialTheme.colorScheme.surface

    // State dialog
    var showClearDialog by remember { mutableStateOf(false) }

    // Helper teks berdasarkan bahasa
    fun str(key: String) = AppStrings.get(key, language)

    val uriHandler = LocalUriHandler.current

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ======== HEADER ========
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(surfaceColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = textPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(str("pengaturan"), color = textPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Text(str("sesuaikan_preferensi"), color = textSecondary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ======== SEKSI TAMPILAN ========
            SettingsGroupHeader(str("tampilan"), Icons.Default.Palette, accentColor, textSecondary)
            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Mode Gelap/Terang
            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                title = str("mode_gelap"),
                subtitle = if (isDarkMode) str("gelap_aktif")
                           else str("gelap_nonaktif"),
                checked = isDarkMode,
                onCheckedChange = { coroutineScope.launch { userPreferences.setDarkMode(it) } },
                accentColor = accentColor, surfaceColor = surfaceColor,
                textPrimary = textPrimary, textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Ukuran Teks Besar/Normal
            SettingsSwitchItem(
                icon = Icons.Default.FormatSize,
                title = str("teks_besar"),
                subtitle = if (textScale > 1.0f) str("teks_aktif")
                           else str("teks_nonaktif"),
                checked = textScale > 1.0f,
                onCheckedChange = { isLarge ->
                    coroutineScope.launch { userPreferences.setTextSizeScale(if (isLarge) 1.2f else 1.0f) }
                },
                accentColor = Color(0xFF00BCD4), surfaceColor = surfaceColor,
                textPrimary = textPrimary, textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ======== SEKSI BAHASA & SATUAN ========
            SettingsGroupHeader(str("bahasa_satuan"), Icons.Default.Language, Color(0xFF9C27B0), textSecondary)
            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Bahasa Indonesia/English — benar-benar merubah seluruh aplikasi
            SettingsSwitchItem(
                icon = Icons.Default.Language,
                title = str("bahasa_inggris"),
                subtitle = str("bahasa_aktif"),
                checked = language == "en",
                onCheckedChange = { isEng ->
                    coroutineScope.launch { userPreferences.setLanguage(if (isEng) "en" else "id") }
                },
                accentColor = Color(0xFF9C27B0), surfaceColor = surfaceColor,
                textPrimary = textPrimary, textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ======== SEKSI PRIVASI & KEAMANAN ========
            SettingsGroupHeader(str("privasi_keamanan"), Icons.Default.Security, Color(0xFFE91E63), textSecondary)
            Spacer(modifier = Modifier.height(10.dp))

            // Hapus semua data
            SettingsActionItem(
                icon = Icons.Default.DeleteForever,
                title = str("hapus_data"),
                subtitle = str("hapus_data_desc"),
                onClick = { showClearDialog = true },
                accentColor = Color(0xFFFF5252), surfaceColor = surfaceColor,
                textPrimary = textPrimary, textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Kebijakan Privasi
            SettingsActionItem(
                icon = Icons.Default.PrivacyTip,
                title = str("kebijakan_privasi"),
                subtitle = str("kebijakan_desc"),
                onClick = {
                    // Buka browser dengan URL placeholder kebijakan privasi
                    try { uriHandler.openUri("https://pantaujompo.app/privacy") } catch (_: Exception) {}
                },
                accentColor = Color(0xFF00BCD4), surfaceColor = surfaceColor,
                textPrimary = textPrimary, textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ======== SEKSI TENTANG APLIKASI ========
            SettingsGroupHeader(str("tentang_aplikasi"), Icons.Default.Info, textSecondary, textSecondary)
            Spacer(modifier = Modifier.height(10.dp))

            // Tombol tentang aplikasi
            SettingsActionItem(
                icon = Icons.Default.Info,
                title = str("tentang_pantau"),
                subtitle = "Versi 1.0.0 · ${str("dibuat_dengan")}",
                onClick = { onNavigateToAbout() },
                accentColor = accentColor, surfaceColor = surfaceColor,
                textPrimary = textPrimary, textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // ======== DIALOG KONFIRMASI HAPUS DATA ========
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    str("hapus_semua_tanya"),
                    color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    str("hapus_semua_peringatan"),
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            userPreferences.clearAllData()
                            showClearDialog = false
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text(str("hapus_semuanya"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(str("batal"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
        )
    }
}

// ======== KOMPONEN HEADER GRUP PENGATURAN ========
@Composable
fun SettingsGroupHeader(title: String, icon: ImageVector, color: Color, textSecondary: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
    }
}

// ======== KOMPONEN ITEM SWITCH ========
@Composable
fun SettingsSwitchItem(
    icon: ImageVector, title: String, subtitle: String,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit,
    accentColor: Color, surfaceColor: Color, textPrimary: Color, textSecondary: Color
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(surfaceColor, RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(accentColor.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = textSecondary, fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = textSecondary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

// ======== KOMPONEN ITEM AKSI (KLIK) ========
@Composable
fun SettingsActionItem(
    icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit,
    accentColor: Color, surfaceColor: Color, textPrimary: Color, textSecondary: Color
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(surfaceColor, RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(accentColor.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = textSecondary, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = textSecondary, modifier = Modifier.size(20.dp))
        }
    }
}
