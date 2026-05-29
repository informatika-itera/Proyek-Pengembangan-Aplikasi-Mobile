package com.soundletter.app.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.presentation.components.EmptyStateView
import com.soundletter.app.presentation.components.GlassCard
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import com.soundletter.app.presentation.theme.SoundLetterColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToCompose: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    navController: NavController,
    viewModel: HomeScreenViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mengambil status dari ComposeScreen via savedStateHandle
    val composeSuccess by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("compose_success", false)
        ?.collectAsState() ?: mutableStateOf(false)

    val isSynced by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("compose_is_synced", true)
        ?.collectAsState() ?: mutableStateOf(true)

    LaunchedEffect(composeSuccess) {
        if (composeSuccess) {
            val message = if (isSynced) {
                "Surat musik berhasil dilarungkan!"
            } else {
                "Koneksi terputus. Pesan disimpan di riwayat lokal."
            }
            snackbarHostState.showSnackbar(message)
            // Reset agar tidak muncul berulang
            navController.currentBackStackEntry?.savedStateHandle?.set("compose_success", false)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCompose,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Compose", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "SoundLetter",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    )
                )

                when (val state = uiState) {
                    is UiState.Loading -> {
                        LoadingView()
                    }
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            EmptyStateView(
                                icon = Icons.Default.Inbox,
                                title = "Belum Ada Surat",
                                description = "Jadilah yang pertama mengirimkan melodi perasaan!"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.data, key = { it.id }) { letter ->
                                    Box(modifier = Modifier.animateItem()) {
                                        MessageCard(
                                            message = letter,
                                            isDarkMode = isDarkMode,
                                            onClick = { onNavigateToDetail(letter.id.toString()) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        LaunchedEffect(state.message) {
                            snackbarHostState.showSnackbar("Gagal memuat data: ${state.message}")
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun MessageCard(message: Note, isDarkMode: Boolean, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        isDarkMode = isDarkMode
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "To: ${message.recipient}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "From: ${message.sender}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color.Black
                ),
                maxLines = 3
            )
            message.songTitle?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    }
}
