package com.soundletter.app.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.presentation.components.GlassCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val clearStatus by viewModel.clearHistoryStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val backgroundColor = if (isDarkMode) Color(0xFF000000) else Color(0xFFF0F8FF)
    val primaryColor = if (isDarkMode) Color.White else Color(0xFF007ACC)

    LaunchedEffect(clearStatus) {
        when (val status = clearStatus) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Riwayat lokal berhasil dihapus")
                viewModel.resetStatus()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar("Gagal: ${status.message}")
                viewModel.resetStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Pengaturan", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = primaryColor 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Section Appearance
            Column {
                Text(
                    text = "Tampilan",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                GlassCard(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Palette, 
                                contentDescription = null,
                                tint = if (isDarkMode) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Mode Gelap",
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode(it) }
                        )
                    }
                }
            }

            // Section Data
            Column {
                Text(
                    text = "Data",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                GlassCard(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { viewModel.clearLocalHistory() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = clearStatus !is UiState.Loading
                        ) {
                            if (clearStatus is UiState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hapus Riwayat Pesan")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer About
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SoundLetter v1.0",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.4f) else Color.Gray
                )
                Text(
                    text = "Institut Teknologi Sumatera",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.3f) else Color.LightGray
                )
            }
        }
    }
}
