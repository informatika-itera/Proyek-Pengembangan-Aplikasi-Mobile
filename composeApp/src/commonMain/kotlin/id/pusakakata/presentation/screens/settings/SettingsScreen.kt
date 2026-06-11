package id.pusakakata.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
) {
    val currentTheme by viewModel.theme.collectAsState()
    var showThemeDialog by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PENGATURAN", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Aplikasi", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            
            ListItem(
                headlineContent = { Text("Tentang Pusaka Kata") },
                supportingContent = { Text("Informasi versi dan pengembang") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToAbout() }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp).alpha(0.1f))
            
            Text("Tampilan", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            
            ListItem(
                headlineContent = { Text("Tema Aplikasi") },
                supportingContent = { 
                    val themeText = when(currentTheme) {
                        AppTheme.SYSTEM -> "Ikuti Sistem"
                        AppTheme.LIGHT -> "Terang"
                        AppTheme.DARK -> "Gelap"
                    }
                    Text(themeText)
                },
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                modifier = Modifier.clickable { showThemeDialog = true }
            )
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Pilih Tema", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    ThemeOption(
                        label = "Ikuti Sistem",
                        selected = currentTheme == AppTheme.SYSTEM,
                        onClick = { 
                            viewModel.setTheme(AppTheme.SYSTEM)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        label = "Terang",
                        selected = currentTheme == AppTheme.LIGHT,
                        onClick = { 
                            viewModel.setTheme(AppTheme.LIGHT)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        label = "Gelap",
                        selected = currentTheme == AppTheme.DARK,
                        onClick = { 
                            viewModel.setTheme(AppTheme.DARK)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        RadioButton(selected = selected, onClick = null)
    }
}
