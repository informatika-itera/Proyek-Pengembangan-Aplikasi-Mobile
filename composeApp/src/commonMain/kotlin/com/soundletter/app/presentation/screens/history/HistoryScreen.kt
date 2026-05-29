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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.presentation.components.EmptyStateView
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.home.MessageCard
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import com.soundletter.app.presentation.theme.SoundLetterColors
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

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Sent History",
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
            when (val state = uiState) {
                is UiState.Loading -> {
                    LoadingView()
                }
                is UiState.Success -> {
                    val letters = state.data
                    if (letters.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Inbox,
                            title = "Riwayat Kosong",
                            description = "Anda belum mengirimkan surat musik apa pun."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(letters, key = { it.id }) { note: Note ->
                                Box(modifier = Modifier.animateItem()) {
                                    MessageCard(
                                        message = note,
                                        isDarkMode = isDarkMode,
                                        onClick = { onNavigateToDetail(note.id.toString()) }
                                    )
                                }
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
