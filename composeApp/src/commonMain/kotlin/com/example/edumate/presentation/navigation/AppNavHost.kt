package com.example.edumate.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.edumate.presentation.screens.settings.SettingsScreen // Tambahkan Import

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToAdd = { navController.navigate(Route.AddEditTask()) },
                onNavigateToDetail = { taskId -> navController.navigate(Route.TaskDetail(taskId)) },
                onNavigateToAIAssistant = { navController.navigate(Route.AIAssistant()) },
                onNavigateToTimer = { navController.navigate(Route.FocusTimer) },
                onNavigateToStatistics = { navController.navigate(Route.Statistics) },
                onNavigateToSettings = { navController.navigate(Route.Settings) } // Inject navigasi
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
                onNavigateToEdit = { taskId -> navController.navigate(Route.AddEditTask(taskId)) },
                onNavigateToAIAssistant = { initialText ->
                    navController.navigate(Route.AIAssistant(taskId = route.taskId, initialText = initialText))
                }
            )
        }

        composable<Route.AIAssistant> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.AIAssistant>()
            AIAssistantScreen(
                noteId = route.taskId,
                initialText = route.initialText,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.FocusTimer> {
            TimerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.Statistics> {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Komponen Navigasi Pengaturan
        composable<Route.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}