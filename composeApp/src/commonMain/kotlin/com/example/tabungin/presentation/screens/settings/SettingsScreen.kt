package com.example.tabungin.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier            = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SectionLabel("Profil")
            OutlinedTextField(
                value         = uiState.namaUser,
                onValueChange = viewModel::onNamaUserChange,
                label         = { Text("Nama") },
                leadingIcon   = { Icon(Icons.Default.Person, null) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()

            SectionLabel("Tampilan")
            SettingsToggleRow(
                icon     = Icons.Default.DarkMode,
                label    = "Mode Gelap",
                subtitle = "Aktifkan tema gelap",
                checked  = uiState.isDarkMode,
                onToggle = viewModel::toggleDarkMode
            )
            HorizontalDivider()

            SectionLabel("Notifikasi")
            SettingsToggleRow(
                icon     = Icons.Default.Notifications,
                label    = "Pengingat Menabung",
                subtitle = "Notifikasi harian untuk menabung",
                checked  = uiState.notifikasiAktif,
                onToggle = viewModel::toggleNotifikasi
            )
            HorizontalDivider()

            SectionLabel("Tentang Aplikasi")
            ListItem(
                headlineContent   = { Text("TabungIn") },
                supportingContent = { Text("Versi 1.0.0 · Sprint 2") },
                leadingContent    = { Text("🏦", style = MaterialTheme.typography.titleLarge) }
            )
            ListItem(
                headlineContent   = { Text("Dikembangkan untuk") },
                supportingContent = { Text("Pengembangan Aplikasi Mobile · ITERA") },
                leadingContent    = { Icon(Icons.Default.School, null) }
            )
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text     = label,
        style    = MaterialTheme.typography.labelLarge,
        color    = MaterialTheme.colorScheme.primary,
        modifier = androidx.compose.ui.Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    ListItem(
        headlineContent   = { Text(label) },
        supportingContent = { Text(subtitle) },
        leadingContent    = { Icon(icon, null) },
        trailingContent   = { Switch(checked = checked, onCheckedChange = { onToggle() }) }
    )
}