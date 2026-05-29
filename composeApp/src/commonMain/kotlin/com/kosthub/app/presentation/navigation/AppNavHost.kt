package com.kosthub.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kosthub.app.presentation.screens.detail.DetailScreen
import com.kosthub.app.presentation.screens.favorites.FavoritesScreen
import com.kosthub.app.presentation.screens.home.HomeScreen
import com.kosthub.app.presentation.screens.profile.ProfileScreen
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.KostViewModel
import com.kosthub.app.presentation.viewmodel.HomeViewModel
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.platform.PlatformContext
import com.kosthub.app.platform.LocationTracker

@Composable
fun AppNavHost(
    navController: NavHostController,
    uiState: UiState<List<Kost>>,
    viewModel: KostViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    platformContext: PlatformContext,
    modifier: Modifier = Modifier
) {
    val startDestination = Routes.Home

    NavHost(
        navController = navController,
        startDestination = startDestination!!,
        modifier = modifier
    ) {
        composable(Routes.Home) {
            val homeUiState by homeViewModel.uiState.collectAsState()
            val searchQuery by homeViewModel.searchQuery.collectAsState()
            val selectedDaerah by homeViewModel.selectedDaerah.collectAsState()
            val selectedTipeKos by homeViewModel.selectedTipeKos.collectAsState()

            HomeScreen(
                uiState = homeUiState,
                searchQuery = searchQuery,
                onQueryChange = { homeViewModel.onSearchQueryChange(it) },
                selectedDaerah = selectedDaerah,
                onDaerahChange = { homeViewModel.onDaerahChange(it) },
                selectedTipeKos = selectedTipeKos,
                onTipeKosChange = { homeViewModel.onTipeKosChange(it) },
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

        composable(Routes.Profile) {
            ProfileScreen(
                profileViewModel = profileViewModel,
                locationTracker = remember { LocationTracker(platformContext) }
            )
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

