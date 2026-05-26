package com.example.hujjah.presentation.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.presentation.components.hujjah.HujjahMenuItem
import com.example.hujjah.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.hujjah.presentation.theme.LocalHujjahColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddNote: () -> Unit, // compatibility
    onNavigateToDetail: (Long) -> Unit, // compatibility
    onNavigateToAI: () -> Unit, // compatibility
    viewModel: HomeViewModel = koinViewModel()
) {
    val durationSeconds by viewModel.readingDurationSeconds.collectAsStateWithLifecycle()
    val streakDays by viewModel.currentStreakDays.collectAsStateWithLifecycle()
    val quote by viewModel.quoteOfTheDay.collectAsStateWithLifecycle()
    val lastReadLoc by viewModel.lastReadLocation.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

    val colors = LocalHujjahColors.current
    val textGlow = if (colors.isDarkTheme) {
        androidx.compose.ui.graphics.Shadow(
            color = colors.goldHighlight.copy(alpha = 0.8f),
            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
            blurRadius = 8f
        )
    } else {
        androidx.compose.ui.graphics.Shadow.None
    }
    val targetSeconds = viewModel.dailyTargetSeconds
    val progress = (durationSeconds.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.HOME,
                onNavigateToHome = onNavigateToHome,
                onNavigateToLens = onNavigateToLens,
                onNavigateToQuran = onNavigateToQuran,
                onNavigateToHadith = onNavigateToHadith,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==================== SMART HEADER ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Assalamualaikum,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Awi",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            shadow = textGlow
                        ),
                        fontWeight = FontWeight.Bold,
                        color = colors.goldHighlight
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = colors.goldHighlight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colors.goldHighlight)
                            .border(1.5.dp, colors.goldHighlight, CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // ==================== QURAN ENGAGEMENT DASHBOARD ====================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .padding(vertical = 16.dp)
                    .goldGlowShadow(colors.isDarkTheme, colors.goldHighlight, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (colors.isDarkTheme) {
                        colors.islamicGreen.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(
                    0.5.dp,
                    if (colors.isDarkTheme) colors.goldHighlight.copy(alpha = 0.15f) else colors.goldHighlight.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progres Mengaji Hari Ini",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = textGlow
                        ),
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = if (colors.isDarkTheme) Color.White.copy(alpha = 0.1f) else colors.islamicGreen.copy(alpha = 0.1f),
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round,
                        )

                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = colors.goldHighlight,
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round,
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val minutesRead = durationSeconds / 60
                            val secondsRead = durationSeconds % 60
                            Text(
                                text = "${minutesRead.toString().padStart(2, '0')}:${secondsRead.toString().padStart(2, '0')}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                            )
                            Text(
                                text = "Waktu",
                                fontSize = 12.sp,
                                color = if (colors.isDarkTheme) Color.White.copy(alpha = 0.7f) else colors.islamicGreen.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = 20.sp
                            )
                            Column {
                                Text(
                                    text = "$streakDays Hari",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.goldHighlight,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Konsistensi",
                                    fontSize = 10.sp,
                                    color = if (colors.isDarkTheme) Color.White.copy(alpha = 0.6f) else colors.islamicGreen.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.clickable { onNavigateToQuran() }
                        ) {
                            Text(
                                text = "📖",
                                fontSize = 20.sp
                            )
                            Column {
                                Text(
                                    text = lastReadLoc.ifBlank { "Belum ada" },
                                    fontWeight = FontWeight.Bold,
                                    color = if (colors.isDarkTheme) Color.White else colors.islamicGreen,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Terakhir Baca",
                                    fontSize = 10.sp,
                                    color = if (colors.isDarkTheme) Color.White.copy(alpha = 0.6f) else colors.islamicGreen.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // ==================== CENTERED QUOTE OF THE DAY ====================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "— KUTIPAN HARI INI —",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.goldHighlight,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = quote.arabic,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"${quote.translation}\"",
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = quote.reference,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.goldHighlight
                )
            }

            // ==================== SIMULATION PANEL -> REAL TIMER ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.toggleTimer() },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.islamicGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null, 
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTimerRunning) "Jeda Mengaji" else "Mulai Mengaji", 
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = { viewModel.resetReadingTime() },
                    border = BorderStroke(1.dp, colors.goldHighlight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = colors.goldHighlight)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", color = colors.goldHighlight, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== GOLD GLOW SHADOW EXTENSION MODIFIER ====================
private fun Modifier.goldGlowShadow(
    enabled: Boolean,
    color: Color,
    shape: androidx.compose.ui.graphics.Shape
): Modifier {
    return if (enabled) {
        this
            .border(4.dp, color.copy(alpha = 0.08f), shape)
            .border(2.dp, color.copy(alpha = 0.2f), shape)
            .border(0.5.dp, color.copy(alpha = 0.5f), shape)
    } else {
        this
    }
}
