package com.example.movein.presentation.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.AppState
import com.example.movein.presentation.JourneyLog
import com.example.movein.presentation.MentalTheme
import com.example.movein.presentation.components.BentoCard
import kotlinx.coroutines.delay

@Composable
fun DetoxScreen(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    setAppState: (AppState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(theme.gap)
    ) {
        DetoxHeaderCard(
            theme = theme,
            isLight = isLight
        )

        ZenBreathingCard(
            theme = theme,
            isLight = isLight,
            addJourneyLog = addJourneyLog,
            addMomentum = addMomentum,
            setAppState = setAppState
        )

        CozyGameHubSection(
            theme = theme,
            isLight = isLight,
            addJourneyLog = addJourneyLog,
            addMomentum = addMomentum,
            setAppState = setAppState,
            modifier = Modifier.fillMaxWidth()
        )

        DoomscrollLockCard(
            theme = theme,
            isLight = isLight,
            addJourneyLog = addJourneyLog,
            addMomentum = addMomentum,
            setAppState = setAppState
        )
    }
}

@Composable
private fun DetoxHeaderCard(
    theme: MentalTheme,
    isLight: Boolean
) {
    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLight) theme.accentLight.copy(alpha = 0.12f)
                        else theme.accentDark.copy(alpha = 0.16f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = if (isLight) theme.accentLight else theme.accentDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Detox Space",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )

                Text(
                    text = "Breathing, cozy mini-game, dan doomscroll lock.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }
        }
    }
}

@Composable
private fun ZenBreathingCard(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    setAppState: (AppState) -> Unit
) {
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var phaseIndex by rememberSaveable { mutableStateOf(0) }
    var completedCycle by rememberSaveable { mutableStateOf(0) }

    val phases = listOf(
        BreathingPhase(
            title = "Tarik napas",
            subtitle = "Isi paru-paru pelan-pelan.",
            scale = 1.18f
        ),
        BreathingPhase(
            title = "Tahan sebentar",
            subtitle = "Diam sebentar. Kamu aman.",
            scale = 1.05f
        ),
        BreathingPhase(
            title = "Hembuskan",
            subtitle = "Lepaskan pelan-pelan.",
            scale = 0.82f
        )
    )

    val currentPhase = phases[phaseIndex]

    val animatedScale by animateFloatAsState(
        targetValue = if (isRunning) currentPhase.scale else 0.9f,
        animationSpec = tween(durationMillis = 1400),
        label = "BreathingScale"
    )

    LaunchedEffect(isRunning, phaseIndex) {
        if (isRunning) {
            delay(2600)

            if (phaseIndex == phases.lastIndex) {
                phaseIndex = 0
                completedCycle += 1
            } else {
                phaseIndex += 1
            }
        }
    }

    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = if (isLight) theme.accentLight else theme.accentDark,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Zen Breathing",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )

                Text(
                    text = "Latihan napas singkat untuk reset pikiran.",
                    fontSize = 11.sp,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }

            Text(
                text = "$completedCycle cycle",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size((150 * animatedScale).dp)
                    .clip(CircleShape)
                    .background(
                        if (isLight) theme.accentLight.copy(alpha = 0.13f)
                        else theme.accentDark.copy(alpha = 0.18f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isLight) theme.accentLight.copy(alpha = 0.28f)
                        else theme.accentDark.copy(alpha = 0.32f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRunning) currentPhase.title else "Mulai",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) Color(0xFF171717) else Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isRunning) currentPhase.subtitle else "Tap tombol di bawah",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center,
                        color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isRunning = !isRunning

                if (isRunning) {
                    setAppState(AppState.RECOVERING)
                }
            },
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLight) Color(0xFF171717) else Color.White,
                contentColor = if (isLight) Color.White else Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.CheckCircle else Icons.Default.Stars,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isRunning) "Pause Breathing" else "Start Breathing",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (completedCycle > 0) {
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    addMomentum(6)
                    setAppState(AppState.RECOVERING)

                    addJourneyLog(
                        JourneyLog(
                            time = "Baru saja",
                            mood = "Detox",
                            task = "Zen Breathing",
                            result = "$completedCycle breathing cycle +6",
                            type = "breathing",
                            appState = AppState.RECOVERING,
                            color = if (isLight) theme.accentLight else theme.accentDark,
                            bgColor = if (isLight) {
                                theme.accentLight.copy(alpha = 0.10f)
                            } else {
                                theme.accentDark.copy(alpha = 0.10f)
                            }
                        )
                    )

                    completedCycle = 0
                    phaseIndex = 0
                    isRunning = false
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Simpan hasil breathing",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )
            }
        }
    }
}

@Composable
private fun DoomscrollLockCard(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    setAppState: (AppState) -> Unit
) {
    var isLocked by rememberSaveable { mutableStateOf(false) }
    var selectedMinutes by rememberSaveable { mutableStateOf(5) }
    var secondsLeft by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(isLocked) {
        while (isLocked && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }

        if (isLocked && secondsLeft <= 0) {
            isLocked = false
            addMomentum(10)
            setAppState(AppState.RECOVERING)

            addJourneyLog(
                JourneyLog(
                    time = "Baru saja",
                    mood = "Detox",
                    task = "Doomscroll Lock",
                    result = "$selectedMinutes menit selesai +10",
                    type = "doomscroll_lock",
                    appState = AppState.RECOVERING,
                    color = Color(0xFF10B981),
                    bgColor = Color(0xFF10B981).copy(alpha = 0.10f)
                )
            )
        }
    }

    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = null,
                tint = if (isLight) theme.accentLight else theme.accentDark,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Doomscroll Lock",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )

                Text(
                    text = "Kunci diri dari scroll impulsif sebentar.",
                    fontSize = 11.sp,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isLight) Color.White.copy(alpha = 0.76f)
                    else Color.Black.copy(alpha = 0.24f)
                )
                .border(
                    width = 1.dp,
                    color = if (isLight) theme.accentLight.copy(alpha = 0.16f)
                    else theme.accentDark.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(18.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isLocked) {
                        formatTimer(secondsLeft)
                    } else {
                        "$selectedMinutes menit"
                    },
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isLocked) {
                        "Jangan buka aplikasi distraksi dulu."
                    } else {
                        "Pilih durasi detox singkat."
                    },
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )

                if (!isLocked) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15).forEach { minute ->
                            DurationChip(
                                minute = minute,
                                selected = selectedMinutes == minute,
                                theme = theme,
                                isLight = isLight,
                                onClick = {
                                    selectedMinutes = minute
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isLocked) {
                    isLocked = false
                    secondsLeft = 0
                } else {
                    secondsLeft = selectedMinutes * 60
                    isLocked = true
                    setAppState(AppState.RECOVERING)
                }
            },
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLocked) {
                    Color(0xFFEF4444)
                } else {
                    if (isLight) Color(0xFF171717) else Color.White
                },
                contentColor = if (isLocked || isLight) Color.White else Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = if (isLocked) Icons.Default.Refresh else Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isLocked) "Batalkan Lock" else "Mulai Lock",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DurationChip(
    minute: Int,
    selected: Boolean,
    theme: MentalTheme,
    isLight: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) {
                    if (isLight) theme.accentLight.copy(alpha = 0.16f)
                    else theme.accentDark.copy(alpha = 0.20f)
                } else {
                    if (isLight) Color.Black.copy(alpha = 0.05f)
                    else Color.White.copy(alpha = 0.08f)
                }
            )
            .border(
                width = 1.dp,
                color = if (selected) {
                    if (isLight) theme.accentLight.copy(alpha = 0.35f)
                    else theme.accentDark.copy(alpha = 0.35f)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .then(
                Modifier.background(Color.Transparent)
            )
    ) {
        Text(
            text = "${minute}m",
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) {
                if (isLight) theme.accentLight else theme.accentDark
            } else {
                if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
            },
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Transparent)
        )
    }

    LaunchedEffect(Unit) {
        // No-op, intentionally empty.
    }
}

private data class BreathingPhase(
    val title: String,
    val subtitle: String,
    val scale: Float
)

private fun formatTimer(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return "${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
}