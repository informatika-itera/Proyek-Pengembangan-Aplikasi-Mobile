package id.pusakakata.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import id.pusakakata.ui.screens.home.HomeScreen
import id.pusakakata.ui.screens.home.HomeViewModel
import id.pusakakata.ui.screens.addedit.AddEditScreen
import id.pusakakata.ui.screens.addedit.AddEditViewModel
import id.pusakakata.ui.screens.detail.DetailScreen
import id.pusakakata.ui.screens.detail.DetailViewModel
import id.pusakakata.ui.screens.gacha.GachaScreen
import id.pusakakata.ui.screens.gacha.GachaViewModel
import id.pusakakata.ui.screens.about.AboutScreen
import id.pusakakata.ui.screens.flashcard.FlashcardScreen
import id.pusakakata.ui.screens.flashcard.FlashcardViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PusakaNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = koinViewModel()
            HomeScreen(
                viewModel = viewModel,
                onAddWord = { navController.navigate(Screen.AddEdit.passId()) },
                onWordClick = { id -> navController.navigate(Screen.Detail.passId(id)) },
                onNavigateToGacha = { navController.navigate(Screen.Gacha.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToFlashcard = { navController.navigate(Screen.Flashcard.route) }
            )
        }
        composable(
            route = Screen.AddEdit.route,
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
            route = Screen.Detail.route,
            arguments = listOf(navArgument("wordId") { nullable = false })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId")!!
            val viewModel: DetailViewModel = koinViewModel { parametersOf(wordId) }
            DetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Screen.AddEdit.passId(id)) }
            )
        }
        composable(Screen.Gacha.route) {
            val viewModel: GachaViewModel = koinViewModel()
            GachaScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Flashcard.route) {
            val viewModel: FlashcardViewModel = koinViewModel()
            FlashcardScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
