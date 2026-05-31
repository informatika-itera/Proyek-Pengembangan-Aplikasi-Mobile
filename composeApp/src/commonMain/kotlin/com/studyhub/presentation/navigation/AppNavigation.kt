package com.studyhub.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studyhub.presentation.screens.calendar.CalendarScreen
import com.studyhub.presentation.screens.home.HomeScreen
import com.studyhub.presentation.screens.profile.ProfileScreen
import com.studyhub.presentation.screens.ai.SmartPriorityScreen
import com.studyhub.presentation.screens.task.AddEditTaskScreen
import com.studyhub.presentation.screens.task.TaskDetailScreen
import com.studyhub.presentation.screens.task.TasksScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        enterTransition = {
            fadeIn(tween(250)) + slideInHorizontally(tween(250)) { 30 }
        },
        exitTransition = {
            fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { -30 }
        },
        popEnterTransition = {
            fadeIn(tween(250)) + slideInHorizontally(tween(250)) { -30 }
        },
        popExitTransition = {
            fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { 30 }
        }
    ) {
        composable(Screen.Main.route) {
            MainScreen(navController)
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            TaskDetailScreen(
                taskId = backStackEntry.arguments?.getString("taskId") ?: "",
                navController = navController
            )
        }

        composable(
            route = Screen.AddTask.route,
            arguments = listOf(navArgument("date") {
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { backStackEntry ->
            AddEditTaskScreen(
                date = backStackEntry.arguments?.getString("date"),
                taskId = null,
                navController = navController
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddEditTaskScreen(
                date = null,
                taskId = backStackEntry.arguments?.getString("taskId") ?: "",
                navController = navController
            )
        }

        composable(Screen.SmartPriority.route) {
            SmartPriorityScreen(navController)
        }

        composable(Screen.Progress.route) {
            ProgressScreen(navController)
        }

        composable(
            route = Screen.Pomodoro.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            PomodoroScreen(
                taskId = backStackEntry.arguments?.getString("taskId"),
                navController = navController
            )
        }
    }
}

@Composable
fun ProgressScreen(navController: NavController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Progress Screen — Sprint 3 P1")
    }
}

@Composable
fun PomodoroScreen(
    taskId: String?,
    navController: NavController
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pomodoro Screen — Sprint 3 P1")
    }
}

@Composable
fun MainScreen(rootNavController: NavController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            StudyHubBottomBar(
                currentRoute = currentRoute ?: Screen.Home.route,
                onItemSelected = { screen ->
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationRoute!!) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
            enterTransition = { 
                fadeIn(tween(250)) + slideInHorizontally(tween(250)) { 30 }
            },
            exitTransition = { 
                fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { -30 }
            }
        ) {
            composable(Screen.Home.route) { HomeScreen(rootNavController) }
            composable(Screen.Tasks.route) { TasksScreen(rootNavController) }
            composable(Screen.Calendar.route) { CalendarScreen(rootNavController) }
            composable(Screen.Profile.route) { ProfileScreen(rootNavController) }
        }
    }
}
