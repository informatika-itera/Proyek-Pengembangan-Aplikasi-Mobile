package com.example.mybawanggacha.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mybawanggacha.presentation.screens.notes.addnote.AddNoteScreen
import com.example.mybawanggacha.presentation.screens.ai.AIAssistantScreen
import com.example.mybawanggacha.presentation.screens.anime.detail.AnimeDetailScreen
import com.example.mybawanggacha.presentation.screens.anime.list.AnimeListScreen
import com.example.mybawanggacha.presentation.screens.notes.detail.NoteDetailScreen
import com.example.mybawanggacha.presentation.screens.discover.HomeScreen
import com.example.mybawanggacha.presentation.screens.library.editor.LibraryEntryEditorScreen
import com.example.mybawanggacha.presentation.screens.library.list.MyListScreen
import com.example.mybawanggacha.presentation.screens.manga.detail.MangaDetailScreen
import com.example.mybawanggacha.presentation.screens.manga.list.MangaListScreen
import com.example.mybawanggacha.presentation.screens.search.SearchScreen
import com.example.mybawanggacha.presentation.screens.settings.SettingsScreen
import com.example.mybawanggacha.domain.library.model.MediaType

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToAnimeDetail = { malId -> navigationActions.navigateToAnimeDetail(malId) },
                onNavigateToMangaDetail = { malId -> navigationActions.navigateToMangaDetail(malId) },
                onNavigateToMyLibrary = { navigationActions.navigateToMyLibrary() },
                onNavigateToAnimeList = { navigationActions.navigateToAnimeList() },
                onNavigateToMangaList = { navigationActions.navigateToMangaList() },
                onNavigateToSearch = { navigationActions.navigateToSearch() },
                onNavigateToSettings = { navigationActions.navigateToSettings() }
            )
        }

        composable<Route.Search> {
            SearchScreen(
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateHome = { navigationActions.navigateToHome() },
                onNavigateToMyLibrary = { navigationActions.navigateToMyLibrary() },
                onNavigateToAnimeList = { navigationActions.navigateToAnimeList() },
                onNavigateToMangaList = { navigationActions.navigateToMangaList() },
                onNavigateToAnimeDetail = { malId -> navigationActions.navigateToAnimeDetail(malId) },
                onNavigateToMangaDetail = { malId -> navigationActions.navigateToMangaDetail(malId) }
            )
        }

        composable<Route.MyLibrary> {
            MyListScreen(
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateHome = { navigationActions.navigateToHome() },
                onNavigateToAnimeList = { navigationActions.navigateToAnimeList() },
                onNavigateToMangaList = { navigationActions.navigateToMangaList() },
                onNavigateToSearch = { navigationActions.navigateToSearch() },
                onNavigateToDetail = { mediaId, mediaType ->
                    when (mediaType) {
                        MediaType.Anime -> navigationActions.navigateToAnimeDetail(mediaId)
                        MediaType.Manga -> navigationActions.navigateToMangaDetail(mediaId)
                    }
                },
                onEditEntry = { entry ->
                    navigationActions.navigateToLibraryEntryEditor(
                        mediaId = entry.mediaId,
                        mediaType = entry.mediaType.storageKey,
                        title = entry.title,
                        imageUrl = entry.imageUrl,
                        totalCount = entry.progress.total,
                        entryId = entry.id
                    )
                }
            )
        }

        composable<Route.AnimeList> {
            AnimeListScreen(
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateHome = { navigationActions.navigateToHome() },
                onNavigateToMyLibrary = { navigationActions.navigateToMyLibrary() },
                onNavigateToMangaList = { navigationActions.navigateToMangaList() },
                onNavigateToSearch = { navigationActions.navigateToSearch() },
                onNavigateToAnimeDetail = { malId -> navigationActions.navigateToAnimeDetail(malId) }
            )
        }

        composable<Route.MangaList> {
            MangaListScreen(
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateHome = { navigationActions.navigateToHome() },
                onNavigateToMyLibrary = { navigationActions.navigateToMyLibrary() },
                onNavigateToAnimeList = { navigationActions.navigateToAnimeList() },
                onNavigateToSearch = { navigationActions.navigateToSearch() },
                onNavigateToMangaDetail = { malId -> navigationActions.navigateToMangaDetail(malId) }
            )
        }

        composable<Route.MangaDetail> { backStackEntry ->
            val route: Route.MangaDetail = backStackEntry.toRoute()
            MangaDetailScreen(
                malId = route.malId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToAnimeDetail = { malId -> navigationActions.navigateToAnimeDetail(malId) },
                onNavigateToMangaDetail = { malId -> navigationActions.navigateToMangaDetail(malId) },
                onNavigateToLibraryEditor = { manga, entryId ->
                    navigationActions.navigateToLibraryEntryEditor(
                        mediaId = manga.malId,
                        mediaType = MediaType.Manga.storageKey,
                        title = manga.title,
                        imageUrl = manga.imageUrl,
                        totalCount = manga.chapters,
                        entryId = entryId
                    )
                }
            )
        }

        composable<Route.LibraryEntryEditor> { backStackEntry ->
            val route: Route.LibraryEntryEditor = backStackEntry.toRoute()
            LibraryEntryEditorScreen(
                mediaId = route.mediaId,
                mediaType = MediaType.fromStorageKey(route.mediaType),
                title = route.title,
                imageUrl = route.imageUrl,
                totalCount = route.totalCount,
                entryId = route.entryId,
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        composable<Route.Settings> {
            SettingsScreen(
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateHome = { navigationActions.navigateToHome() },
                onNavigateToMyLibrary = { navigationActions.navigateToMyLibrary() },
                onNavigateToAnimeList = { navigationActions.navigateToAnimeList() },
                onNavigateToMangaList = { navigationActions.navigateToMangaList() },
                onNavigateToSearch = { navigationActions.navigateToSearch() }
            )
        }
        
        composable<Route.AddNote> { backStackEntry ->
            val route: Route.AddNote = backStackEntry.toRoute()
            AddNoteScreen(
                noteId = route.noteId,
                onNavigateBack = { navigationActions.navigateBack() },
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
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { navigationActions.navigateToAddNote(route.noteId) },
                onShare = { _ -> }
            )
        }
        
        composable<Route.AIAssistant> { backStackEntry ->
            val route: Route.AIAssistant = backStackEntry.toRoute()
            AIAssistantScreen(
                noteId = route.noteId,
                initialText = route.initialText,
                onNavigateBack = { navigationActions.navigateBack() },
                onApplyResult = null
            )
        }

        composable<Route.AnimeDetail> { backStackEntry ->
            val route: Route.AnimeDetail = backStackEntry.toRoute()
            AnimeDetailScreen(
                malId = route.malId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToAnimeDetail = { malId -> navigationActions.navigateToAnimeDetail(malId) },
                onNavigateToMangaDetail = { malId -> navigationActions.navigateToMangaDetail(malId) },
                onNavigateToLibraryEditor = { anime, entryId ->
                    navigationActions.navigateToLibraryEntryEditor(
                        mediaId = anime.malId,
                        mediaType = MediaType.Anime.storageKey,
                        title = anime.title,
                        imageUrl = anime.imageUrl,
                        totalCount = anime.episodes,
                        entryId = entryId
                    )
                }
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = true }
            }
        }

        override fun navigateToSearch() {
            navController.navigate(Route.Search)
        }

        override fun navigateToMyLibrary() {
            navController.navigate(Route.MyLibrary)
        }

        override fun navigateToAnimeList() {
            navController.navigate(Route.AnimeList)
        }

        override fun navigateToMangaList() {
            navController.navigate(Route.MangaList)
        }

        override fun navigateToLibraryEntryEditor(
            mediaId: Int,
            mediaType: String,
            title: String,
            imageUrl: String?,
            totalCount: Int?,
            entryId: Long?
        ) {
            navController.navigate(
                Route.LibraryEntryEditor(
                    mediaId = mediaId,
                    mediaType = mediaType,
                    title = title,
                    imageUrl = imageUrl,
                    totalCount = totalCount,
                    entryId = entryId
                )
            )
        }

        override fun navigateToSettings() {
            navController.navigate(Route.Settings)
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

        override fun navigateToAnimeDetail(malId: Int) {
            navController.navigate(Route.AnimeDetail(malId))
        }

        override fun navigateToMangaDetail(malId: Int) {
            navController.navigate(Route.MangaDetail(malId))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}
