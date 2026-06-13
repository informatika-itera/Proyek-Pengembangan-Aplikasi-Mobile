package com.example.arcane.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.arcane.presentation.screens.bookdetail.BookDetailScreen
import com.example.arcane.presentation.screens.explore.ExploreScreen
import com.example.arcane.presentation.screens.home.HomeScreen
import com.example.arcane.presentation.screens.ai.AIAssistantScreen
import com.example.arcane.presentation.screens.letterbox.LetterboxScreen
import com.example.arcane.presentation.screens.settings.SettingsScreen
import com.example.arcane.presentation.screens.splash.SplashScreen
import com.example.arcane.presentation.screens.folder.FolderDetailScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

data class BottomNavItem(
    val label: String,
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        label = "Beranda",
        route = Route.Home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        label = "Jelajah",
        route = Route.Explore(),
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    ),
    BottomNavItem(
        label = "Letterbox",
        route = Route.Letterbox,
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    ),
    BottomNavItem(
        label = "Pengaturan",
        route = Route.Settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomNav = currentDestination?.let { dest ->
        bottomNavItems.any { dest.hasRoute(it.route::class) }
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hasRoute(item.route::class) == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                when (item.route) {
                                    Route.Home -> navigationActions.navigateToHome()
                                    is Route.Explore -> navigationActions.navigateToExplore("")
                                    Route.Letterbox -> navigationActions.navigateToLetterbox()
                                    Route.Settings -> navigationActions.navigateToSettings()
                                    else -> {}
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash,
            modifier = modifier.padding(paddingValues),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
            composable<Route.Splash> {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Splash) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<Route.Home> {
                HomeScreen(
                    onNavigateToExplore = { navigationActions.navigateToExplore("") },
                    onNavigateToBook = { googleBookId, localBookId ->
                        navigationActions.navigateToBookDetail(googleBookId, localBookId)
                    },
                )
            }

            composable<Route.Explore> { backStackEntry ->
                val exploreRoute: Route.Explore = backStackEntry.toRoute()
                ExploreScreen(
                    initialQuery = exploreRoute.searchQuery,
                    onNavigateToBook = { idDariExplore ->
                        navigationActions.navigateToBookDetail(googleBookId = idDariExplore, localBookId = 0L)
                    }
                )
            }

            composable<Route.Letterbox> {
                LetterboxScreen(
                    onNavigateToBook = { judulBukuDariAI ->
                        navigationActions.navigateToExplore(judulBukuDariAI)
                    },
                    onNavigateToBookDetail = { googleBookId, localBookId ->
                        navigationActions.navigateToBookDetail(googleBookId, localBookId)
                    },
                    onNavigateToFolderDetail = { folderId ->
                        navigationActions.navigateToFolderDetail(folderId)
                    }
                )
            }

            composable<Route.BookDetail> { backStackEntry ->
                val route: Route.BookDetail = backStackEntry.toRoute()
                BookDetailScreen(
                    googleBookId = route.googleBookId,
                    localBookId = route.localBookId,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToResearch = { title, description ->
                        navigationActions.navigateToResearchAssistant(title, description)
                    }
                )
            }

            composable<Route.ResearchAssistant> { backStackEntry ->
                val route: Route.ResearchAssistant = backStackEntry.toRoute()
                AIAssistantScreen(
                    initialText = "Buku: ${route.bookTitle}\n\nDeskripsi: ${route.bookDescription}",
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }

            composable<Route.FolderDetail> { backStackEntry ->
                val route: Route.FolderDetail = backStackEntry.toRoute()
                FolderDetailScreen(
                    folderId = route.folderId,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToBook = { googleBookId, localBookId ->
                        navigationActions.navigateToBookDetail(googleBookId, localBookId)
                    }
                )
            }

            composable<Route.Settings> {
                SettingsScreen()
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToExplore(query: String) {
            navController.navigate(Route.Explore(searchQuery = query)) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToLetterbox() {
            navController.navigate(Route.Letterbox) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToSettings() {
            navController.navigate(Route.Settings) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToBookDetail(googleBookId: String, localBookId: Long) {
            navController.navigate(Route.BookDetail(googleBookId, localBookId))
        }

        override fun navigateToFolderDetail(folderId: Long) {
            navController.navigate(Route.FolderDetail(folderId))
        }

        override fun navigateToResearchAssistant(bookTitle: String, bookDescription: String) {
            navController.navigate(Route.ResearchAssistant(bookTitle, bookDescription))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}