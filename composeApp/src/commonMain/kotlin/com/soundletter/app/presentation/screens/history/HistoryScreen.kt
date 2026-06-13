package com.soundletter.app.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.presentation.components.EmptyStateView
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.home.MessageCard
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: HistoryScreenViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.historyState.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Adaptive colors for background and text
    val backgroundColor = if (isDarkMode) Color(0xFF000000) else Color(0xFFF0F8FF)
    val primaryColor = if (isDarkMode) Color.White else Color(0xFF007ACC)

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Riwayat Terkirim",
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
                // Ensure AppBar is transparent to show adaptive background
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingView()
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Inbox,
                            title = "Riwayat Kosong",
                            description = "Anda belum mengirimkan surat musik apa pun."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.data, key = { it.id }) { note ->
                                MessageCard(
                                    message = note,
                                    isDarkMode = isDarkMode,
                                    onClick = { onNavigateToDetail(note.id.toString()) }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    LaunchedEffect(state.message) {
                        snackbarHostState.showSnackbar("Gagal memuat riwayat: ${state.message}")
                    }
                }
                else -> {}
            }
        }
    }
}
