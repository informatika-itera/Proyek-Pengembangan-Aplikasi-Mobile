package com.example.synesthesia.presentation.screens.insights

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.presentation.theme.AngerColor
import com.example.synesthesia.presentation.theme.BrightYellow
import com.example.synesthesia.presentation.theme.CalmColor
import com.example.synesthesia.presentation.theme.JoyColor
import com.example.synesthesia.presentation.theme.MelancholyColor
import com.example.synesthesia.presentation.theme.RoyalBlue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditingProfile.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userBio by viewModel.userBio.collectAsStateWithLifecycle()
    
    var editedName by remember(userName) { mutableStateOf(userName) }
    var editedBio by remember(userBio) { mutableStateOf(userBio) }
    
    val weeklySummary by viewModel.weeklySummary.collectAsStateWithLifecycle()
    val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- USER PROFILE SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        if (isEditing) {
                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                label = { Text("Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Text("Stargazer Level 12", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                        }
                    }
                    IconButton(onClick = { 
                        if (isEditing) viewModel.updateProfile(editedName, editedBio, null)
                        else viewModel.toggleEditProfile()
                    }) {
                        Icon(if (isEditing) Icons.Default.Check else Icons.Default.Edit, contentDescription = null, tint = RoyalBlue)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (isEditing) {
                    OutlinedTextField(
                        value = editedBio,
                        onValueChange = { editedBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(userBio, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("INSIGHTS", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp))
        Text("Emotional intelligence analytics", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
        
        Spacer(modifier = Modifier.height(24.dp))

        // AI Weekly Summary Card
        Card(
            modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (MaterialTheme.colorScheme.background == Color.White) 0.8f else 0.25f)
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ANALISIS JIWA MINGGUAN", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                if (isGeneratingSummary) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (weeklySummary != null) {
                    Text(weeklySummary!!, style = MaterialTheme.typography.bodyMedium, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                } else {
                    Button(onClick = { 
                        // Logic moved to VM for simpler access to data
                        viewModel.triggerWeeklySummary()
                    }) {
                        Text("Generate Analysis")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        when (val state = uiState) {
            is InsightsUiState.Success -> {
                // Emotional Trend Chart
                Card(
                    modifier = Modifier.fillMaxWidth().height(240.dp).shadow(12.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Real-time Mood Galaxy", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        MoodChart(
                            modifier = Modifier.fillMaxSize(),
                            points = state.weeklyTrend,
                            dominantEmotions = state.weeklyDominantEmotions
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("EMOTION DISTRIBUTION", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                
                val distribution = remember(state.emotionDistribution) { state.emotionDistribution }
                
                distribution.forEach { (emotionId, percentage) ->
                    EmotionStatRow(
                        label = getEmotionLabel(emotionId),
                        percentage = percentage,
                        color = getEmotionColor(emotionId)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("MOOD CALENDAR", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                MoodCalendar(
                    monthName = state.currentMonthName,
                    year = state.currentYear,
                    calendarData = state.calendarData
                )
            }
            is InsightsUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is InsightsUiState.Empty -> Text("No data yet. Start journaling to see insights!", modifier = Modifier.padding(24.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun getEmotionColor(emotionId: String): Color {
    return when (emotionId.uppercase()) {
        "HEP" -> JoyColor
        "LEP" -> CalmColor
        "LEU" -> MelancholyColor
        "HEU" -> AngerColor
        else -> RoyalBlue
    }
}

private fun getEmotionLabel(emotionId: String): String {
    return when (emotionId.uppercase()) {
        "HEP" -> "Joyful"
        "HEU" -> "Intense"
        "LEP" -> "Peaceful"
        "LEU" -> "Reflective"
        else -> "Balanced"
    }
}

@Composable
fun MoodChart(modifier: Modifier = Modifier, points: List<Float>, dominantEmotions: List<String>) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val path = Path()
        val stepX = size.width / (points.size - 1)
        val maxVal = points.maxOrNull()?.coerceAtLeast(1f) ?: 1f
        
        points.forEachIndexed { index, valRaw ->
            val x = index * stepX
            val y = size.height * (1f - (valRaw / maxVal))
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            
            val emotionColor = getEmotionColor(dominantEmotions.getOrElse(index) { "calm" })
            drawCircle(color = emotionColor, radius = 6.dp.toPx(), center = Offset(x, y))
            drawCircle(color = Color.White.copy(alpha = 0.5f), radius = 8.dp.toPx(), center = Offset(x, y), style = Stroke(width = 2f))
        }
        
        drawPath(path = path, color = Color.White.copy(alpha = 0.3f), style = Stroke(width = 3.dp.toPx()))
    }
}

@Composable
fun EmotionStatRow(label: String, percentage: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("$percentage%", fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.3f)
        )
    }
}
