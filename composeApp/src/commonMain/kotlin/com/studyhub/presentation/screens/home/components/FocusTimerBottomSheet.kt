package com.studyhub.presentation.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.core.manager.PomodoroManager
import com.studyhub.domain.model.PomodoroPhase
import org.koin.compose.koinInject
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerBottomSheet(
    onDismiss: () -> Unit
) {
    val pomodoroManager: PomodoroManager = koinInject()
    val state by pomodoroManager.state.collectAsState()
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val progress = if (state.totalSeconds > 0) 1f - (state.timeRemainingSeconds.toFloat() / state.totalSeconds.toFloat()) else 0f
    
    val activeColor = if (state.phase == PomodoroPhase.FOCUS) Color(0xFF9C7C50) else Color(0xFF10B981)

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
                        "Session ${state.currentSession} • ${state.completedSessionsToday} completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.background(Color(0xFFF3F4F6), CircleShape)
                ) {
                    Icon(Icons.Default.Close, "Tutup", modifier = Modifier.size(20.dp))
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
                        selected = state.phase == PomodoroPhase.FOCUS,
                        label = "Focus",
                        icon = Icons.Outlined.Timer,
                        activeColor = Color(0xFF9C7C50),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Manual phase switch if not running
                        }
                    )
                    ModeTab(
                        selected = state.phase != PomodoroPhase.FOCUS,
                        label = "Break",
                        icon = Icons.Outlined.Coffee,
                        activeColor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Manual phase switch if not running
                        }
                    )
                }
            }

            // Progress Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    drawCircle(
                        color = activeColor.copy(alpha = 0.1f),
                        style = Stroke(width = 16.dp.toPx())
                    )
                    drawArc(
                        color = activeColor,
                        startAngle = -90f,
                        sweepAngle = 360f * (if (state.isRunning) progress else 1f),
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
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
                            if (state.phase == PomodoroPhase.FOCUS) Icons.Outlined.Timer else Icons.Outlined.Coffee,
                            null,
                            tint = Color.White
                        )
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        formatTime(state.timeRemainingSeconds),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (state.phase == PomodoroPhase.FOCUS) "Stay focused!" else "Take a breather",
                        style = MaterialTheme.typography.bodyMedium,
                        color = activeColor,
                        fontWeight = FontWeight.Medium
                    )
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
                        pomodoroManager.resetTimer()
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, "Reset", tint = Color.Gray)
                }

                Spacer(Modifier.width(24.dp))

                // Play/Pause
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(activeColor)
                        .clickable { 
                            if (state.isRunning) pomodoroManager.pauseTimer() else pomodoroManager.startTimer()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        if (state.isRunning) "Pause" else "Mulai",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(Modifier.width(24.dp))

                // Skip
                IconButton(
                    onClick = {
                        pomodoroManager.skipPhase()
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                ) {
                    Icon(Icons.Default.SkipNext, "Lewati", tint = Color.Gray)
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
                        val active = i < (state.completedSessionsToday % 4)
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
                    "${state.completedSessionsToday % 4}/4 sessions before long break",
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
