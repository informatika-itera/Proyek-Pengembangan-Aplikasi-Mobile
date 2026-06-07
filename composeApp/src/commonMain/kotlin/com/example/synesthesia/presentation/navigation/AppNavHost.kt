package com.example.synesthesia.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.presentation.screens.addnote.AddNoteScreen
import com.example.synesthesia.presentation.screens.ai.AIAssistantScreen
import com.example.synesthesia.presentation.screens.detail.MemoryDetailScreen
import com.example.synesthesia.presentation.screens.home.HomeScreen
import com.example.synesthesia.presentation.screens.main.MainScreen
import com.example.synesthesia.presentation.screens.onboarding.OnboardingScreen
import androidx.compose.runtime.getValue
import com.example.synesthesia.presentation.screens.settings.SettingsScreen

import com.example.synesthesia.presentation.app.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = koinViewModel()
) {
    val navigationActions = createNavigationActions(navController)
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = if (isOnboardingCompleted) Route.Constellation else Route.Onboarding,
            modifier = modifier
        ) {
            composable<Route.Onboarding> {
                OnboardingScreen(
                    onFinish = {
                        viewModel.completeOnboarding()
                        navigationActions.navigateToConstellation()
                    }
                )
            }

            composable<Route.Constellation> {
                MainScreen(
                    themeMode = themeMode,
                    isOnline = isOnline,
                    onNavigateToAddNote = { navigationActions.navigateToAddMemory() },
                    onNavigateToDetail = { noteId -> navigationActions.navigateToMemoryDetail(noteId) },
                    onNavigateToAI = { navigationActions.navigateToAIAssistant() },
                    onNavigateToSettings = { navigationActions.navigateToSettings() },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            
            composable<Route.AddMemory> { backStackEntry ->
                val route: Route.AddMemory = backStackEntry.toRoute()
                val aiResult by backStackEntry.savedStateHandle.getStateFlow<String?>("ai_result", null).collectAsStateWithLifecycle()

                AddNoteScreen(
                    noteId = route.memoryId,
                    aiResult = aiResult,
                    onResultConsumed = { backStackEntry.savedStateHandle.remove<String>("ai_result") },
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToAI = { text ->
                        navigationActions.navigateToAIAssistant(
                            noteId = route.memoryId,
                            initialText = text
                        )
                    }
                )
            }
            
            composable<Route.MemoryDetail> { backStackEntry ->
                val route: Route.MemoryDetail = backStackEntry.toRoute()
                MemoryDetailScreen(
                    noteId = route.memoryId,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToEdit = { navigationActions.navigateToAddMemory(it) },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            
            composable<Route.AIAssistant> { backStackEntry ->
                val route: Route.AIAssistant = backStackEntry.toRoute()
                AIAssistantScreen(
                    noteId = route.noteId,
                    initialText = route.initialText,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onApplyResult = { text ->
                        navController.previousBackStackEntry?.savedStateHandle?.set("ai_result", text)
                    }
                )
            }

            composable<Route.Settings> {
                SettingsScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToOnboarding() {
            navController.navigate(Route.Onboarding) {
                popUpTo(Route.Onboarding) { inclusive = true }
            }
        }

        override fun navigateToConstellation() {
            navController.navigate(Route.Constellation) {
                popUpTo(Route.Constellation) { inclusive = true }
            }
        }
        
        override fun navigateToAddMemory(memoryId: Long?) {
            navController.navigate(Route.AddMemory(memoryId))
        }
        
        override fun navigateToMemoryDetail(memoryId: Long) {
            navController.navigate(Route.MemoryDetail(memoryId))
        }
        
        override fun navigateToAIAssistant(noteId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(noteId, initialText))
        }

        override fun navigateToSettings() {
            navController.navigate(Route.Settings)
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}
