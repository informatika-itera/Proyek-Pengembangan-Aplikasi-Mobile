package com.studyhub.presentation.navigation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studyhub.presentation.screens.calendar.CalendarScreen
import com.studyhub.presentation.screens.home.HomeScreen
import com.studyhub.presentation.screens.profile.ProfileScreen
import com.studyhub.presentation.screens.task.AddEditTaskScreen
import com.studyhub.presentation.screens.task.TaskDetailScreen
import com.studyhub.presentation.screens.task.TasksScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in listOf("home", "tasks", "calendar", "profile")

    val navOrder = listOf(Screen.Home.route, Screen.Tasks.route, Screen.Calendar.route, Screen.Profile.route)

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                StudyHubBottomBar(navController, currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
            enterTransition = { 
                val isRight = navOrder.indexOf(targetState.destination.route) > navOrder.indexOf(initialState.destination.route)
                slideInHorizontally(animationSpec = tween(220, easing = EaseInOut)) { if (isRight) 60 else -60 } + 
                fadeIn(animationSpec = tween(220, easing = EaseInOut))
            },
            exitTransition = { 
                val isRight = navOrder.indexOf(targetState.destination.route) > navOrder.indexOf(initialState.destination.route)
                slideOutHorizontally(animationSpec = tween(220, easing = EaseInOut)) { if (isRight) -60 else 60 } + 
                fadeOut(animationSpec = tween(220, easing = EaseInOut))
            },
            popEnterTransition = { 
                val isRight = navOrder.indexOf(targetState.destination.route) > navOrder.indexOf(initialState.destination.route)
                slideInHorizontally(animationSpec = tween(220, easing = EaseInOut)) { if (isRight) 60 else -60 } + 
                fadeIn(animationSpec = tween(220, easing = EaseInOut))
            },
            popExitTransition = { 
                val isRight = navOrder.indexOf(targetState.destination.route) > navOrder.indexOf(initialState.destination.route)
                slideOutHorizontally(animationSpec = tween(220, easing = EaseInOut)) { if (isRight) -60 else 60 } + 
                fadeOut(animationSpec = tween(220, easing = EaseInOut))
            }
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Tasks.route) { TasksScreen(navController) }
            composable(Screen.Calendar.route) { CalendarScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
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
        }
    }
}
