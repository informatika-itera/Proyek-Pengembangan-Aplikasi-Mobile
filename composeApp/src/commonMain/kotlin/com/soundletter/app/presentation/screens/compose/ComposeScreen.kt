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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
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

    val backgroundColor = if (isDarkMode) Color(0xFF000000) else Color(0xFFF0F8FF)
    val primaryColor = if (isDarkMode) Color.White else Color(0xFF007ACC)
    
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = primaryColor.copy(alpha = 0.3f),
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = Color.Gray,
        focusedTextColor = if (isDarkMode) Color.White else Color.Black,
        unfocusedTextColor = if (isDarkMode) Color.White else Color.Black,
        focusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.White,
        unfocusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.White
    )

    // Handle UI Events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ComposeUiEvent.ShowOfflineSnackbar -> {
                    snackbarHostState.showSnackbar("Koneksi terputus. Pesan disimpan secara lokal.")
                }
                is ComposeUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
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
                title = { Text("Tulis Surat", fontWeight = FontWeight.Bold, color = primaryColor) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryColor)
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
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = state.recipient,
                    onValueChange = { viewModel.onRecipientChange(it) },
                    label = { Text("Untuk") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.sender,
                    onValueChange = { viewModel.onSenderChange(it) },
                    label = { Text("Dari (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.message,
                    onValueChange = { viewModel.onMessageChange(it) },
                    label = { Text("Isi Pesan") },
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    colors = textFieldColors
                )

                Button(
                    onClick = { viewModel.recommendSongs() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = !state.isAiLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (state.isAiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dapatkan Saran Lagu AI")
                    }
                }

                if (state.suggestions.isNotEmpty()) {
                    Text("Pilih Rekomendasi:", style = MaterialTheme.typography.labelLarge, color = primaryColor, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().height(85.dp)
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
            }

            // Tombol Kirim Fix di bawah
            Button(
                onClick = { viewModel.sendSoundLetter() },
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = MaterialTheme.shapes.large,
                enabled = state.sendStatus !is UiState.Loading
            ) {
                Text("Kirim Surat Musik", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
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
            containerColor = if (isSelected) Color(0xFF007ACC) 
                             else if (isDarkMode) Color.White.copy(alpha = 0.1f)
                             else Color.White
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = song.title, 
                fontWeight = FontWeight.Bold, 
                maxLines = 1,
                color = if (isSelected) Color.White else if (isDarkMode) Color.White else Color.Black
            )
            Text(
                text = song.artist, 
                style = MaterialTheme.typography.labelSmall, 
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
            )
        }
    }
}
