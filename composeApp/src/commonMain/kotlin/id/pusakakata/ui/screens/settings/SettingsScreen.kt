package id.pusakakata.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Aplikasi", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            ListItem(
                headlineContent = { Text("Tentang Pusaka Kata") },
                supportingContent = { Text("Informasi versi dan pengembang") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToAbout() }
            )

            HorizontalDivider()
            
            Text("Preferensi (Segera Hadir)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            ListItem(
                headlineContent = { Text("Tema Gelap") },
                trailingContent = { Switch(checked = false, onCheckedChange = {}) }
            )
        }
    }
}
