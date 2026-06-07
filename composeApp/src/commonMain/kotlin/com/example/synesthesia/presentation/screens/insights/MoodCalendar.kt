package com.example.synesthesia.presentation.screens.insights

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import kotlinx.datetime.*

@Composable
fun MoodCalendar(
    monthName: String,
    year: Int,
    calendarData: Map<Int, String?>,
    modifier: Modifier = Modifier
) {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentMonth = Month.valueOf(monthName.uppercase())
    val today = if (now.month == currentMonth && now.year == year) now.dayOfMonth else -1
    
    // Calculate days in month and start offset
    val firstDayOfMonth = LocalDate(year, currentMonth, 1)
    val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.isoDayNumber - 1) % 7
    val daysInMonth = when (currentMonth) {
        Month.FEBRUARY -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }

    Card(
        modifier = modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "${monthName.lowercase().replaceFirstChar { it.uppercase() }} $year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Day Headers
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(max = 300.dp),
                userScrollEnabled = false
            ) {
                // Empty slots for offset
                items(dayOfWeekOffset) {
                    Spacer(modifier = Modifier.padding(2.dp).aspectRatio(1f))
                }

                items(
                    count = daysInMonth,
                    key = { index -> "day-${index + 1}-$monthName" }
                ) { index ->
                    val day = index + 1
                    val emotionColorHex = calendarData[day]
                    val backgroundColor = if (emotionColorHex != null) {
                        parseHexColor(emotionColorHex).copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                    }
                    
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .aspectRatio(1f)
                            .background(backgroundColor, RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = if (day == today) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            fontSize = 12.sp,
                            fontWeight = if (day == today) FontWeight.Bold else FontWeight.Normal,
                            color = if (day == today) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun parseHexColor(hex: String?): Color {
    if (hex == null || !hex.startsWith("#")) return Color.Gray
    return try {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        Color.Gray
    }
}
