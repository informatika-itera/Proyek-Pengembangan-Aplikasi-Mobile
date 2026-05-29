package com.example.fitkos.presentation.screens.settings

import androidx.compose.foundation.layout.*
import com.example.fitkos.presentation.components.FitKosTopBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showNameDialog by remember { mutableStateOf(false) }
    var showWaterTargetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            FitKosTopBar(
                title = "Pengaturan"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Nama Pengguna",
                subtitle = state.userName,
                onClick = { showNameDialog = true }
            )

            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Target Minum Harian",
                subtitle = "${state.waterTarget} gelas",
                onClick = { showWaterTargetDialog = true }
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Mode Gelap", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text("Gunakan tema gelap untuk aplikasi", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = state.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            }

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Tentang FitKos",
                subtitle = "Versi 1.0.0"
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showNameDialog) {
        var nameText by remember { mutableStateOf(state.userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Ubah Nama") },
            text = {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Nama") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateUserName(nameText)
                    showNameDialog = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showWaterTargetDialog) {
        var targetText by remember { mutableStateOf(state.waterTarget.toString()) }
        AlertDialog(
            onDismissRequest = { showWaterTargetDialog = false },
            title = { Text("Target Minum Harian") },
            text = {
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) targetText = it },
                    label = { Text("Jumlah Gelas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    targetText.toIntOrNull()?.let { viewModel.updateWaterTarget(it) }
                    showWaterTargetDialog = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWaterTargetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
