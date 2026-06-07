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

    val backgroundColor = if (isDarkMode) Color(0xFF000000) else Color(0xFFF0F8FF) // Black vs Sky Blue Light
    val primaryTextColor = if (isDarkMode) Color.White else Color(0xFF007ACC)

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCompose,
                containerColor = primaryTextColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Compose")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "SoundLetter",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryTextColor,
                    letterSpacing = 1.5.sp
                )
            )

            when (val state = uiState) {
                is UiState.Loading -> LoadingView()
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
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(state.data, key = { it.id }) { letter ->
                                MessageCard(
                                    message = letter,
                                    isDarkMode = isDarkMode,
                                    onClick = { onNavigateToDetail(letter.id.toString()) }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    LaunchedEffect(state.message) {
                        snackbarHostState.showSnackbar("Error: ${state.message}")
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MessageCard(message: Note, isDarkMode: Boolean, onClick: () -> Unit) {
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        isDarkMode = isDarkMode
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To: ${message.recipient}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF007ACC)
                    )
                )
                Text(
                    text = message.sender,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color.Black
                ),
                maxLines = 3
            )
            if (!message.songTitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isDarkMode) Color(0xFF00BFFF) else Color(0xFF007ACC),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.songTitle,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color(0xFF00BFFF) else Color(0xFF007ACC)
                        )
                    )
                }
            }
        }
    }
}
