package id.pusakakata.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import id.pusakakata.ui.screens.about.AboutScreen
import id.pusakakata.ui.screens.addedit.AddEditScreen
import id.pusakakata.ui.screens.addedit.AddEditViewModel
import id.pusakakata.ui.screens.detail.DetailScreen
import id.pusakakata.ui.screens.detail.DetailViewModel
import id.pusakakata.ui.screens.collection.CollectionScreen
import id.pusakakata.ui.screens.collection.CollectionViewModel
import id.pusakakata.ui.screens.favorite.FavoriteScreen
import id.pusakakata.ui.screens.favorite.FavoriteViewModel
import id.pusakakata.ui.screens.flashcard.FlashcardScreen
import id.pusakakata.ui.screens.flashcard.FlashcardViewModel
import id.pusakakata.ui.screens.gacha.GachaScreen
import id.pusakakata.ui.screens.gacha.GachaViewModel
import id.pusakakata.ui.screens.home.HomeScreen
import id.pusakakata.ui.screens.home.HomeViewModel
import id.pusakakata.ui.screens.profile.ProfileScreen
import id.pusakakata.ui.screens.profile.ProfileViewModel
import id.pusakakata.ui.screens.quiz.QuizScreen
import id.pusakakata.ui.screens.quiz.QuizViewModel
import id.pusakakata.ui.screens.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarRoutes = listOf(Routes.Home.route, Routes.Favorite.route, Routes.Profile.route)
    val shouldShowBottomBar = currentDestination?.route in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple("Beranda", Routes.Home.route, Icons.Default.Home),
                        Triple("Favorit", Routes.Favorite.route, Icons.Default.Favorite),
                        Triple("Profil", Routes.Profile.route, Icons.Default.Person)
                    )
                    items.forEach { (label, route, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().route ?: Routes.Home.route) {
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
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.Home.route) {
                val viewModel: HomeViewModel = koinViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onAddWord = { navController.navigate(Routes.AddEdit.passId()) },
                    onWordClick = { id -> navController.navigate(Routes.Detail.passId(id)) },
                    onNavigateToGacha = { navController.navigate(Routes.Gacha.route) },
                    onNavigateToSettings = { navController.navigate(Routes.Settings.route) },
                    onNavigateToQuiz = { navController.navigate(Routes.Quiz.route) }
                )
            }
            composable(Routes.Favorite.route) {
                val viewModel: FavoriteViewModel = koinViewModel()
                FavoriteScreen(
                    viewModel = viewModel,
                    onWordClick = { id -> navController.navigate(Routes.Detail.passId(id)) }
                )
            }
            composable(Routes.Profile.route) {
                val viewModel: ProfileViewModel = koinViewModel()
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { navController.navigate(Routes.Settings.route) },
                    onNavigateToCollection = { navController.navigate(Routes.Collection.route) }
                )
            }
            composable(Routes.Collection.route) {
                val viewModel: CollectionViewModel = koinViewModel()
                CollectionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.AddEdit.route,
                arguments = listOf(navArgument("wordId") { nullable = true })
            ) { backStackEntry ->
                val wordId = backStackEntry.arguments?.getString("wordId")
                val viewModel: AddEditViewModel = koinViewModel { parametersOf(wordId) }
                AddEditScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.Detail.route,
                arguments = listOf(navArgument("wordId") { nullable = false })
            ) { backStackEntry ->
                val wordId = backStackEntry.arguments?.getString("wordId")!!
                val viewModel: DetailViewModel = koinViewModel { parametersOf(wordId) }
                val homeViewModel: HomeViewModel = koinViewModel()
                DetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Routes.AddEdit.passId(id)) },
                    onDelete = { id -> homeViewModel.deleteWord(id) },
                    onToggleFavorite = { id -> homeViewModel.toggleFavorite(id) }
                )
            }
            composable(Routes.Gacha.route) {
                val viewModel: GachaViewModel = koinViewModel()
                GachaScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.About.route) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToAbout = { navController.navigate(Routes.About.route) }
                )
            }
            composable(Routes.Flashcard.route) {
                val viewModel: FlashcardViewModel = koinViewModel()
                FlashcardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Quiz.route) {
                val viewModel: QuizViewModel = koinViewModel()
                QuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
