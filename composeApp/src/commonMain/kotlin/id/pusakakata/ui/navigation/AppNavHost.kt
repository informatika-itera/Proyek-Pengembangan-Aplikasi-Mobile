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
import id.pusakakata.ui.screens.settings.SettingsScreen
import id.pusakakata.ui.screens.quiz.QuizScreen
import id.pusakakata.ui.screens.quiz.QuizViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
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
            val homeViewModel: HomeViewModel = koinViewModel() // To access delete logic
            DetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.AddEdit.passId(id)) },
                onDelete = { id -> homeViewModel.deleteWord(id) }
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
