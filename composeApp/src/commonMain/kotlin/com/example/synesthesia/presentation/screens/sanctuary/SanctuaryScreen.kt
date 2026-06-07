package com.example.synesthesia.presentation.screens.sanctuary

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.presentation.theme.CalmColor
import com.example.synesthesia.presentation.theme.SpaceBlack
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SanctuaryScreen(
    viewModel: SanctuaryViewModel = koinViewModel()
) {
    val recommendation by viewModel.aiRecommendation.collectAsStateWithLifecycle()
    val isGeneratingRec by viewModel.isGeneratingRecommendation.collectAsStateWithLifecycle()
    
    val groundingStep by viewModel.groundingStep.collectAsStateWithLifecycle()
    val worryText by viewModel.worryVaultText.collectAsStateWithLifecycle()
    val isVaultLocked by viewModel.isVaultLocked.collectAsStateWithLifecycle()
    val vaultAiRes by viewModel.vaultAiResponse.collectAsStateWithLifecycle()

    var activeRitual by remember { mutableStateOf<Ritual?>(null) }
    var isRitualActive by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(0) }
    var phaseText by remember { mutableStateOf("") }
    
    val isAstronomy = MaterialTheme.colorScheme.background == SpaceBlack

    val infiniteTransition = rememberInfiniteTransition()
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(isRitualActive, activeRitual) {
        if (isRitualActive && activeRitual != null) {
            if (activeRitual!!.id == "breathing") {
                val phases = listOf("Breathe In" to 4000L, "Hold" to 4000L, "Breathe Out" to 4000L, "Rest" to 4000L)
                while (isRitualActive) {
                    for (phase in phases) {
                        if (!isRitualActive) break
                        phaseText = phase.first
                        delay(phase.second)
                    }
                }
            } else {
                secondsLeft = activeRitual!!.durationSeconds
                while (secondsLeft > 0 && isRitualActive) {
                    delay(1000)
                    secondsLeft--
                    phaseText = when (activeRitual!!.id) {
                        "meditation" -> "Quiet your mind..."
                        "sleep" -> "Drift away..."
                        "focus" -> "Focus on your heart..."
                        "gratitude" -> "Recall a happy moment..."
                        else -> "Breathe and relax..."
                    }
                }
                isRitualActive = false
            }
        }
    }

    var showWorryVault by remember { mutableStateOf(false) }
    var showGrounding by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column {
                Text(
                    "SANCTUARY",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Guided rituals to calm your universe",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
        
        item {
            // AI Recommendation Card
            if (isGeneratingRec) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().clip(CircleShape))
            } else {
                recommendation?.let { rec ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = rec.accentColor.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, rec.accentColor.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.AutoAwesome, tint = rec.accentColor, modifier = Modifier.size(20.dp), contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(rec.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = rec.accentColor)
                                Spacer(Modifier.height(4.dp))
                                Text(rec.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }

        item {
            // --- Worry Vault & Grounding Row ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SmallSanctuaryTool(
                    title = "Worry Vault",
                    icon = Icons.Default.Lock,
                    color = Color(0xFFCE93D8),
                    onClick = { showWorryVault = true }
                )
                SmallSanctuaryTool(
                    title = "5-4-3-2-1",
                    icon = Icons.Default.Sensors,
                    color = Color(0xFF81D4FA),
                    onClick = { 
                        viewModel.startGrounding()
                        showGrounding = true 
                    }
                )
            }
        }

        item {
            // Active Ritual Display
            Box(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                if (isRitualActive && activeRitual != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .shadow(16.dp, RoundedCornerShape(32.dp)),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = activeRitual!!.color)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            IconButton(
                                onClick = { isRitualActive = false },
                                modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (activeRitual!!.id == "breathing") {
                                    BreathingCircle(phaseText, breatheScale)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .background(Color.White.copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            activeRitual!!.icon, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(60.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(phaseText, color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                                if (activeRitual!!.id != "breathing") {
                                    Text("${secondsLeft / 60}:${(secondsLeft % 60).toString().padStart(2, '0')} remaining", color = Color.White.copy(alpha = 0.8f))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                TextButton(onClick = { isRitualActive = false }) {
                                    Text("END SESSION", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Featured/Quick Start Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .shadow(16.dp, RoundedCornerShape(28.dp))
                            .clickable { 
                                activeRitual = rituals.first()
                                isRitualActive = true
                            },
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = CalmColor)
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Daily Breathing", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                                Text("Box breathing session", color = Color.White.copy(alpha = 0.9f))
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { 
                                        activeRitual = rituals.first()
                                        isRitualActive = true 
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("START", color = CalmColor, fontWeight = FontWeight.Bold)
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }

        if (!isRitualActive) {
            item {
                Text(
                    "RITUAL CATEGORIES", 
                    style = MaterialTheme.typography.labelLarge, 
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(
                items = rituals.chunked(2),
                key = { it.joinToString("-") { ritual -> ritual.id } }
            ) { pair ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    pair.forEach { ritual ->
                        RitualCard(
                            ritual = ritual,
                            isAstronomy = isAstronomy,
                            isRecommended = recommendation?.suggestedRituals?.contains(ritual.id) == true,
                            accentColor = recommendation?.accentColor ?: MaterialTheme.colorScheme.primary,
                            onClick = {
                                activeRitual = ritual
                                isRitualActive = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Bottom padding
        }
    }

    // --- Dialogs ---
    if (showWorryVault) {
        WorryVaultDialog(
            text = worryText,
            isLocked = isVaultLocked,
            aiResponse = vaultAiRes,
            onTextChange = viewModel::setWorryText,
            onLock = viewModel::lockWorryVault,
            onDismiss = {
                showWorryVault = false
                viewModel.resetVault()
            }
        )
    }

    if (showGrounding) {
        GroundingDialog(
            step = groundingStep,
            aiResponse = vaultAiRes,
            onInput = viewModel::addGroundingInput,
            onDismiss = {
                showGrounding = false
                viewModel.resetGrounding()
            }
        )
    }
}

@Composable
fun RowScope.SmallSanctuaryTool(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.weight(1f).height(70.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun BreathingCircle(phase: String, breatheScale: Float) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
        Box(
            modifier = Modifier.size(160.dp).scale(breatheScale * 1.3f)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                }
        )
        Box(
            modifier = Modifier.size(140.dp).scale(breatheScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier.size(120.dp).scale(breatheScale * 0.9f)
                .background(Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(phase, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun RitualCard(
    ritual: Ritual, 
    isAstronomy: Boolean, 
    isRecommended: Boolean, 
    accentColor: Color, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1.1f)
            .shadow(if (isAstronomy) 0.dp else 4.dp, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAstronomy) MaterialTheme.colorScheme.surface.copy(alpha = 0.98f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isRecommended) BorderStroke(2.dp, accentColor) else if (isAstronomy) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isRecommended) {
                Surface(
                    color = accentColor,
                    shape = RoundedCornerShape(bottomStart = 12.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        "AI Pick ✨",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = ritual.color.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = ritual.icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = ritual.color
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    ritual.name, 
                    fontWeight = FontWeight.Black, 
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun WorryVaultDialog(
    text: String,
    isLocked: Boolean,
    aiResponse: String?,
    onTextChange: (String) -> Unit,
    onLock: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (!isLocked) {
                Button(onClick = onLock) { Text("Lock Vault") }
            } else {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        },
        title = { Text("Worry Vault 🔒") },
        text = {
            Column {
                if (!isLocked) {
                    Text("Release your worry here. It will be locked away and not part of your journal.")
                    OutlinedTextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("What's on your mind?") }
                    )
                } else {
                    Text("Your worry is locked in the cosmic vault.", fontWeight = FontWeight.Bold)
                    if (aiResponse != null) {
                        Spacer(Modifier.height(16.dp))
                        Text("AI Counter-thought:", style = MaterialTheme.typography.labelSmall)
                        Text(aiResponse, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    } else {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun GroundingDialog(
    step: Int,
    aiResponse: String?,
    onInput: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val stepTitle = when(step) {
        1 -> "5 things you see"
        2 -> "4 things you can touch"
        3 -> "3 things you hear"
        4 -> "2 things you smell"
        5 -> "1 thing you taste"
        else -> "Completion"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (step <= 5) {
                Button(onClick = { 
                    onInput(text)
                    text = ""
                }, enabled = text.isNotBlank()) { Text("Next") }
            } else {
                TextButton(onClick = onDismiss) { Text("Finish") }
            }
        },
        title = { Text("5-4-3-2-1 Grounding 🌿") },
        text = {
            Column {
                if (step <= 5) {
                    Text(stepTitle, fontWeight = FontWeight.Black)
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type here...") }
                    )
                } else {
                    if (aiResponse != null) {
                        Text(aiResponse, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    )
}

data class Ritual(
    val id: String,
    val name: String, 
    val icon: ImageVector,
    val color: Color,
    val durationSeconds: Int
)

val rituals = listOf(
    Ritual("breathing", "Breathing", Icons.Default.Air, Color(0xFF34D399), 60),
    Ritual("meditation", "Meditation", Icons.Default.SelfImprovement, Color(0xFF7C3AED), 300),
    Ritual("sleep", "Sleep Well", Icons.Default.Bedtime, Color(0xFF60A5FA), 600),
    Ritual("focus", "Focus Flow", Icons.Default.FilterCenterFocus, Color(0xFFF97316), 1200),
    Ritual("gratitude", "Gratitude", Icons.Default.Favorite, Color(0xFFFDE047), 180),
    Ritual("energy", "Energy Boost", Icons.Default.Bolt, Color(0xFFFF7171), 300)
)
