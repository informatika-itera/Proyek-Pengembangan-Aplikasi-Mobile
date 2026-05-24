package com.example.mapenumkm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mapenumkm.data.local.datastore.UserPreferences
import com.example.mapenumkm.presentation.screens.addnote.AddNoteScreen
import com.example.mapenumkm.presentation.screens.ai.AIAssistantScreen
import com.example.mapenumkm.presentation.screens.detail.NoteDetailScreen
import com.example.mapenumkm.presentation.screens.history.HistoryScreen
import com.example.mapenumkm.presentation.screens.report.ReportScreen
import com.example.mapenumkm.presentation.screens.home.HomeScreen
import com.example.mapenumkm.presentation.screens.login.LoginScreen
import com.example.mapenumkm.presentation.screens.register.RegisterScreen
import com.example.mapenumkm.presentation.screens.forgotpassword.ForgotPasswordScreen
import com.example.mapenumkm.presentation.screens.product.ProductListScreen
import com.example.mapenumkm.presentation.screens.product.ProductListViewModel
import com.example.mapenumkm.presentation.screens.splash.SplashScreen
import com.example.mapenumkm.presentation.screens.transaction.TransactionScreen
import com.example.mapenumkm.presentation.screens.transaction.TransactionViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    userPreferences: UserPreferences = koinInject()
) {
    val navigationActions = createNavigationActions(navController)
    val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = null)

    if (isLoggedIn == null) return

    val startDestination = if (isLoggedIn == true) Route.Home else Route.Splash

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onSplashFinished = {
                    val destination = if (isLoggedIn == true) Route.Home else Route.Login
                    navController.navigate(destination) {
                        popUpTo(Route.Splash) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.ForgotPassword)
                }
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onNavigateToDetail = { noteId ->
                    navigationActions.navigateToNoteDetail(noteId)
                },
                onNavigateToProductList = {
                    navController.navigate(Route.ProductList) {
                        launchSingleTop = true
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Route.History) {
                        launchSingleTop = true
                    }
                },
                onNavigateToReport = {
                    navController.navigate(Route.Report) {
                        launchSingleTop = true
                    }
                },
                onNavigateToTransaction = {
                    navController.navigate(Route.Transaction) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAI = {
                    navigationActions.navigateToAIAssistant()
                },
                onLoggedOut = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Home) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Route.Transaction> {
            val viewModel: TransactionViewModel = koinViewModel()
            TransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable<Route.ProductList> {
            val viewModel: ProductListViewModel = koinViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            ProductListScreen(
                state = state,
                onBackClick = { navController.popBackStack() },
                onAddProductClick = { navigationActions.navigateToAddNote() },
                onEditProductClick = { product ->
                    navigationActions.navigateToAddNote(product.id)
                },
                onDeleteProductClick = { product ->
                    viewModel.deleteProduct(product)
                },
                onSearchQueryChange = { query ->
                    viewModel.onSearchQueryChange(query)
                },
                onCategoryChange = { category ->
                    viewModel.onCategoryChange(category)
                }
            )
        }

        composable<Route.History> {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToProduct = {
                    navController.navigate(Route.ProductList) {
                        launchSingleTop = true
                    }
                },
                onNavigateToTransaction = {
                    navController.navigate(Route.Transaction) {
                        launchSingleTop = true
                    }
                },
                onNavigateToReport = {
                    navController.navigate(Route.Report) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Route.Report> {
            ReportScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToProduct = {
                    navController.navigate(Route.ProductList) {
                        launchSingleTop = true
                    }
                },
                onNavigateToTransaction = {
                    navController.navigate(Route.Transaction) {
                        launchSingleTop = true
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Route.History) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSettings = {
                    // Implementasi jika ada Route.Settings
                },
                onNavigateToProfile = {
                    // Implementasi jika ada Route.Profile
                }
            )
        }

        composable<Route.AddNote> { backStackEntry ->
            val route: Route.AddNote = backStackEntry.toRoute()

            AddNoteScreen(
                noteId = route.noteId,
                onNavigateBack = {
                    navigationActions.navigateBack()
                },
                onNavigateToAI = { text ->
                    navigationActions.navigateToAIAssistant(
                        noteId = route.noteId,
                        initialText = text
                    )
                }
            )
        }

        composable<Route.NoteDetail> { backStackEntry ->
            val route: Route.NoteDetail = backStackEntry.toRoute()

            NoteDetailScreen(
                noteId = route.noteId,
                onNavigateBack = {
                    navigationActions.navigateBack()
                },
                onNavigateToEdit = {
                    navigationActions.navigateToAddNote(route.noteId)
                },
                onShare = { _ -> }
            )
        }

        composable<Route.AIAssistant> { backStackEntry ->
            val route: Route.AIAssistant = backStackEntry.toRoute()
            AIAssistantScreen(
                noteId = route.noteId,
                initialText = route.initialText,
                onNavigateBack = {
                    navigationActions.navigateBack()
                }
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        override fun navigateToAddNote(noteId: Long?) {
            navController.navigate(Route.AddNote(noteId))
        }

        override fun navigateToNoteDetail(noteId: Long) {
            navController.navigate(Route.NoteDetail(noteId))
        }

        override fun navigateToAIAssistant(noteId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(noteId, initialText))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}