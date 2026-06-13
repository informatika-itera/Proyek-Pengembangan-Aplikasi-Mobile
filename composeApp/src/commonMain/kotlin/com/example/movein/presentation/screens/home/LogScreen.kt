package com.example.movein.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.JourneyLog

@Composable
fun LogScreen(
    logs: List<JourneyLog>,
    clearLogs: () -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf("timeline") }
    var monthOffset by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Emotional Log",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )
                Text(
                    text = "Jejak perjalanan mentalmu.",
                    fontSize = 14.sp,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }
            if (logs.isNotEmpty() && viewMode == "timeline") {
                Text(
                    text = "Clear DB",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFFF87171),
                    modifier = Modifier.clickable { clearLogs() }.padding(bottom = 4.dp)
                )
            }
        }

        // View Mode Toggle (Segmented Control)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isLight) Color(0xFFF3F4F6) else Color(0xFF171717).copy(alpha = 0.6f))
                .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            ToggleItem(
                label = "Timeline",
                icon = Icons.Default.List,
                selected = viewMode == "timeline",
                isLight = isLight,
                modifier = Modifier.weight(1f)
            ) { viewMode = "timeline" }
            ToggleItem(
                label = "Kalender",
                icon = Icons.Default.CalendarMonth,
                selected = viewMode == "calendar",
                isLight = isLight,
                modifier = Modifier.weight(1f)
            ) { viewMode = "calendar" }
        }

        Crossfade(targetState = viewMode) { mode ->
            if (mode == "timeline") {
                TimelineView(logs = logs, isLight = isLight)
            } else {
                CalendarView(monthOffset = monthOffset, onOffsetChange = { monthOffset = it }, isLight = isLight)
            }
        }
    }
}

@Composable
private fun ToggleItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, isLight: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) (if (isLight) Color.White else Color.White.copy(alpha = 0.1f)) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (selected) (if (isLight) Color(0xFF171717) else Color.White) else Color(0xFF737373))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (selected) (if (isLight) Color(0xFF171717) else Color.White) else Color(0xFF737373))
        }
    }
}

@Composable
private fun TimelineView(logs: List<JourneyLog>, isLight: Boolean) {
    if (logs.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(if (isLight) Color(0xFFF9FAFB) else Color(0xFF171717).copy(alpha = 0.5f))
                    .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Waves, null, modifier = Modifier.size(32.dp), tint = Color(0xFFA3A3A3))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Layar Masih Kosong", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
            Text("Transisi emosimu akan tercatat otomatis di sini.", fontSize = 14.sp, textAlign = TextAlign.Center, color = Color(0xFF737373), modifier = Modifier.width(250.dp))
        }
    } else {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 6.dp)) {
            // Timeline Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .align(Alignment.TopStart)
                    .offset(x = 11.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                (if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.2f)),
                                (if (isLight) Color(0xFFF3F4F6) else Color(0xFF262626)),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                logs.forEach { log ->
                    TimelineItem(log = log, isLight = isLight)
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(log: JourneyLog, isLight: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Dot
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isLight) Color.White else Color.Black)
                .border(3.dp, if (isLight) Color(0xFFF3F4F6) else Color(0xFF0F0F11), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isLight) Color(0xFFA3A3A3) else Color.White.copy(alpha = 0.5f)))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isLight) Color.White else Color(0xFF171717).copy(alpha = 0.4f))
                .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Text(log.time.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Color(0xFF737373))
            
            Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(log.bgColor)
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = when(log.type) {
                            "yapping" -> Icons.Default.Psychology
                            "breathing" -> Icons.Default.Air
                            "tinywin" -> Icons.Default.Coffee
                            else -> Icons.Default.List
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = log.color
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("State: ${log.mood}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (isLight) Color(0xFF171717) else Color.White)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLight) Color(0xFFF9FAFB) else Color.Black.copy(alpha = 0.3f))
                    .border(1.dp, if (isLight) Color(0xFFF3F4F6) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp), tint = Color(0xFFA3A3A3))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(log.task, fontSize = 12.sp, color = if (isLight) Color(0xFF525252) else Color(0xFFD1D5DB))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, null, modifier = Modifier.size(14.dp), tint = log.color)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(log.result, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = log.color)
                }
            }
        }
    }
}

@Composable
private fun CalendarView(monthOffset: Int, onOffsetChange: (Int) -> Unit, isLight: Boolean) {
    val months = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    
    // Mocking May 2026 as base (index 4)
    var monthIndex = 4 + monthOffset
    var year = 2026
    while (monthIndex < 0) { monthIndex += 12; year -= 1 }
    while (monthIndex > 11) { monthIndex -= 12; year += 1 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(if (isLight) Color.White else Color(0xFF171717).copy(alpha = 0.4f))
            .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        // Calendar Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onOffsetChange(monthOffset - 1) }) {
                Icon(Icons.Default.ChevronLeft, null, tint = if (isLight) Color(0xFF171717) else Color.White)
            }
            Text("${months[monthIndex]} $year", fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
            IconButton(
                onClick = { onOffsetChange(monthOffset + 1) },
                enabled = monthOffset < 0
            ) {
                Icon(Icons.Default.ChevronRight, null, tint = if (monthOffset < 0) (if (isLight) Color(0xFF171717) else Color.White) else Color.Transparent)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days Grid
        val days = listOf("S", "S", "R", "K", "J", "S", "M")
        Row(modifier = Modifier.fillMaxWidth()) {
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF737373)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mock 31 days
        for (row in 0 until 5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayNum = row * 7 + col + 1
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNum <= 31) {
                            val moodColor = getMockMoodColor(dayNum, monthOffset, isLight)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(moodColor.bg)
                                    .then(if (moodColor.border != Color.Transparent) Modifier.border(1.dp, moodColor.border, CircleShape) else Modifier),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (monthOffset == 0 && dayNum == 24) "" else dayNum.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = moodColor.text
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("Overwhelmed", Color(0xFFEF4444))
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem("Lelah", Color(0xFFF59E0B))
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem("Recovering", Color(0xFF3B82F6))
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 10.sp, color = Color(0xFF737373))
    }
}

data class MoodColorSet(val bg: Color, val text: Color, val border: Color = Color.Transparent)

private fun getMockMoodColor(day: Int, offset: Int, isLight: Boolean): MoodColorSet {
    if (offset == 0) { // Mei 2026
        return when (day) {
            12, 15 -> MoodColorSet(Color(0xFFEF4444), Color.White)
            18, 21 -> MoodColorSet(Color(0xFF3B82F6), Color.White)
            19, 20 -> MoodColorSet(Color(0xFFF59E0B), Color.White)
            24 -> if (isLight) MoodColorSet(Color(0xFF171717), Color.White) else MoodColorSet(Color.White, Color.Black)
            else -> MoodColorSet(if (isLight) Color.White else Color(0xFF262626), if (isLight) Color(0xFF525252) else Color.White.copy(alpha = 0.8f), if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f))
        }
    } else {
        return when {
            day % 7 == 0 -> MoodColorSet(Color(0xFFEF4444), Color.White)
            day % 5 == 0 -> MoodColorSet(Color(0xFF3B82F6), Color.White)
            else -> MoodColorSet(if (isLight) Color.White else Color(0xFF262626), if (isLight) Color(0xFF525252) else Color.White.copy(alpha = 0.8f), if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f))
        }
    }
}
