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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.presentation.components.GlassCard
import com.soundletter.app.presentation.theme.SoundLetterColors
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
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(SoundLetterColors.getBackgroundGradient(isDarkMode)))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section Appearance
                Column {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                    "Dark Mode",
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
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.clearLocalHistory() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = clearStatus !is UiState.Loading
                        ) {
                            if (clearStatus is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onError,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hapus Riwayat Lokal")
                            }
                        }
                    }
                }

                // Section About Developer
                Column {
                    Text(
                        text = "About Developer",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(
                                text = "Dibuat oleh: Gian Ivander, Muhammad Dzaky & Atalie Salsabila",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Teknik Informatika, Institut Teknologi Sumatera",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkMode) Color.White.copy(alpha = 0.7f) else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
