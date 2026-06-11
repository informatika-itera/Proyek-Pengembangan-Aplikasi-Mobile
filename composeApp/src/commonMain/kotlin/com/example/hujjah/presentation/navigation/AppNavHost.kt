package com.example.hujjah.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hujjah.presentation.screens.bookmark.BookmarkScreen
import com.example.hujjah.presentation.screens.home.HomeScreen
import com.example.hujjah.presentation.screens.lens.HujjahLensScreen
import com.example.hujjah.presentation.screens.quran.QuranScreen
import com.example.hujjah.presentation.screens.quran.QuranDetailScreen
import com.example.hujjah.presentation.screens.hadith.HadithScreen
import com.example.hujjah.presentation.screens.profile.ProfileScreen
import com.example.hujjah.presentation.screens.reference.ReferenceDetailScreen
import com.example.hujjah.presentation.screens.result.HujjahResultScreen
import com.example.hujjah.presentation.screens.splash.SplashScreen
import com.example.hujjah.presentation.screens.notes.NotesScreen
import com.example.hujjah.presentation.screens.addnote.AddNoteScreen
import com.example.hujjah.presentation.screens.detail.NoteDetailScreen


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
        startDestination = Route.Splash,
        modifier = modifier
    ) {

        // ==================== SPLASH SCREEN ====================
        composable<Route.Splash> {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }


        // ==================== 1. BERANDA (HOME) ====================
        composable<Route.Home> {
            HomeScreen(
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile,
                onNavigateToAddNote = {},
                onNavigateToDetail = {},
                onNavigateToAI = {}
            )
        }

        // ==================== 2. LENS (AI COUNSELOR) ====================
        composable<Route.HujjahLens> {
            HujjahLensScreen(
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToQuranDetail = navigationActions::navigateToQuranDetail,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile,
                onNavigateToBookmarks = navigationActions::navigateToBookmarks,
                onNavigateToResult = navigationActions::navigateToHujjahResult,
                onNavigateToAddNote = navigationActions::navigateToAddNote
            )
        }

        // ==================== 3. AL-QUR'AN (DIGITAL MUSHAF) ====================
        composable<Route.Quran> {
            QuranScreen(
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile,
                onNavigateToDetail = navigationActions::navigateToQuranDetail
            )
        }

        composable<Route.QuranDetail> { backStackEntry ->
            val route: Route.QuranDetail = backStackEntry.toRoute()
            QuranDetailScreen(
                surahNumber = route.surahNumber,
                surahName = route.surahName,
                verseNumber = route.verseNumber,
                onNavigateBack = navigationActions::navigateBack
            )
        }

        // ==================== 4. HADIS (ENCYCLOPEDIA) ====================
        composable<Route.Hadith> {
            HadithScreen(
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile
            )
        }

        // ==================== 5. PROFIL ====================
        composable<Route.Profile> {
            ProfileScreen(
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile,
                onNavigateToBookmarks = navigationActions::navigateToBookmarks,
                onNavigateToNotes = navigationActions::navigateToNotes
            )
        }

        // ==================== NOTES SCREEN ====================
        composable<Route.Notes> {
            NotesScreen(
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToAddNote = { navigationActions::navigateToAddNote.invoke(null, null) },
                onNavigateToDetail = navigationActions::navigateToNoteDetail
            )
        }

        composable<Route.AddNote> { backStackEntry ->
            val route: Route.AddNote = backStackEntry.toRoute()
            AddNoteScreen(
                noteId = route.noteId,
                initialContent = route.initialContent,
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToAI = { /* optional fallback */ }
            )
        }

        composable<Route.NoteDetail> { backStackEntry ->
            val route: Route.NoteDetail = backStackEntry.toRoute()
            NoteDetailScreen(
                noteId = route.noteId,
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToEdit = { noteId -> navigationActions.navigateToAddNote(noteId, null) },
                onShare = { /* optional share action */ }
            )
        }

        // ==================== SUPPORTING ROUTES ====================
        composable<Route.Bookmark> {
            BookmarkScreen(
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToDetail = navigationActions::navigateToReferenceDetail,
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile
            )
        }

        composable<Route.HujjahResult> { backStackEntry ->
            val route: Route.HujjahResult = backStackEntry.toRoute()
            HujjahResultScreen(
                topicId = route.topicId,
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToDetail = navigationActions::navigateToReferenceDetail,
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile
            )
        }

        composable<Route.ReferenceDetail> { backStackEntry ->
            val route: Route.ReferenceDetail = backStackEntry.toRoute()
            ReferenceDetailScreen(
                referenceId = route.referenceId,
                onNavigateBack = navigationActions::navigateBack,
                onNavigateToHome = navigationActions::navigateToHome,
                onNavigateToLens = navigationActions::navigateToHujjahLens,
                onNavigateToQuran = navigationActions::navigateToQuran,
                onNavigateToHadith = navigationActions::navigateToHadith,
                onNavigateToProfile = navigationActions::navigateToProfile
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToHujjahLens() {
            navController.navigate(Route.HujjahLens) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToQuran() {
            navController.navigate(Route.Quran) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToQuranDetail(surahNumber: Int, surahName: String, verseNumber: Int?) {
            navController.navigate(Route.QuranDetail(surahNumber, surahName, verseNumber))
        }

        override fun navigateToHadith() {
            navController.navigate(Route.Hadith) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
            }
        }

        override fun navigateToProfile() {
            navController.navigate(Route.Profile) {
                popUpTo(Route.Home) { inclusive = false }
                launchSingleTop = true
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

        override fun navigateToNotes() {
            navController.navigate(Route.Notes)
        }

        override fun navigateToNoteDetail(noteId: Long) {
            navController.navigate(Route.NoteDetail(noteId))
        }

        override fun navigateToAddNote(noteId: Long?, initialContent: String?) {
            navController.navigate(Route.AddNote(noteId, initialContent))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}