package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.noteai.presentation.activity.ActivityGeneratorScreen
import com.example.noteai.presentation.auth.LoginScreen
import com.example.noteai.presentation.auth.RegisterScreen
import com.example.noteai.presentation.mood.MoodSelectionScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    var currentUser by remember { mutableStateOf("Mahasiswa") }
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Login,
        modifier = modifier
    ) {
        composable<Route.Login> {
            LoginScreen(
                onNavigateToRegister = {
                    navigationActions.navigateToRegister()
                },
                onLoginSuccess = { userName ->
                    currentUser = userName.ifBlank { "Mahasiswa" }
                    navigationActions.navigateToMoodSelection(clearBackStack = true)
                }
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                onNavigateBack = {
                    navigationActions.navigateBack()
                },
                onRegisterSuccess = { userName ->
                    currentUser = userName.ifBlank { "Mahasiswa" }
                    navigationActions.navigateToMoodSelection(clearBackStack = true)
                }
            )
        }

        composable<Route.MoodSelection> {
            MoodSelectionScreen(
                userName = currentUser,
                onMoodSelected = { moodId ->
                    navigationActions.navigateToActivityGenerator(moodId)
                },
                onLogout = {
                    currentUser = "Mahasiswa"
                    navigationActions.navigateToLogin()
                }
            )
        }

        composable<Route.ActivityGenerator> { backStackEntry ->
            val route: Route.ActivityGenerator = backStackEntry.toRoute()

            ActivityGeneratorScreen(
                moodId = route.moodId,
                userName = currentUser,
                onNavigateBack = {
                    navigationActions.navigateBack()
                },
                onLogout = {
                    currentUser = "Mahasiswa"
                    navigationActions.navigateToLogin()
                }
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {

        override fun navigateToLogin() {
            navController.navigate(Route.Login) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        override fun navigateToRegister() {
            navController.navigate(Route.Register)
        }

        override fun navigateToMoodSelection(clearBackStack: Boolean) {
            navController.navigate(Route.MoodSelection) {
                if (clearBackStack) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
                launchSingleTop = true
            }
        }

        override fun navigateToActivityGenerator(moodId: String) {
            navController.navigate(Route.ActivityGenerator(moodId))
        }

        override fun navigateToHome() {
            navController.navigate(Route.MoodSelection) {
                launchSingleTop = true
            }
        }

        override fun navigateToAddNote(noteId: Long?) {
            navController.navigate(Route.MoodSelection)
        }

        override fun navigateToNoteDetail(noteId: Long) {
            navController.navigate(Route.MoodSelection)
        }

        override fun navigateToAIAssistant(noteId: Long?, initialText: String?) {
            navController.navigate(Route.MoodSelection)
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}