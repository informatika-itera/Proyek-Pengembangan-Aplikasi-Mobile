package com.soundletter.app.presentation.screens.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.presentation.components.GlassCard
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMessageScreen(
    messageId: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailMessageScreenViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    val backgroundColor = if (isDarkMode) Color(0xFF000000) else Color(0xFFF0F8FF)
    val primaryColor = if (isDarkMode) Color.White else Color(0xFF007ACC)

    LaunchedEffect(messageId) {
        viewModel.loadMessage(messageId)
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Isi Surat", fontWeight = FontWeight.Bold, color = primaryColor) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val letterState = state.letterState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                is UiState.Success -> DetailContent(letterState.data, viewModel, isDarkMode)
                is UiState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = letterState.message, color = primaryColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMessage(messageId) }) { Text("Coba Lagi") }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun DetailContent(message: Note, viewModel: DetailMessageScreenViewModel, isDarkMode: Boolean) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        VinylRecord(isPlaying = state.isPlaying)
        Spacer(modifier = Modifier.height(48.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Untuk: ${message.recipient}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDarkMode) Color.White else Color(0xFF007ACC)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                    color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "— Dari ${message.sender}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Gray
                    ),
                    modifier = Modifier.align(Alignment.End),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        MusicControls(message, viewModel)
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun VinylRecord(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(220.dp)
            .rotate(if (isPlaying) rotation else 0f)
            .clip(CircleShape)
            .background(Color.Black)
            .border(6.dp, Color.DarkGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 1..8) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = (size.minDimension / 2) * (i / 8f),
                    style = Stroke(width = 1f)
                )
            }
        }
        Box(
            modifier = Modifier.size(70.dp).clip(CircleShape).background(Color(0xFF007ACC)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color.Black))
        }
    }
}

@Composable
fun MusicControls(message: Note, viewModel: DetailMessageScreenViewModel) {
    val state by viewModel.state.collectAsState()
    val hasPreview = !message.songPreviewUrl.isNullOrBlank()

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message.songTitle ?: "Tanpa Judul",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (state.isPlaying) Color(0xFF007ACC) else Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = message.songArtist ?: "Unknown Artist",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        FloatingActionButton(
            onClick = { if (hasPreview) viewModel.toggleAudio(message.songPreviewUrl) },
            containerColor = if (hasPreview) Color(0xFF007ACC) else Color.LightGray,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}
