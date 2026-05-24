package com.example.musickeep.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var tempName by remember(uiState.userName) { mutableStateOf(uiState.userName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan & Statistik") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ringkasan Statistik (Enhanced Feature)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Statistik Koleksi", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Lagu", style = MaterialTheme.typography.labelMedium)
                            Text("${uiState.totalSongs}", style = MaterialTheme.typography.headlineMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Genre Terbanyak", style = MaterialTheme.typography.labelMedium)
                            Text(uiState.favoriteGenre, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tombol Share (Bonus Feature)
                    Button(
                        onClick = { /* Fitur Share akan memanggil intent native */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan Progres Musikku")
                    }
                }
            }

            // Edit Profil
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Profil Pengguna", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nama Kamu") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { viewModel.updateUserName(tempName) },
                        modifier = Modifier.align(Alignment.End),
                        enabled = tempName != uiState.userName && tempName.isNotBlank()
                    ) {
                        Text("Simpan")
                    }
                }
            }

            // Theme Switcher
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mode Gelap", style = MaterialTheme.typography.titleMedium)
                    Switch(checked = uiState.isDarkMode, onCheckedChange = { viewModel.toggleDarkMode(it) })
                }
            }
        }
    }
}
