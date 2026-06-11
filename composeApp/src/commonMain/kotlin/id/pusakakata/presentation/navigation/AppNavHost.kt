package id.pusakakata.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import id.pusakakata.presentation.screens.about.AboutScreen
import id.pusakakata.presentation.screens.addedit.AddEditScreen
import id.pusakakata.presentation.screens.addedit.AddEditViewModel
import id.pusakakata.presentation.screens.detail.DetailScreen
import id.pusakakata.presentation.screens.detail.DetailViewModel
import id.pusakakata.presentation.screens.collection.CollectionScreen
import id.pusakakata.presentation.screens.collection.CollectionViewModel
import id.pusakakata.presentation.screens.flashcard.FlashcardScreen
import id.pusakakata.presentation.screens.flashcard.FlashcardViewModel
import id.pusakakata.presentation.screens.gacha.GachaScreen
import id.pusakakata.presentation.screens.gacha.GachaViewModel
import id.pusakakata.presentation.screens.home.HomeViewModel
import id.pusakakata.presentation.screens.quiz.QuizScreen
import id.pusakakata.presentation.screens.quiz.QuizViewModel
import id.pusakakata.presentation.screens.settings.SettingsScreen
import id.pusakakata.presentation.screens.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Main.route,
        enterTransition = { 
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) + fadeIn(tween(400))
        },
        exitTransition = { 
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) + fadeOut(tween(400))
        },
        popEnterTransition = { 
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) + fadeIn(tween(400))
        },
        popExitTransition = { 
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) + fadeOut(tween(400))
        }
    ) {
        composable(Routes.Main.route) {
            MainScreen(
                onNavigateToAddWord = { navController.navigate(Routes.AddEdit.passId()) },
                onNavigateToDetail = { id -> navController.navigate(Routes.Detail.passId(id)) },
                onNavigateToGacha = { navController.navigate(Routes.Gacha.route) },
                onNavigateToSettings = { navController.navigate(Routes.Settings.route) },
                onNavigateToQuiz = { navController.navigate(Routes.Quiz.route) },
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
                onDelete = { id -> 
                    homeViewModel.deleteWord(id)
                    navController.popBackStack()
                },
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
            val viewModel: SettingsViewModel = koinViewModel()
            SettingsScreen(
                viewModel = viewModel,
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
