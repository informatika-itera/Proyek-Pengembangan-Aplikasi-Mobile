package com.studyhub.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studyhub.presentation.components.OfflineBanner
import com.studyhub.presentation.screens.calendar.CalendarScreen
import com.studyhub.presentation.screens.home.HomeScreen
import com.studyhub.presentation.screens.profile.ProfileScreen
import com.studyhub.presentation.screens.ai.SmartPriorityScreen
import com.studyhub.presentation.screens.notification.NotifHistoryScreen
import com.studyhub.presentation.screens.pomodoro.PomodoroScreen
import com.studyhub.presentation.screens.progress.ProgressScreen
import com.studyhub.presentation.screens.report.ReportScreen
import com.studyhub.presentation.screens.task.AddEditTaskScreen
import com.studyhub.presentation.screens.task.TaskDetailScreen
import com.studyhub.presentation.screens.task.TasksScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    openScreen: String? = null,
    taskId: String? = null,
    onScreenOpened: () -> Unit = {}
) {
    val navController = rememberNavController()

    val networkViewModel: NetworkViewModel = koinViewModel()
    val isOnline by networkViewModel.isOnline.collectAsStateWithLifecycle()

    LaunchedEffect(openScreen, taskId) {
        if (openScreen != null) {
            when (openScreen) {
                "task_detail" -> {
                    taskId?.let {
                        navController.navigate(Screen.TaskDetail.createRoute(it))
                    }
                }
                "pomodoro" -> {
                    navController.navigate(Screen.Main.createRoute(openPomodoro = true)) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            }
            onScreenOpened()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OfflineBanner(isOnline = isOnline)

        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.weight(1f),
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
            composable(
                route = Screen.Main.route,
                arguments = listOf(
                    navArgument("openPomodoro") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val openFromArg = backStackEntry.arguments?.getBoolean("openPomodoro") ?: false
                MainScreen(
                    rootNavController = navController, 
                    initialOpenPomodoro = openFromArg || openScreen == "pomodoro"
                )
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

            composable(Screen.NotifHistory.route) {
                NotifHistoryScreen(navController)
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

            composable(Screen.Progress.route) {
                ProgressScreen(navController)
            }

            composable(Screen.Report.route) {
                ReportScreen(navController)
            }
        }
    }
}

@Composable
fun MainScreen(
    rootNavController: NavController,
    initialOpenPomodoro: Boolean = false
) {
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
                            popUpTo(navController.graph.findStartDestination().id) {
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
            composable(Screen.Home.route) { 
                HomeScreen(
                    onNavigateToTasks = { 
                        navController.navigate(Screen.Tasks.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTaskDetail = { taskId -> rootNavController.navigate(Screen.TaskDetail.createRoute(taskId)) },
                    onNavigateToSmartPriority = { rootNavController.navigate(Screen.SmartPriority.route) },
                    onNavigateToNotifHistory = { rootNavController.navigate(Screen.NotifHistory.route) },
                    onNavigateToProgress = { rootNavController.navigate(Screen.Progress.route) },
                    onNavigateToReport = { rootNavController.navigate(Screen.Report.route) },
                    openPomodoro = initialOpenPomodoro
                ) 
            }
            composable(Screen.Tasks.route) { 
                TasksScreen(
                    onNavigateToTaskDetail = { taskId -> rootNavController.navigate(Screen.TaskDetail.createRoute(taskId)) },
                    onNavigateToSmartPriority = { rootNavController.navigate(Screen.SmartPriority.route) }
                ) 
            }
            composable(Screen.Calendar.route) { 
                CalendarScreen(
                    onNavigateToTaskDetail = { taskId -> rootNavController.navigate(Screen.TaskDetail.createRoute(taskId)) }
                ) 
            }
            composable(Screen.Profile.route) { ProfileScreen(rootNavController) }
        }
    }
}
