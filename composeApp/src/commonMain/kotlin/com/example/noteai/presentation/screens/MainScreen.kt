package com.example.noteai.presentation.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.AppState
import com.example.noteai.presentation.JourneyLog
import com.example.noteai.presentation.MENTAL_THEMES
import com.example.noteai.presentation.components.MoveInBottomNav
import com.example.noteai.presentation.screens.home.*
import com.example.noteai.presentation.theme.NoteAITheme

@Composable
fun MainScreen(
    userName: String,
    isLightMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    mentalState: AppState,
    onMentalStateChange: (AppState) -> Unit,
    momentum: Int,
    onMomentumChange: (Int) -> Unit,
    logs: List<JourneyLog>,
    onLogsChange: (List<JourneyLog>) -> Unit,
    onLogout: () -> Unit
) {
    var activeTab by rememberSaveable { mutableStateOf("home") }
    val theme = MENTAL_THEMES[mentalState] ?: MENTAL_THEMES[AppState.NEUTRAL]!!
    val currentBackground = if (isLightMode) theme.bgLight else theme.bgDark

    NoteAITheme(darkTheme = !isLightMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentBackground)
        ) {
            // Ambient Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-40).dp)
                    .blur(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(theme.glow, Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Top Bar / Header
                HeaderApp(
                    isLight = isLightMode,
                    onReset = { onMentalStateChange(AppState.NEUTRAL) }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val screenModifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 104.dp)

                    Crossfade(targetState = activeTab, animationSpec = tween(500)) { tab ->
                        when (tab) {
                            "home" -> HomeScreen(
                                theme = theme,
                                isLight = isLightMode,
                                addJourneyLog = { onLogsChange(listOf(it) + logs) },
                                addMomentum = { onMomentumChange(momentum + it) },
                                appState = mentalState,
                                setAppState = onMentalStateChange,
                                modifier = screenModifier
                            )
                            "journey" -> LogScreen(
                                logs = logs,
                                clearLogs = { onLogsChange(emptyList()) },
                                isLight = isLightMode,
                                modifier = screenModifier
                            )
                            "detox" -> DetoxScreen(
                                theme = theme,
                                isLight = isLightMode,
                                addJourneyLog = { onLogsChange(listOf(it) + logs) },
                                addMomentum = { onMomentumChange(momentum + it) },
                                setAppState = onMentalStateChange,
                                modifier = screenModifier
                            )
                            "quests" -> GrowthScreen(
                                momentum = momentum,
                                setMomentum = onMomentumChange,
                                isLight = isLightMode,
                                modifier = screenModifier
                            )
                            "profile" -> ProfileScreen(
                                isLight = isLightMode,
                                setIsLight = onThemeToggle,
                                userName = userName,
                                onLogout = onLogout,
                                modifier = screenModifier
                            )
                        }
                    }
                }
            }

            MoveInBottomNav(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                isLight = isLightMode,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
fun HeaderApp(isLight: Boolean, onReset: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MoveIn.",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF171717) else Color.White
            )
        )
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.1f),
                contentColor = if (isLight) Color(0xFF525252) else Color.White.copy(alpha = 0.7f)
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            shape = CircleShape,
            modifier = Modifier.height(32.dp)
        ) {
            Text("Reset State", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
