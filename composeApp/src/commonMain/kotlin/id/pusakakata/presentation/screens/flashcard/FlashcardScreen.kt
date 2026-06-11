package id.pusakakata.presentation.screens.flashcard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.presentation.components.EmptyState
import id.pusakakata.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ASAH PUSAKA", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f))
                    )
                )
        ) {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                }
            ) { state ->
                when (state) {
                    is FlashcardUiState.Loading -> LoadingIndicator()
                    is FlashcardUiState.Empty -> EmptyState(
                        message = "Semua pusaka sudah diasah! Datang lagi besok untuk mempertajam ingatan Anda.",
                        onAction = onBack,
                        actionLabel = "Kembali"
                    )
                    is FlashcardUiState.Success -> {
                        if (state.isFinished) {
                            EmptyState(
                                message = "Sesi belajar selesai! Pengetahuan Anda tentang kosakata Nusantara semakin tajam. 🎉",
                                onAction = onBack,
                                actionLabel = "Kembali ke Menu"
                            )
                        } else {
                            FlashcardContent(state, viewModel::flipCard, viewModel::nextCard)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardContent(
    state: FlashcardUiState.Success,
    onFlip: () -> Unit,
    onNext: (Int) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "KARTU ${state.currentIndex + 1} / ${state.words.size}",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(Modifier.height(40.dp))
        
        val currentWord = state.currentWord
        if (currentWord != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .shadow(12.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .clickable { onFlip() },
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (rotation <= 90f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation <= 90f) {
                        Text(
                            text = currentWord.term,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Column(
                            modifier = Modifier.graphicsLayer { rotationY = 180f },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "MAKNA", 
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = currentWord.definition,
                                style = MaterialTheme.typography.titleLarge.copy(lineHeight = 32.sp, fontWeight = FontWeight.SemiBold),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(60.dp))

            AnimatedVisibility(
                visible = state.isFlipped,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "SEBERAPA HAFAL ANDA?", 
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FlashcardActionButton("Lupa", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f)) { onNext(1) }
                        FlashcardActionButton("Ingat", MaterialTheme.colorScheme.secondary, Modifier.weight(1f)) { onNext(3) }
                        FlashcardActionButton("Hafal", MaterialTheme.colorScheme.primary, Modifier.weight(1f)) { onNext(5) }
                    }
                }
            }
            
            if (!state.isFlipped) {
                Text(
                    "Ketuk kartu untuk melihat arti", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}

@Composable
fun FlashcardActionButton(label: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(label.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
    }
}
