package com.example.edumate.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.edumate.presentation.screens.add.AddEditScreen
import com.example.edumate.presentation.screens.ai.AIAssistantScreen
import com.example.edumate.presentation.screens.detail.DetailScreen
import com.example.edumate.presentation.screens.home.HomeScreen
import com.example.edumate.presentation.screens.timer.TimerScreen
import com.example.edumate.presentation.screens.statistics.StatisticsScreen
import com.example.edumate.presentation.screens.settings.SettingsScreen
import com.example.edumate.presentation.screens.profile.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navigateFromDrawer = { route: Any ->
        scope.launch { drawerState.close() }
        navController.navigate(route) {
            popUpTo(Route.Home) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawerContent(
                    onNavigateHome = { navigateFromDrawer(Route.Home) },
                    onNavigateProfile = { navigateFromDrawer(Route.Profile) },
                    onNavigateTimer = { navigateFromDrawer(Route.FocusTimer) },
                    onNavigateStatistics = { navigateFromDrawer(Route.Statistics) },
                    onNavigateAI = { navigateFromDrawer(Route.AIAssistant()) },
                    onNavigateSettings = { navigateFromDrawer(Route.Settings) }
                )
            }
        },
        modifier = modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = Route.Home
        ) {
            composable<Route.Home> {
                HomeScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateToAdd = { navController.navigate(Route.AddEditTask()) },
                    onNavigateToDetail = { taskId -> navController.navigate(Route.TaskDetail(taskId)) },
                    onNavigateToAIAssistant = { navController.navigate(Route.AIAssistant()) }
                )
            }

            composable<Route.AddEditTask> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.AddEditTask>()
                AddEditScreen(
                    taskId = route.taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAIAssistant = { initialText ->
                        navController.navigate(Route.AIAssistant(initialText = initialText))
                    }
                )
            }

            composable<Route.TaskDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.TaskDetail>()
                DetailScreen(
                    taskId = route.taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { taskId -> navController.navigate(Route.AddEditTask(taskId)) }
                )
            }

            composable<Route.AIAssistant> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.AIAssistant>()
                AIAssistantScreen(
                    noteId = route.taskId,
                    initialText = route.initialText,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<Route.FocusTimer> {
                TimerScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<Route.Statistics> {
                StatisticsScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<Route.Settings> {
                SettingsScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<Route.Profile> {
                ProfileScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}

@Composable
private fun AppDrawerContent(
    onNavigateHome: () -> Unit,
    onNavigateProfile: () -> Unit,
    onNavigateTimer: () -> Unit,
    onNavigateStatistics: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateAI: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(320.dp)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "EduMate",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Asisten Belajar Akademik",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        NavigationDrawerItem(label = { Text("Beranda") }, icon = { Icon(Icons.Default.Home, null) }, selected = false, onClick = onNavigateHome)
        NavigationDrawerItem(label = { Text("Profil") }, icon = { Icon(Icons.Default.AccountCircle, null) }, selected = false, onClick = onNavigateProfile)
        NavigationDrawerItem(label = { Text("Fokus Belajar") }, icon = { Icon(Icons.Default.Timer, null) }, selected = false, onClick = onNavigateTimer)
        NavigationDrawerItem(label = { Text("Statistik Akademik") }, icon = { Icon(Icons.Default.Assessment, null) }, selected = false, onClick = onNavigateStatistics)
        NavigationDrawerItem(label = { Text("Asisten Cerdas") }, icon = { Icon(Icons.Default.AutoAwesome, null) }, selected = false, onClick = onNavigateAI)
        NavigationDrawerItem(label = { Text("Pengaturan Aplikasi") }, icon = { Icon(Icons.Default.Settings, null) }, selected = false, onClick = onNavigateSettings)

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gunakan panel ini untuk bernavigasi ke fitur lainnya.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}