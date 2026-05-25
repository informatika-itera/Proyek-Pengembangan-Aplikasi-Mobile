package com.example.inventra.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.inventra.presentation.screens.addedit.AddEditItemScreen
import com.example.inventra.presentation.screens.ai.AIInventoryScreen
import com.example.inventra.presentation.screens.catalog.CatalogScreen
import com.example.inventra.presentation.screens.dashboard.DashboardScreen
import com.example.inventra.presentation.screens.detail.ItemDetailScreen
import com.example.inventra.presentation.screens.history.HistoryScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Dashboard,
        modifier = modifier
    ) {
        composable<Route.Dashboard> {
            DashboardScreen(
                onNavigateToAddItem = { navigationActions.navigateToAddEditItem() },
                onNavigateToDetail = { itemId -> navigationActions.navigateToItemDetail(itemId) },
                onNavigateToCatalog = { navigationActions.navigateToCatalog() },
                onNavigateToAI = { navigationActions.navigateToAIAssistant() }
            )
        }

        composable<Route.Catalog> {
            CatalogScreen(
                onNavigateToDetail = { itemId -> navigationActions.navigateToItemDetail(itemId) }
            )
        }

        composable<Route.History> {
            HistoryScreen()
        }

        composable<Route.AIAssistant> {
            AIInventoryScreen(
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        composable<Route.ItemDetail> { backStackEntry ->
            val route: Route.ItemDetail = backStackEntry.toRoute()
            ItemDetailScreen(
                itemId = route.itemId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { itemId -> navigationActions.navigateToAddEditItem(itemId) }
            )
        }

        composable<Route.AddEditItem> { backStackEntry ->
            val route: Route.AddEditItem = backStackEntry.toRoute()
            AddEditItemScreen(
                itemId = route.itemId,
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }
    }
}

interface NavigationActions {
    fun navigateToDashboard()
    fun navigateToCatalog()
    fun navigateToHistory()
    fun navigateToAIAssistant()
    fun navigateToItemDetail(itemId: Long)
    fun navigateToAddEditItem(itemId: Long? = null)
    fun navigateBack()
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToDashboard() {
            navController.navigate(Route.Dashboard) {
                popUpTo(Route.Dashboard) { inclusive = true }
            }
        }

        override fun navigateToCatalog() {
            navController.navigate(Route.Catalog)
        }

        override fun navigateToHistory() {
            navController.navigate(Route.History)
        }

        override fun navigateToAIAssistant() {
            navController.navigate(Route.AIAssistant)
        }

        override fun navigateToItemDetail(itemId: Long) {
            navController.navigate(Route.ItemDetail(itemId))
        }

        override fun navigateToAddEditItem(itemId: Long?) {
            navController.navigate(Route.AddEditItem(itemId))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}
