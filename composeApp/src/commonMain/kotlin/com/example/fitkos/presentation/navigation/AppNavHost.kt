package com.example.fitkos.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fitkos.presentation.screens.addnote.AddNoteScreen
import com.example.fitkos.presentation.screens.ai.AIAssistantScreen
import com.example.fitkos.presentation.screens.dashboard.DashboardScreen
import com.example.fitkos.presentation.screens.detail.NoteDetailScreen
import com.example.fitkos.presentation.screens.home.HomeScreen
import com.example.fitkos.presentation.screens.splash.SplashScreen
import com.example.fitkos.presentation.screens.settings.SettingsScreen
import com.example.fitkos.presentation.screens.watertracker.WaterTrackerScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute.shouldShowBottomBar()) {
                FitKosBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigateTopLevel(route)
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash,
            modifier = modifier.padding(paddingValues)
        ) {
            composable<Route.Splash> {
                SplashScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Route.Dashboard) {
                            popUpTo(Route.Splash) { inclusive = true }
                        }
                    }
                )
            }
            composable<Route.Dashboard> {
                DashboardScreen(
                    onNavigateToMealLog = { navigationActions.navigateToHome() },
                    onNavigateToAddMeal = { navigationActions.navigateToAddNote() },
                    onNavigateToWaterTracker = { navigationActions.navigateToWaterTracker() },
                    onNavigateToAI = {
                        navigationActions.navigateToAIAssistant(
                            initialText = """
                                Saya penghuni kos dan ingin menjaga hidup sehat dengan budget terbatas.
                                Tolong beri saran makanan sehat hemat, kebiasaan minum air, dan olahraga ringan yang realistis untuk hari ini.
                            """.trimIndent()
                        )
                    }
                )
            }

            composable<Route.Home> {
                HomeScreen(
                    onNavigateToAddNote = { navigationActions.navigateToAddNote() },
                    onNavigateToDetail = { noteId -> navigationActions.navigateToNoteDetail(noteId) },
                    onNavigateToAI = { navigationActions.navigateToAIAssistant() }
                )
            }

            composable<Route.WaterTracker> {
                WaterTrackerScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }

            composable<Route.Settings> {
                SettingsScreen()
            }
            composable<Route.AddNote> { backStackEntry ->
                val route: Route.AddNote = backStackEntry.toRoute()
                AddNoteScreen(
                    noteId = route.noteId,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToAI = { text ->
                        navigationActions.navigateToAIAssistant(
                            noteId = route.noteId,
                            initialText = text
                        )
                    }
                )
            }

            composable<Route.NoteDetail> { backStackEntry ->
                val route: Route.NoteDetail = backStackEntry.toRoute()
                NoteDetailScreen(
                    noteId = route.noteId,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToEdit = { navigationActions.navigateToAddNote(route.noteId) },
                    onShare = { _ -> }
                )
            }

            composable<Route.AIAssistant> { backStackEntry ->
                val route: Route.AIAssistant = backStackEntry.toRoute()
                AIAssistantScreen(
                    noteId = route.noteId,
                    initialText = route.initialText,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onApplyResult = null
                )
            }
        }
    }
}

@Composable
private fun FitKosBottomBar(
    currentRoute: String?,
    onNavigate: (Route) -> Unit
) {
    val items = listOf(
        BottomBarItem(
            label = "Home",
            icon = Icons.Default.Home,
            route = Route.Dashboard,
            routeKey = "Dashboard"
        ),
        BottomBarItem(
            label = "Catatan",
            icon = Icons.Default.List,
            route = Route.Home,
            routeKey = "Home"
        ),
        BottomBarItem(
            label = "Air",
            icon = Icons.Default.WaterDrop,
            route = Route.WaterTracker,
            routeKey = "WaterTracker"
        ),
        BottomBarItem(
            label = "AI",
            icon = Icons.Outlined.AutoAwesome,
            route = Route.AIAssistant(),
            routeKey = "AIAssistant"
        ),
        BottomBarItem(
            label = "Setting",
            icon = Icons.Default.Settings,
            route = Route.Settings,
            routeKey = "Settings"
        )
    )

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute?.contains(item.routeKey) == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    item.route?.let { onNavigate(it) }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label)
                }
            )
        }
    }
}

private data class BottomBarItem(
    val label: String,
    val icon: ImageVector,
    val route: Route?,
    val routeKey: String
)

private fun String?.shouldShowBottomBar(): Boolean {
    return this?.contains("Dashboard") == true ||
            this?.contains("Home") == true ||
            this?.contains("WaterTracker") == true ||
            this?.contains("AIAssistant") == true ||
            this?.contains("Settings") == true
}

private fun NavHostController.navigateTopLevel(route: Route) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true

        popUpTo(Route.Dashboard) {
            saveState = true
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToDashboard() {
            navController.navigateTopLevel(Route.Dashboard)
        }

        override fun navigateToHome() {
            navController.navigateTopLevel(Route.Home)
        }

        override fun navigateToAddNote(noteId: Long?) {
            navController.navigate(Route.AddNote(noteId))
        }

        override fun navigateToNoteDetail(noteId: Long) {
            navController.navigate(Route.NoteDetail(noteId))
        }

        override fun navigateToAIAssistant(noteId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(noteId, initialText))
        }

        override fun navigateToWaterTracker() {
            navController.navigateTopLevel(Route.WaterTracker)
        }

        override fun navigateToSettings() {
            navController.navigateTopLevel(Route.Settings)
        }
        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}