package com.example.mapenumkm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mapenumkm.presentation.screens.addnote.AddNoteScreen
import com.example.mapenumkm.presentation.screens.ai.AIAssistantScreen
import com.example.mapenumkm.presentation.screens.detail.NoteDetailScreen
import com.example.mapenumkm.presentation.screens.history.HistoryScreen
import com.example.mapenumkm.presentation.screens.report.ReportScreen
import com.example.mapenumkm.presentation.screens.home.HomeScreen
import com.example.mapenumkm.presentation.screens.login.LoginScreen
import com.example.mapenumkm.presentation.screens.product.ProductListScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Login,
        modifier = modifier
    ) {
        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
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
                }
            )
        }

        composable<Route.ProductList> {
            ProductListScreen(
                onNavigateToAddProduct = { navigationActions.navigateToAddNote() },
                onNavigateToEditProduct = { noteId ->
                    navigationActions.navigateToAddNote(noteId)
                },
                onNavigateToDashboard = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
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
                onNavigateToTransaksi = {},
                onNavigateToLaporan = {
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
                onNavigateToTransaksi = {},
                onNavigateToRiwayat = {
                    navController.navigate(Route.History) {
                        launchSingleTop = true
                    }
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
                },
                onApplyResult = null
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