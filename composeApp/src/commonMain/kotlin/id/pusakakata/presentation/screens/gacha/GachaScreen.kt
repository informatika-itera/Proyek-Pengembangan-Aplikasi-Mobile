package id.pusakakata.presentation.screens.gacha

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
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
                title = { Text("GALERI MITOLOGI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier.padding(end = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text("$tokens 🪙", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                    }
                ) { state ->
                    when (state) {
                        is GachaUiState.Idle -> {
                            GachaIdleView(onDraw = viewModel::drawCard, canDraw = tokens > 0)
                        }
                        is GachaUiState.Drawing -> {
                            GachaDrawingView()
                        }
                        is GachaUiState.Result -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CardResult(card = state.card)
                                Spacer(modifier = Modifier.height(40.dp))
                                Button(
                                    onClick = { viewModel.reset() },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    Text("PANGGIL LAGI", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                }
                            }
                        }
                        is GachaUiState.Error -> {
                            GachaErrorView(state.message, onBack)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GachaIdleView(onDraw: () -> Unit, canDraw: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing))
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(220.dp).rotate(rotation),
                shape = RoundedCornerShape(60.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {}
            
            Icon(
                Icons.Default.AutoAwesome, 
                contentDescription = null, 
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            "Pusaka Mitologi", 
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Text(
            "Gunakan 1 token untuk mengungkap sosok legenda.", 
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onDraw,
            enabled = canDraw,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Stars, null)
                Spacer(Modifier.width(12.dp))
                Text("MULAI PEMANGGILAN (1 🪙)", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun GachaDrawingView() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.AutoAwesome, 
            contentDescription = null, 
            modifier = Modifier.size(100.dp).scale(scale).alpha(alpha),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            "MEMANGGIL SOSOK LEGENDA...", 
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
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

    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(rarityColor.copy(alpha = 0.15f * glowAlpha), MaterialTheme.colorScheme.surface)
                        )
                    )
                    .border(2.dp, Brush.sweepGradient(listOf(rarityColor.copy(alpha = 0.2f), rarityColor, rarityColor.copy(alpha = 0.2f))), RoundedCornerShape(32.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = rarityColor,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = card.rarity.displayName.uppercase(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = card.description,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, lineHeight = 26.sp),
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn, 
                            null, 
                            modifier = Modifier.size(18.dp), 
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("ASAL: ${card.origin.uppercase()}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }

                if (card.fullStory.isNotBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = { showFullStory = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.HistoryEdu, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("BACA LEGENDA LENGKAP", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }

    if (showFullStory) {
        AlertDialog(
            onDismissRequest = { showFullStory = false },
            shape = RoundedCornerShape(32.dp),
            title = {
                Text(
                    text = "Legenda ${card.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = card.fullStory,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                        textAlign = TextAlign.Justify
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showFullStory = false }, shape = RoundedCornerShape(12.dp)) {
                    Text("TUTUP", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun GachaErrorView(message: String, onBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Stars, null, modifier = Modifier.size(80.dp).alpha(0.3f))
        Spacer(Modifier.height(24.dp))
        Text(
            message, 
            color = MaterialTheme.colorScheme.error, 
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack, shape = RoundedCornerShape(16.dp)) { 
            Text("Dapatkan Token di Kuis", fontWeight = FontWeight.Bold) 
        }
    }
}
