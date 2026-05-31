package com.studyhub.presentation.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerBottomSheet(
    initialWorkMinutes: Int = 25,
    initialBreakMinutes: Int = 5,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var mode by remember { mutableStateOf("work") }
    var seconds by remember { mutableStateOf(initialWorkMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var sessions by remember { mutableStateOf(0) }
    
    // Duration settings (to calculate progress correctly even after manual adjustment)
    var currentWorkDurationMins by remember { mutableStateOf(initialWorkMinutes) }
    var currentBreakDurationMins by remember { mutableStateOf(initialBreakMinutes) }

    val totalSeconds = if (mode == "work") currentWorkDurationMins * 60 else currentBreakDurationMins * 60
    val progress = if (totalSeconds > 0) 1f - (seconds.toFloat() / totalSeconds.toFloat()) else 0f
    
    val activeColor = if (mode == "work") Color(0xFF9C7C50) else Color(0xFF10B981)

    LaunchedEffect(isRunning, seconds) {
        if (isRunning && seconds > 0) {
            delay(1000)
            seconds -= 1
        } else if (seconds == 0 && isRunning) {
            isRunning = false
            if (mode == "work") {
                sessions += 1
                mode = "break"
                seconds = currentBreakDurationMins * 60
            } else {
                mode = "work"
                seconds = currentWorkDurationMins * 60
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Focus Timer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Session ${sessions + 1} • $sessions completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.background(Color(0xFFF3F4F6), CircleShape)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
                }
            }

            // Mode Switcher
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF3F4F6)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ModeTab(
                        selected = mode == "work",
                        label = "Focus",
                        icon = Icons.Outlined.Timer,
                        activeColor = Color(0xFF9C7C50),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mode = "work"
                            isRunning = false
                            seconds = currentWorkDurationMins * 60
                        }
                    )
                    ModeTab(
                        selected = mode == "break",
                        label = "Break",
                        icon = Icons.Outlined.Coffee,
                        activeColor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mode = "break"
                            isRunning = false
                            seconds = currentBreakDurationMins * 60
                        }
                    )
                }
            }

            // Progress Ring & Analog Timer Picker
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .pointerInput(isRunning) {
                        if (!isRunning) {
                            detectDragGestures { change, _ ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val touchPos = change.position
                                
                                val angleRad = atan2(touchPos.y - center.y, touchPos.x - center.x)
                                var angleDeg = (angleRad * 180f / PI).toFloat() + 90f
                                if (angleDeg < 0) angleDeg += 360f
                                
                                val selectedMinutes = (angleDeg / 360f * 60f).roundToInt().coerceIn(1, 60)
                                
                                if (mode == "work") {
                                    currentWorkDurationMins = selectedMinutes
                                } else {
                                    currentBreakDurationMins = selectedMinutes
                                }
                                seconds = selectedMinutes * 60
                                change.consume()
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    // Background track
                    drawCircle(
                        color = activeColor.copy(alpha = 0.1f),
                        style = Stroke(width = 16.dp.toPx())
                    )
                    // Progress arc
                    drawArc(
                        color = activeColor,
                        startAngle = -90f,
                        sweepAngle = 360f * (if (isRunning) progress else 1f), // Show full ring when picking time
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Analog Handle (only when paused)
                    if (!isRunning) {
                        val handleAngle = ( (seconds / 60f) / 60f * 360f ) - 90f
                        val radius = 110.dp.toPx()
                        val handleX = center.x + radius * kotlin.math.cos(handleAngle * PI / 180f).toFloat()
                        val handleY = center.y + radius * kotlin.math.sin(handleAngle * PI / 180f).toFloat()
                        
                        drawCircle(
                            color = Color.White,
                            radius = 12.dp.toPx(),
                            center = Offset(handleX, handleY)
                        )
                        drawCircle(
                            color = activeColor,
                            radius = 8.dp.toPx(),
                            center = Offset(handleX, handleY),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(activeColor, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (mode == "work") Icons.Outlined.Timer else Icons.Outlined.Coffee,
                            null,
                            tint = Color.White
                        )
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        formatTime(seconds),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (mode == "work") "Stay focused!" else "Take a breather",
                        style = MaterialTheme.typography.bodyMedium,
                        color = activeColor,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (!isRunning) {
                        Text(
                            "Rotate to adjust",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset
                IconButton(
                    onClick = {
                        isRunning = false
                        seconds = if (mode == "work") currentWorkDurationMins * 60 else currentBreakDurationMins * 60
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, null, tint = Color.Gray)
                }

                Spacer(Modifier.width(24.dp))

                // Play/Pause
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(activeColor)
                        .clickable { isRunning = !isRunning },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(Modifier.width(24.dp))

                // Skip
                IconButton(
                    onClick = {
                        isRunning = false
                        if (mode == "work") {
                            sessions += 1
                            mode = "break"
                            seconds = currentBreakDurationMins * 60
                        } else {
                            mode = "work"
                            seconds = currentWorkDurationMins * 60
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                ) {
                    Icon(Icons.Default.SkipNext, null, tint = Color.Gray)
                }
            }

            // Sessions Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) { i ->
                        val active = i < (sessions % 4)
                        val width by animateDpAsState(if (active) 22.dp else 8.dp)
                        val color by animateColorAsState(if (active) activeColor else Color.LightGray)
                        
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
                Text(
                    "${sessions % 4}/4 sessions before long break",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ModeTab(
    selected: Boolean,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color.White else Color.Transparent,
        tonalElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                tint = if (selected) activeColor else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.Black else Color.Gray
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
