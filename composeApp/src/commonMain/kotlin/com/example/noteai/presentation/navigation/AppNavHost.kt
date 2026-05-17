package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.noteai.presentation.screens.addnote.AddNoteScreen
import com.example.noteai.presentation.screens.ai.AIAssistantScreen
import com.example.noteai.presentation.screens.bookmark.BookmarkScreen
import com.example.noteai.presentation.screens.detail.NoteDetailScreen
import com.example.noteai.presentation.screens.home.HomeScreen
import com.example.noteai.presentation.screens.lens.HujjahLensScreen
import com.example.noteai.presentation.screens.reference.ReferenceDetailScreen
import com.example.noteai.presentation.screens.result.HujjahResultScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    val demoTopicId = "anger"
    val demoReferenceId = "quran-ali-imran-134"

    NavHost(
        navController = navController,
        startDestination = Route.HujjahLens,
        modifier = modifier
    ) {
        // ==================== HUJJAH SPRINT 2 GRAPH ====================

        composable<Route.HujjahLens> {
            HujjahLensScreen(
                onNavigateToResult = navigationActions::navigateToHujjahResult,
                onNavigateToBookmarks = navigationActions::navigateToBookmarks,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToDemoResult = { navigationActions.navigateToHujjahResult(demoTopicId) },
                onNavigateToDemoDetail = { navigationActions.navigateToReferenceDetail(demoReferenceId) }
            )
        }

        composable<Route.HujjahResult> { backStackEntry ->
            val route: Route.HujjahResult = backStackEntry.toRoute()
            HujjahResultScreen(
                topicId = route.topicId,
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToDetail = navigationActions::navigateToReferenceDetail,
                onNavigateToBookmarks = navigationActions::navigateToBookmarks,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToDemoResult = { navigationActions.navigateToHujjahResult(demoTopicId) },
                onNavigateToDemoDetail = { navigationActions.navigateToReferenceDetail(demoReferenceId) }
            )
        }

        composable<Route.ReferenceDetail> { backStackEntry ->
            val route: Route.ReferenceDetail = backStackEntry.toRoute()
            ReferenceDetailScreen(
                referenceId = route.referenceId,
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToBookmarks = navigationActions::navigateToBookmarks,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToDemoResult = { navigationActions.navigateToHujjahResult(demoTopicId) },
                onNavigateToDemoDetail = { navigationActions.navigateToReferenceDetail(demoReferenceId) }
            )
        }

        composable<Route.Bookmark> {
            BookmarkScreen(
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToDetail = navigationActions::navigateToReferenceDetail,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToDemoResult = { navigationActions.navigateToHujjahResult(demoTopicId) },
                onNavigateToDemoDetail = { navigationActions.navigateToReferenceDetail(demoReferenceId) },
                onNavigateToBookmarks = navigationActions::navigateToBookmarks
            )
        }

        // ==================== LEGACY SPRINT 1 NOTEAI GRAPH ====================
        // Masih disimpan sebagai bukti evolusi project, tetapi bukan start destination Sprint 2.

        composable<Route.Home> {
            HomeScreen(
                onNavigateToAddNote = { navigationActions.navigateToAddNote() },
                onNavigateToDetail = { noteId -> navigationActions.navigateToNoteDetail(noteId) },
                onNavigateToAI = { navigationActions.navigateToAIAssistant() }
            )
        }

        composable<Route.AddNote> { backStackEntry ->
            val route: Route.AddNote = backStackEntry.toRoute()
            AddNoteScreen(
                noteId = route.noteId,
                onNavigateBack = navigationActions::navigateBack,
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
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToEdit = { navigationActions.navigateToAddNote(route.noteId) },
                onShare = { _ -> }
            )
        }

        composable<Route.AIAssistant> { backStackEntry ->
            val route: Route.AIAssistant = backStackEntry.toRoute()
            AIAssistantScreen(
                noteId = route.noteId,
                initialText = route.initialText,
                onNavigateBack = navigationActions::navigateBack,
                onApplyResult = null
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHujjahLens() {
            navController.navigate(Route.HujjahLens) {
                launchSingleTop = true
                popUpTo(Route.HujjahLens) { inclusive = false }
            }
        }

        override fun navigateToHujjahResult(topicId: String) {
            navController.navigate(Route.HujjahResult(topicId))
        }

        override fun navigateToReferenceDetail(referenceId: String) {
            navController.navigate(Route.ReferenceDetail(referenceId))
        }

        override fun navigateToBookmarks() {
            navController.navigate(Route.Bookmark)
        }

        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = true }
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