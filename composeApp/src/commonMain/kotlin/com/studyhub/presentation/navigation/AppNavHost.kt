package com.studyhub.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.studyhub.presentation.screens.add_task.AddTaskScreen
import com.studyhub.presentation.screens.task_detail.TaskDetailScreen
import com.studyhub.presentation.screens.home.HomeScreen
import com.studyhub.presentation.screens.profile.ProfileScreen
import com.studyhub.presentation.theme.AppBlack
import com.studyhub.presentation.theme.AppWhite

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val mainScreens = listOf(
        BottomNavItem("Home", Screen.Home, Icons.Default.Home),
        BottomNavItem("Tasks", Screen.Tasks, Icons.Default.Task),
        BottomNavItem("Calendar", Screen.Calendar, Icons.Default.CalendarMonth),
        BottomNavItem("Profile", Screen.Profile, Icons.Default.Person)
    )

    val showBottomBar = mainScreens.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = AppWhite,
                    contentColor = AppBlack,
                    tonalElevation = 0.dp
                ) {
                    mainScreens.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppWhite,
                                unselectedIconColor = Color.Gray,
                                indicatorColor = AppBlack,
                                selectedTextColor = AppBlack,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onNavigateToAddTask = { navController.navigate(Screen.AddTask()) },
                    onNavigateToDetail = { taskId -> navController.navigate(Screen.TaskDetail(taskId)) }
                )
            }
            
            composable<Screen.Tasks> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { 
                    Text("Tasks Screen Placeholder") 
                }
            }

            composable<Screen.Calendar> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { 
                    Text("Calendar Screen Placeholder") 
                }
            }

            composable<Screen.Profile> {
                ProfileScreen()
            }
            
            composable<Screen.AddTask> { backStackEntry ->
                val route: Screen.AddTask = backStackEntry.toRoute()
                AddTaskScreen(
                    taskId = route.taskId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Screen.TaskDetail> { backStackEntry ->
                val route: Screen.TaskDetail = backStackEntry.toRoute()
                TaskDetailScreen(
                    taskId = route.taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { taskId -> navController.navigate(Screen.AddTask(taskId)) }
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: Any,
    val icon: ImageVector
)
