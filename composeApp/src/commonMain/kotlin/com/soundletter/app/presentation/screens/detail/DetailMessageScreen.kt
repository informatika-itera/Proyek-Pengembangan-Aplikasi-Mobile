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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundletter.app.presentation.components.GlassCard
import com.soundletter.app.presentation.theme.SoundLetterColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMessageScreen(
    messageId: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(messageId) {
        viewModel.loadMessage(messageId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Letter Detail") },
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
            state.message?.let { message ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VinylRecord()

                    Spacer(modifier = Modifier.height(32.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(
                                text = "To: ${message.to}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "— From ${message.from}",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    MusicControls(message.songTitle ?: "Unknown Song")
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun VinylRecord() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .size(240.dp)
            .rotate(rotation)
            .clip(CircleShape)
            .background(Color.Black)
            .border(4.dp, Color.DarkGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 1..5) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = (size.minDimension / 2) * (i / 5f),
                    style = Stroke(width = 2f)
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
fun MusicControls(songTitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = songTitle,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(32.dp))
            }
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Pause, contentDescription = null, tint = Color.Black)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        LinearProgressIndicator(
            progress = { 0.4f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.DarkGray
        )
    }
}
