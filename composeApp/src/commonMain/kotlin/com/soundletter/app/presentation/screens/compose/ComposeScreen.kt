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
import com.soundletter.app.presentation.components.GlassCard
import com.soundletter.app.presentation.theme.SoundLetterColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeScreen(
    onNavigateBack: () -> Unit,
    viewModel: ComposeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.sendStatus) {
        when (val status = state.sendStatus) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Letter sent successfully!")
                viewModel.resetStatus()
                onNavigateBack()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar("Error: ${status.message}")
                viewModel.resetStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Compose Letter") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(SoundLetterColors.BackgroundGradient))
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
                    label = { Text("To") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.sender,
                    onValueChange = { viewModel.onSenderChange(it) },
                    label = { Text("From (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.message,
                    onValueChange = { viewModel.onMessageChange(it) },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )

                Button(
                    onClick = { viewModel.recommendSongs() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = !state.isAiLoading
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (state.isAiLoading) "Analyzing..." else "Rekomendasikan Lagu dengan AI")
                }

                if (state.suggestions.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.suggestions) { song ->
                            SongSuggestionCard(
                                song = song, 
                                isSelected = state.selectedSong == song,
                                onClick = { viewModel.onSongSelect(song) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.sendSoundLetter() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = state.sendStatus !is UiState.Loading
                ) {
                    if (state.sendStatus is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Text("Send Letter", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SongSuggestionCard(song: SongSuggestion, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) 
                             else SoundLetterColors.GlassBackground
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = song.title, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = song.artist, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
        }
    }
}
