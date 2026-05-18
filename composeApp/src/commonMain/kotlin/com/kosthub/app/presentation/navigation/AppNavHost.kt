package com.kosthub.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kosthub.app.presentation.screens.detail.DetailScreen
import com.kosthub.app.presentation.screens.favorites.FavoritesScreen
import com.kosthub.app.presentation.screens.home.HomeScreen
import com.kosthub.app.presentation.screens.contribute.AddEditKostScreen
import com.kosthub.app.presentation.screens.contribute.ContributeScreen
import com.kosthub.app.presentation.screens.contribute.DeleteKostScreen
import com.kosthub.app.presentation.screens.profile.ProfileScreen
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.KostViewModel
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import com.kosthub.app.domain.model.Kost

@Composable
fun AppNavHost(
    navController: NavHostController,
    uiState: UiState<List<Kost>>,
    viewModel: KostViewModel,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier
    ) {
        composable(Routes.Home) {
            HomeScreen(
                uiState = uiState,
                onNavigateDetail = { id -> navController.navigate(Routes.detail(id)) },
                onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
        }
        composable(Routes.Favorites) {
            FavoritesScreen(
                uiState = uiState,
                onNavigateDetail = { id -> navController.navigate(Routes.detail(id)) },
                onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
        }
        composable(Routes.Contribute) {
            ContributeScreen(
                uiState = uiState,
                profileViewModel = profileViewModel,
                onNavigateAdd = { navController.navigate(Routes.ContributeAdd) },
                onNavigateEdit = { id -> navController.navigate(Routes.contributeEdit(id)) },
                onNavigateDelete = { id -> navController.navigate(Routes.contributeDelete(id)) }
            )
        }
        composable(Routes.ContributeAdd) {
            AddEditKostScreen(
                title = "Tambah Kost",
                kostId = null,
                uiState = uiState,
                profileViewModel = profileViewModel,
                operationState = viewModel.operationState.collectAsState().value,
                onClearOperation = { viewModel.clearOperationState() },
                onSave = { viewModel.addKost(it) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.ContributeEdit,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            AddEditKostScreen(
                title = "Edit Kost",
                kostId = id,
                uiState = uiState,
                profileViewModel = profileViewModel,
                operationState = viewModel.operationState.collectAsState().value,
                onClearOperation = { viewModel.clearOperationState() },
                onSave = { viewModel.updateKost(it) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.ContributeDelete,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            DeleteKostScreen(
                kostId = id,
                uiState = uiState,
                operationState = viewModel.operationState.collectAsState().value,
                onClearOperation = { viewModel.clearOperationState() },
                onDelete = { viewModel.deleteKost(it) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.Profile) {
            ProfileScreen(profileViewModel = profileViewModel)
        }
        composable(
            route = Routes.Detail,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            DetailScreen(
                kostId = id,
                uiState = uiState,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
