package com.example.edumate.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.edumate.presentation.screens.add.AddEditScreen
import com.example.edumate.presentation.screens.detail.DetailScreen
import com.example.edumate.presentation.screens.home.HomeScreen

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
                onNavigateToDetail = { taskId -> navController.navigate(Route.TaskDetail(taskId)) }
            )
        }

        composable<Route.AddEditTask> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.AddEditTask>()
            AddEditScreen(
                taskId = route.taskId,
                onNavigateBack = { navController.popBackStack() }
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
    }
}