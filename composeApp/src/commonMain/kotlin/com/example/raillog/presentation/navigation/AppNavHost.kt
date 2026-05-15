package com.example.raillog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.raillog.presentation.screens.addsupply.AddSupplyScreen
import com.example.raillog.presentation.screens.ai.AIAssistantScreen
import com.example.raillog.presentation.screens.detail.SupplyDetailScreen
import com.example.raillog.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToAddNote = { navigationActions.navigateToAddSupply() },
                onNavigateToDetail = { itemId -> navigationActions.navigateToSupplyDetail(itemId) },
                onNavigateToAI = { navigationActions.navigateToAIAssistant() }
            )
        }

        composable<Route.AddSupply> { backStackEntry ->
            val route: Route.AddSupply = backStackEntry.toRoute()
            AddSupplyScreen(
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        composable<Route.SupplyDetail> { backStackEntry ->
            val route: Route.SupplyDetail = backStackEntry.toRoute()
            SupplyDetailScreen(
                itemId = route.itemId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { navigationActions.navigateToAddSupply(route.itemId) },
            )
        }

        composable<Route.AIAssistant> { backStackEntry ->
            val route: Route.AIAssistant = backStackEntry.toRoute()
            AIAssistantScreen(
                noteId = route.itemId,
                initialText = route.initialText,
                onNavigateBack = { navigationActions.navigateBack() },
                onApplyResult = null
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = true }
            }
        }

        override fun navigateToAddSupply(itemId: Long?) {
            navController.navigate(Route.AddSupply(itemId))
        }

        override fun navigateToSupplyDetail(itemId: Long) {
            navController.navigate(Route.SupplyDetail(itemId))
        }

        override fun navigateToAIAssistant(itemId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(itemId, initialText))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}