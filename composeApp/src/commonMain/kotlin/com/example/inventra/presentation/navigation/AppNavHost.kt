package com.example.inventra.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.inventra.domain.repository.AuthRepository
import com.example.inventra.presentation.screens.addedit.AddEditItemScreen
import com.example.inventra.presentation.screens.ai.AIInventoryScreen
import com.example.inventra.presentation.screens.auth.LoginScreen
import com.example.inventra.presentation.screens.catalog.CatalogScreen
import com.example.inventra.presentation.screens.dashboard.DashboardScreen
import com.example.inventra.presentation.screens.detail.ItemDetailScreen
import com.example.inventra.presentation.screens.history.HistoryScreen
import com.example.inventra.presentation.screens.management.UserDetailScreen
import com.example.inventra.presentation.screens.management.UserManagementScreen
import com.example.inventra.presentation.screens.profile.ProfileScreen
import com.example.inventra.presentation.screens.splash.SplashScreen
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val authRepository: AuthRepository = koinInject()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,   // Sprint 3 fix: MULAI DARI SPLASH
        modifier = modifier,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        // SPLASH SCREEN - Sprint 3 fix
        composable(Routes.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    val destination = if (authRepository.isLoggedIn) {
                        Routes.Dashboard.route
                    } else {
                        Routes.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Dashboard.route) {
            DashboardScreen(
                currentRoute = currentRoute ?: Routes.Dashboard.route,
                onNavigate = { route -> navigateBottomNav(navController, route) },
                onNavigateToAddItem = { navController.navigate(Routes.AddEditItem.route) }
            )
        }

        composable(Routes.Catalog.route) {
            CatalogScreen(
                currentRoute = currentRoute ?: Routes.Catalog.route,
                onNavigate = { route -> navigateBottomNav(navController, route) },
                onNavigateToDetail = { itemId ->
                    navController.navigate(Routes.ItemDetail.createRoute(itemId))
                },
                onNavigateToAddItem = { navController.navigate(Routes.AddEditItem.route) }
            )
        }

        composable(Routes.ItemDetail.route) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toLongOrNull() ?: 0L
            ItemDetailScreen(
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate("${Routes.AddEditItem.route}?itemId=$id")
                }
            )
        }

        composable(Routes.AddEditItem.route) {
            AddEditItemScreen(
                itemId = null,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.AddEditItem.route}?itemId={itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId")?.takeIf { it != -1L }
            AddEditItemScreen(
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(Routes.History.route) {
            HistoryScreen(
                currentRoute = currentRoute ?: Routes.History.route,
                onNavigate = { route -> navigateBottomNav(navController, route) }
            )
        }

        composable(Routes.AskAI.route) {
            AIInventoryScreen(
                currentRoute = currentRoute ?: Routes.AskAI.route,
                onNavigate = { route -> navigateBottomNav(navController, route) }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                currentRoute = currentRoute ?: Routes.Profile.route,
                onNavigate = { route -> navigateBottomNav(navController, route) },
                onLogoutClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToUserManagement = {
                    navController.navigate(Routes.UserManagement.route)
                }
            )
        }

        composable(Routes.UserManagement.route) {
            UserManagementScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { userId ->
                    navController.navigate(Routes.UserDetail.createRoute(userId))
                }
            )
        }

        composable(Routes.UserDetail.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private fun navigateBottomNav(navController: NavHostController, route: String) {
    if (navController.currentDestination?.route != route) {
        navController.navigate(route) {
            popUpTo(Routes.Dashboard.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}