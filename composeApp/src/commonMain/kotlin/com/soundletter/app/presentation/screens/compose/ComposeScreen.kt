package com.soundletter.app.presentation.screens.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import com.soundletter.app.presentation.theme.SoundLetterColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeScreen(
    onNavigateBack: () -> Unit,
    onSuccess: (Boolean) -> Unit = {},
    viewModel: ComposeViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val contentColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
    
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = contentColor,
        unfocusedBorderColor = contentColor.copy(alpha = 0.5f),
        focusedLabelColor = if (isDarkMode) Color.Black.copy(alpha = 0.7f) else contentColor,
        unfocusedLabelColor = if (isDarkMode) Color.Black.copy(alpha = 0.5f) else contentColor.copy(alpha = 0.7f),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.2f),
        unfocusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.2f)
    )

    // Handle UI Events (Snackbar)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ComposeUiEvent.ShowOfflineSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = "Koneksi terputus. Pesan disimpan di riwayat lokal.",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    LaunchedEffect(state.sendStatus) {
        when (val status = state.sendStatus) {
            is UiState.Success -> {
                val isSynced = status.data
                viewModel.resetStatus()
                onSuccess(isSynced)
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar("Gagal mengirim: ${status.message}")
                viewModel.resetStatus()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Compose Letter", color = contentColor) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = contentColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.recipient,
                        onValueChange = { viewModel.onRecipientChange(it) },
                        label = { Text("Untuk") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = state.sender,
                        onValueChange = { viewModel.onSenderChange(it) },
                        label = { Text("Dari (Opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = state.message,
                        onValueChange = { viewModel.onMessageChange(it) },
                        label = { Text("Pesan") },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = textFieldColors
                    )

                    Button(
                        onClick = { viewModel.recommendSongs() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkMode) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f) 
                                             else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        enabled = !state.isAiLoading
                    ) {
                        if (state.isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rekomendasikan Lagu dengan AI")
                        }
                    }

                    if (state.suggestions.isNotEmpty()) {
                        Text("AI Recommendations", style = MaterialTheme.typography.labelMedium, color = contentColor)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.suggestions) { song ->
                                SongSuggestionCard(
                                    song = song, 
                                    isSelected = state.selectedSong == song,
                                    isDarkMode = isDarkMode,
                                    onClick = { viewModel.onSongSelect(song) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { 
                            if (state.recipient.isBlank() || state.message.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Nama dan pesan tidak boleh kosong!") }
                            } else {
                                viewModel.sendSoundLetter()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        enabled = state.sendStatus !is UiState.Loading
                    ) {
                        Text("Kirim Surat", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        if (state.sendStatus is UiState.Loading) {
            LoadingView()
        }
    }
}

@Composable
fun SongSuggestionCard(song: SongSuggestion, isSelected: Boolean, isDarkMode: Boolean, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                             else if (isDarkMode) Color.White.copy(alpha = 0.1f)
                             else Color.White.copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = song.title, 
                fontWeight = FontWeight.Bold, 
                maxLines = 1,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                        else if (isDarkMode) Color.White else Color.Black
            )
            Text(
                text = song.artist, 
                style = MaterialTheme.typography.labelSmall, 
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                        else if (isDarkMode) Color.White.copy(alpha = 0.6f) 
                        else Color.DarkGray
            )
        }
    }
}
