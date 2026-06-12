package com.example.bookku.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bookku.data.local.datastore.UserPreferences
import com.example.bookku.presentation.screens.addbook.AddBookScreen
import com.example.bookku.presentation.screens.ai.AIAssistantScreen
import com.example.bookku.presentation.screens.auth.AuthScreen
import com.example.bookku.presentation.screens.bookdetail.BookDetailScreen
import com.example.bookku.presentation.screens.forum.ForumScreen
import com.example.bookku.presentation.screens.home.HomeScreen
import com.example.bookku.presentation.screens.tracker.TrackerScreen
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val userPreferences: UserPreferences = koinInject()
    val userId by userPreferences.userId.collectAsState(initial = null)
    
    val navigationActions = createNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Route.Home, "Beranda", Icons.Default.Home),
        BottomNavItem(Route.Tracker, "Tracker", Icons.Default.TrackChanges),
        BottomNavItem(Route.Forum, "Forum", Icons.Default.Forum)
    )

    val isAuthRoute = currentDestination?.hierarchy?.any { it.route?.contains("Auth") == true } == true
    val showBottomBar = !isAuthRoute && bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route?.contains(item.route::class.simpleName ?: "") == true } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { 
                            it.route?.contains(item.route::class.simpleName ?: "") == true 
                        } == true
                        
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (userId == null) Route.Auth else Route.Home,
            modifier = modifier.padding(innerPadding)
        ) {
            composable<Route.Auth> {
                AuthScreen(
                    onSuccess = { 
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Auth) { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.Home> {
                HomeScreen(
                    onnavigateToAddBook = { navigationActions.navigateToAddBook() },
                    onNavigateToDetail = { noteId -> navigationActions.navigateToBookDetail(noteId) }
                )
            }
            
            composable<Route.Tracker> {
                TrackerScreen()
            }
            
            composable<Route.Forum> {
                ForumScreen(
                    onNavigateToDetail = { noteId -> navigationActions.navigateToBookDetail(noteId) }
                )
            }
            
            composable<Route.AddBook> { backStackEntry ->
                val route: Route.AddBook = backStackEntry.toRoute()
                AddBookScreen(
                    noteId = route.noteId,
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }
            
            composable<Route.BookDetail> { backStackEntry ->
                val route: Route.BookDetail = backStackEntry.toRoute()
                BookDetailScreen(
                    noteId = route.noteId,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToEdit = { navigationActions.navigateToAddBook(route.noteId) },
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

data class BottomNavItem(
    val route: Route,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToAuth() {
            navController.navigate(Route.Auth) {
                popUpTo(0) { inclusive = true }
            }
        }

        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = true }
            }
        }

        override fun navigateToTracker() {
            navController.navigate(Route.Tracker)
        }

        override fun navigateToForum() {
            navController.navigate(Route.Forum)
        }
        
        override fun navigateToAddBook(noteId: Long?) {
            navController.navigate(Route.AddBook(noteId))
        }
        
        override fun navigateToBookDetail(noteId: Long) {
            navController.navigate(Route.BookDetail(noteId))
        }
        
        override fun navigateToAIAssistant(noteId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(noteId, initialText))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}
