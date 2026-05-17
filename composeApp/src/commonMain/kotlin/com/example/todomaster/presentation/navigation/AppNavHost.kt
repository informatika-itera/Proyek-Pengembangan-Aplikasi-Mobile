package com.example.todomaster.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.todomaster.presentation.screens.home.HomeScreen
import com.example.todomaster.presentation.screens.addtask.AddTaskScreen
import com.example.todomaster.presentation.screens.detail.TaskDetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.TaskList,
        modifier = modifier
    ) {
        composable<Route.TaskList> {
            HomeScreen(
                onNavigateToAddTask = { navigationActions.navigateToAddTask() },
                onNavigateToTaskDetail = { taskId -> navigationActions.navigateToTaskDetail(taskId) },
                onNavigateToQuadrantDetail = { quadrantId -> navigationActions.navigateToQuadrantDetail(quadrantId) }
            )
        }

        composable<Route.QuadrantDetail> { backStackEntry ->
            val route: Route.QuadrantDetail = backStackEntry.toRoute()
            com.example.todomaster.presentation.screens.quadrantdetail.QuadrantDetailScreen(
                initialQuadrantId = route.quadrantId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToTaskDetail = { taskId -> navigationActions.navigateToTaskDetail(taskId) },
                onNavigateToAddTask = { navigationActions.navigateToAddTask() }
            )
        }

        composable<Route.AddTask> { backStackEntry ->
            val route: Route.AddTask = backStackEntry.toRoute()
            AddTaskScreen(
                taskId = route.taskId,
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        composable<Route.TaskDetail> { backStackEntry ->
            val route: Route.TaskDetail = backStackEntry.toRoute()
            TaskDetailScreen(
                taskId = route.taskId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { navigationActions.navigateToAddTask(route.taskId) },
                onShare = { _ -> }
            )
        }

        composable<Route.Calendar> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Calendar Screen")
            }
        }

        composable<Route.Statistics> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Statistics Screen")
            }
        }

        composable<Route.AIAssistant> { backStackEntry ->
            val route: Route.AIAssistant = backStackEntry.toRoute()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("AI Assistant Screen (Sprint 4)")
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToTaskList() {
            navController.navigate(Route.TaskList) {
                popUpTo(Route.TaskList) { inclusive = true }
            }
        }

        override fun navigateToQuadrantDetail(quadrantId: Long) {
            navController.navigate(Route.QuadrantDetail(quadrantId))
        }

        override fun navigateToAddTask(taskId: Long?) {
            navController.navigate(Route.AddTask(taskId))
        }

        override fun navigateToTaskDetail(taskId: Long) {
            navController.navigate(Route.TaskDetail(taskId))
        }

        override fun navigateToCalendar() {
            navController.navigate(Route.Calendar)
        }

        override fun navigateToStatistics() {
            navController.navigate(Route.Statistics)
        }

        override fun navigateToAIAssistant(taskId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(taskId, initialText))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}