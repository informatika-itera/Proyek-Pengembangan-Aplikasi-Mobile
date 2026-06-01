package com.mywallet.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mywallet.LocalDarkMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit = {}) {
    val darkModeState = LocalDarkMode.current
    var notificationsEnabled by remember { mutableStateOf(true) }

    // State untuk fungsionalitas Klik Menu
    var selectedLanguage by remember { mutableStateOf("Indonesia") }
    var selectedCurrency by remember { mutableStateOf("IDR (Rp)") }

    // State untuk mengontrol kemunculan Dialog/Pop-up
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pengaturan Akun",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECTION TAMPILAN ---
            SettingsSectionTitle("Tampilan")
            SettingsToggleItem(
                icon = Icons.Default.DarkMode,
                title = "Mode Gelap",
                subtitle = if (darkModeState.value) "Tema gelap aktif" else "Tema terang aktif",
                checked = darkModeState.value,
                onCheckedChange = { darkModeState.value = it }
            )

            // --- SECTION NOTIFIKASI ---
            SettingsSectionTitle("Notifikasi")
            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Notifikasi Transaksi",
                subtitle = if (notificationsEnabled) "Terima pemberitahuan setiap ada transaksi" else "Notifikasi dinonaktifkan",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            // --- SECTION AKUN ---
            SettingsSectionTitle("Akun")
            SettingsInfoItem(
                icon = Icons.Default.Language,
                title = "Bahasa",
                subtitle = selectedLanguage,
                onClick = { showLanguageDialog = true }
            )
            SettingsInfoItem(
                icon = Icons.Default.AttachMoney,
                title = "Mata Uang",
                subtitle = selectedCurrency,
                onClick = { showCurrencyDialog = true }
            )

            // --- SECTION TENTANG ---
            SettingsSectionTitle("Tentang")
            SettingsInfoItem(
                icon = Icons.Default.Info,
                title = "Versi Aplikasi",
                subtitle = "1.0.0",
                onClick = { showVersionDialog = true }
            )
            SettingsInfoItem(
                icon = Icons.Default.Code,
                title = "Developer",
                subtitle = "Tim MyWallet - ITERA 2025",
                onClick = { showDeveloperDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // =================================================================
    // DIALOG POP-UP UNTUK MASING-MASING MENU YANG DIKLIK
    // =================================================================

    // 1. Dialog Pilihan Bahasa
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Pilih Bahasa", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    val languages = listOf("Indonesia", "English (US)", "Melayu")
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (selectedLanguage == lang), onClick = {
                                selectedLanguage = lang
                                showLanguageDialog = false
                            })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Batal") }
            }
        )
    }

    // 2. Dialog Pilihan Mata Uang
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Pilih Mata Uang", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    val currencies = listOf("IDR (Rp)", "USD ($)", "EUR (€)", "SGD ($)")
                    currencies.forEach { curr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCurrency = curr
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (selectedCurrency == curr), onClick = {
                                selectedCurrency = curr
                                showCurrencyDialog = false
                            })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(curr, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Batal") }
            }
        )
    }

    // 3. Dialog Info Versi Aplikasi
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Versi Aplikasi", fontWeight = FontWeight.Bold) },
            text = { Text("Aplikasi MyWallet Anda sudah menggunakan versi terbaru (1.0.0). Tidak ada pembaruan yang tersedia saat ini.") },
            confirmButton = {
                Button(onClick = { showVersionDialog = false }) { Text("Oke") }
            }
        )
    }

    // 4. Dialog Info Anggota Tim Developer (Sesuai Data Kelompok Anda)
    if (showDeveloperDialog) {
        AlertDialog(
            onDismissRequest = { showDeveloperDialog = false },
            title = { Text("Tim Pengembang", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Aplikasi ini dikembangkan untuk memenuhi tugas mata kuliah Pengembangan Aplikasi Mobile (RA) ITERA.", style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider()
                    Column {
                        Text("• Hanifah Hasanah", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("  NIM: 123140082", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column {
                        Text("• Zahwa Natasya Hamzah", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("  NIM: 123140069", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDeveloperDialog = false }) { Text("Tutup") }
            }
        )
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun SettingsInfoItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Membuat Card bisa merespon sentuhan klik
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
        }
    }
}