package com.example.movein.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.AppState
import com.example.movein.presentation.JourneyLog
import com.example.movein.presentation.displayName
import com.example.movein.presentation.components.*
import com.example.movein.presentation.theme.MoveInTheme

@Composable
fun MentalSpaceScreen(
    userName: String,
    mentalState: AppState,
    onMentalStateChange: (AppState) -> Unit,
    onMomentumChange: (Int) -> Unit,
    onAddLog: (JourneyLog) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAIModal by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf("") }

    val accentColor = when (mentalState) {
        AppState.OVERWHELMED -> MoveInTheme.colors.primary
        AppState.RECOVERING -> MoveInTheme.colors.accentBlue
        AppState.NEUTRAL -> MoveInTheme.colors.secondary
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            MoveInPill(
                text = "MENTAL SPACE: ${mentalState.displayName}",
                selected = true,
                color = accentColor.copy(alpha = 0.2f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = mentalState.displayName,
                style = MoveInTheme.typography.displayLarge.copy(color = MoveInTheme.colors.textPrimary)
            )
        }

        item {
            YappingSpaceCard(
                accentColor = accentColor,
                onYappingSent = { text ->
                    onMentalStateChange(AppState.OVERWHELMED)
                    onMomentumChange(3)
                    onAddLog(JourneyLog(
                        time = "Baru saja",
                        mood = "Overwhelmed",
                        task = "Yapping Space",
                        result = "Divalidasi AI",
                        type = "yapping",
                        appState = AppState.OVERWHELMED
                    ))
                    aiResponse = getAIResponse(text)
                    showAIModal = true
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AmbienceCard(
                    mentalState = mentalState,
                    modifier = Modifier.weight(1f)
                )
                TinyWinCard(
                    onComplete = { task ->
                        onMentalStateChange(AppState.RECOVERING)
                        onMomentumChange(10)
                        onAddLog(JourneyLog(
                            time = "Baru saja",
                            mood = "Lelah",
                            task = task,
                            result = "Recovery",
                            type = "tinywin",
                            appState = AppState.RECOVERING
                        ))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            QuoteCard()
        }
    }

    if (showAIModal) {
        AlertDialog(
            onDismissRequest = { showAIModal = false },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MoveInTheme.colors.accentPink) },
            title = { Text("AI Validation", style = MoveInTheme.typography.titleLarge) },
            text = { Text(aiResponse, style = MoveInTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = { showAIModal = false }) {
                    Text("Terima Kasih", color = MoveInTheme.colors.primary)
                }
            },
            containerColor = MoveInTheme.colors.cardPrimary,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

private fun getAIResponse(text: String): String {
    val t = text.lowercase()
    return when {
        t.contains("tugas") || t.contains("kerja") || t.contains("dosen") -> 
            "Tugas yang numpuk memang bisa bikin napas terasa sesak. Valid kalau kamu capek. Tinggalkan layar sebentar, dunia tidak akan runtuh."
        t.contains("sepi") || t.contains("sendiri") || t.contains("sedih") -> 
            "Rasa sepi kadang datang tiba-tiba dan terasa berat. Kamu boleh merasa seperti itu. Pelan-pelan dulu ya."
        t.contains("bingung") || t.contains("overthinking") || t.contains("pusing") -> 
            "Isi kepala yang terlalu ramai memang melelahkan. Tidak semua harus selesai malam ini."
        else -> "Aku dengar keluh kesahmu. Tidak apa-apa merasa seperti ini. Kamu sudah bertahan hari ini."
    }
}

@Composable
fun YappingSpaceCard(accentColor: Color, onYappingSent: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    MoveInCard(
        borderColor = accentColor.copy(alpha = 0.3f),
        contentPadding = 20.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ChatBubbleOutline, null, tint = MoveInTheme.colors.errorRed, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yapping Space", style = MoveInTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text("${text.length}/250", style = MoveInTheme.typography.labelSmall.copy(color = MoveInTheme.colors.textMuted))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { if (it.length <= 250) text = it },
            placeholder = { Text("Ruangan ini aman. Tulis semuanya sesuai curhatanmu...", style = MoveInTheme.typography.bodyMedium.copy(color = MoveInTheme.colors.textMuted)) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor.copy(alpha = 0.5f),
                unfocusedBorderColor = MoveInTheme.colors.borderSubtle,
                focusedContainerColor = MoveInTheme.colors.surfaceSecondary.copy(alpha = 0.3f),
                unfocusedContainerColor = MoveInTheme.colors.surfaceSecondary.copy(alpha = 0.3f)
            )
        )
        AnimatedVisibility(visible = text.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            MoveInPrimaryButton(
                text = if (isSending) "Sending..." else "Kirim Curhatan",
                onClick = {
                    isSending = true
                    // Simulate processing
                    onYappingSent(text)
                    text = ""
                    isSending = false
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AmbienceCard(mentalState: AppState, modifier: Modifier = Modifier) {
    MoveInCard(modifier = modifier) {
        Text("Ambience", style = MoveInTheme.typography.labelSmall.copy(color = MoveInTheme.colors.textSecondary))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (mentalState) {
                AppState.OVERWHELMED -> "Feels Heavy."
                AppState.RECOVERING -> "Breathing."
                AppState.NEUTRAL -> "Neutral."
            },
            style = MoveInTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (mentalState) {
                AppState.OVERWHELMED -> "Ruang visual menyempit sesaat."
                AppState.RECOVERING -> "Sedang memberi ruang untuk pulih."
                AppState.NEUTRAL -> "Semua berjalan normal."
            },
            style = MoveInTheme.typography.bodyMedium.copy(color = MoveInTheme.colors.textMuted)
        )
    }
}

@Composable
fun TinyWinCard(onComplete: (String) -> Unit, modifier: Modifier = Modifier) {
    val tasks = listOf("Minum Segelas Air", "Tarik Napas 3x", "Renggangkan Tangan", "Pejam Mata 1 Menit")
    var currentTaskIndex by rememberSaveable { mutableStateOf(0) }
    val currentTask = tasks[currentTaskIndex]

    MoveInCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WaterDrop, null, tint = MoveInTheme.colors.accentBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { currentTaskIndex = (currentTaskIndex + 1) % tasks.size }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Refresh, null, tint = MoveInTheme.colors.textMuted, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(currentTask, style = MoveInTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        MoveInPrimaryButton(
            text = "Selesaikan",
            onClick = { onComplete(currentTask) },
            small = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun QuoteCard() {
    var quoteMode by rememberSaveable { mutableStateOf("Gentle") }
    val quote = if (quoteMode == "Gentle") 
        "It's okay to do nothing today. Your worth isn't tied to productivity." 
    else 
        "Tugas tidak akan selesai pakai sihir. Berhenti scroll, kerjakan sedikit sekarang."

    MoveInCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Klor Quote", style = MoveInTheme.typography.labelSmall.copy(color = MoveInTheme.colors.textSecondary))
            MoveInSegmentedControl(
                options = listOf("Gentle", "Roast"),
                selectedOption = quoteMode,
                onSelected = { quoteMode = it },
                modifier = Modifier.width(140.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "\"$quote\"",
            style = MoveInTheme.typography.displayMedium.copy(fontSize = 18.sp, lineHeight = 24.sp)
        )
    }
}
