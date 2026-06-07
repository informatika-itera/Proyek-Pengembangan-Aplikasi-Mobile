package com.example.synesthesia.presentation.screens.main

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.synesthesia.presentation.components.CelestialBackground
import com.example.synesthesia.presentation.navigation.Route
import com.example.synesthesia.presentation.screens.home.HomeScreen
import com.example.synesthesia.presentation.screens.insights.InsightsScreen
import com.example.synesthesia.presentation.screens.sanctuary.SanctuaryScreen
import com.example.synesthesia.presentation.screens.soniczen.SonicZenScreen
import com.example.synesthesia.presentation.theme.BrightYellow
import com.example.synesthesia.presentation.theme.RoyalBlue
import com.example.synesthesia.presentation.theme.ThemeMode

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    themeMode: ThemeMode,
    isOnline: Boolean,
    onNavigateToAddNote: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToSettings: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var currentTab by remember { mutableStateOf<Route>(Route.Constellation) }
    val isAstronomy = themeMode == ThemeMode.ASTRONOMY
    val onBg = if (isAstronomy) Color.White else MaterialTheme.colorScheme.onBackground
    val activeColor = if (isAstronomy) BrightYellow else RoyalBlue

    CelestialBackground(isAstronomyMode = isAstronomy) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AnimatedVisibility(
                    visible = !isOnline,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CloudOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "You're offline. Some AI features may be limited.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = if (isAstronomy) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.02f),
                    contentColor = onBg,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == Route.Constellation,
                        onClick = { currentTab = Route.Constellation },
                        icon = { Icon(Icons.Default.AutoGraph, contentDescription = "Galaxy") },
                        label = { Text("Galaxy") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeColor,
                            selectedTextColor = activeColor,
                            unselectedIconColor = onBg.copy(alpha = 0.5f),
                            unselectedTextColor = onBg.copy(alpha = 0.5f),
                            indicatorColor = activeColor.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == Route.SonicZen,
                        onClick = { currentTab = Route.SonicZen },
                        icon = { Icon(Icons.Default.MusicNote, contentDescription = "Sonic Zen") },
                        label = { Text("Sonic Zen") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeColor,
                            selectedTextColor = activeColor,
                            unselectedIconColor = onBg.copy(alpha = 0.5f),
                            unselectedTextColor = onBg.copy(alpha = 0.5f),
                            indicatorColor = activeColor.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == Route.Sanctuary,
                        onClick = { currentTab = Route.Sanctuary },
                        icon = { Icon(Icons.Default.SelfImprovement, contentDescription = "Sanctuary") },
                        label = { Text("Sanctuary") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeColor,
                            selectedTextColor = activeColor,
                            unselectedIconColor = onBg.copy(alpha = 0.5f),
                            unselectedTextColor = onBg.copy(alpha = 0.5f),
                            indicatorColor = activeColor.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == Route.Insights,
                        onClick = { currentTab = Route.Insights },
                        icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
                        label = { Text("Insights") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeColor,
                            selectedTextColor = activeColor,
                            unselectedIconColor = onBg.copy(alpha = 0.5f),
                            unselectedTextColor = onBg.copy(alpha = 0.5f),
                            indicatorColor = activeColor.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (currentTab) {
                    Route.Constellation -> {
                        HomeScreen(
                            onNavigateToAddNote = onNavigateToAddNote,
                            onNavigateToDetail = onNavigateToDetail,
                            onNavigateToAI = onNavigateToAI,
                            onNavigateToSettings = onNavigateToSettings,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                    Route.SonicZen -> SonicZenScreen()
                    Route.Sanctuary -> SanctuaryScreen()
                    Route.Insights -> InsightsScreen()
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        }
    }
}
