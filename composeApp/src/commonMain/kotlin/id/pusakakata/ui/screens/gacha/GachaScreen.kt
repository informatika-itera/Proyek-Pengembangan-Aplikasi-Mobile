package id.pusakakata.ui.screens.gacha

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.domain.model.LegendaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreen(
    viewModel: GachaViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens by viewModel.tokens.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Galeri Mitologi", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("$tokens 🪙", style = MaterialTheme.typography.labelMedium)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (val state = uiState) {
                    is GachaUiState.Idle -> {
                        GachaIdleView(onDraw = viewModel::drawCard, canDraw = tokens > 0)
                    }
                    is GachaUiState.Drawing -> {
                        GachaDrawingView()
                    }
                    is GachaUiState.Result -> {
                        CardResult(card = state.card)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { viewModel.reset() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Tarik Kartu Lagi", fontWeight = FontWeight.Bold)
                        }
                    }
                    is GachaUiState.Error -> {
                        Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onBack) { Text("Dapatkan Token di Kuis") }
                    }
                }
            }
        }
    }
}

@Composable
fun GachaIdleView(onDraw: () -> Unit, canDraw: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Icon(
                Icons.Default.AutoAwesome, 
                contentDescription = null, 
                modifier = Modifier.padding(40.dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            "Temukan Legenda Nusantara", 
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Gunakan 1 token untuk memanggil satu sosok mitologi.", 
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onDraw,
            enabled = canDraw,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Mulai Pemanggilan (1 🪙)", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun GachaDrawingView() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.AutoAwesome, 
            contentDescription = null, 
            modifier = Modifier.size(120.dp * scale),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("Sedang Menarik Kartu...", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun CardResult(card: LegendaryCard) {
    var showFullStory by remember { mutableStateOf(false) }
    
    val rarityColor = when (card.rarity.name) {
        "MYTHIC" -> Color(0xFFD4AF37)
        "EPIC" -> Color(0xFF9C27B0)
        "RARE" -> Color(0xFF2196F3)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(rarityColor.copy(alpha = 0.1f), MaterialTheme.colorScheme.surface)
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = rarityColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = card.rarity.displayName.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = card.name,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = card.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn, 
                        null, 
                        modifier = Modifier.size(16.dp), 
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Asal: ${card.origin}", style = MaterialTheme.typography.labelMedium)
                }
            }

            if (card.fullStory.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { showFullStory = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.HistoryEdu, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Baca Cerita Lengkap", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showFullStory) {
        AlertDialog(
            onDismissRequest = { showFullStory = false },
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = "Legenda ${card.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = card.fullStory,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                        textAlign = TextAlign.Justify
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFullStory = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
