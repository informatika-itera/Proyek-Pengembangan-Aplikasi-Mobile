package id.my.sinanonym.mybawanggacha.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import id.my.sinanonym.mybawanggacha.presentation.screens.notes.addnote.AddNoteScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.ai.AIAssistantScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.detail.AnimeDetailScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.AnimeListScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.notes.detail.NoteDetailScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.discover.HomeScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.gacha.GachaScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.library.editor.LibraryEntryEditorScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.library.list.MyListScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.manga.detail.MangaDetailScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.manga.list.MangaListScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchScreen
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.SettingsScreen
import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType
import kotlin.time.Clock

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = remember(navController) {
        createNavigationActions(navController)
    }

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
                onNavigateToGacha = { navigationActions.navigateToGacha() },
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
                onNavigateToGacha = { navigationActions.navigateToGacha() },
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
                onNavigateToGacha = { navigationActions.navigateToGacha() },
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
                onNavigateToGacha = { navigationActions.navigateToGacha() },
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
                onNavigateToGacha = { navigationActions.navigateToGacha() },
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
                },
                onNavigateToAIAssistant = { mangaContext, mediaId, mediaType, mediaTitle ->
                    navigationActions.navigateToAIAssistant(
                        noteId = null,
                        initialText = null,
                        animeContext = mangaContext,
                        mediaId = mediaId,
                        mediaType = mediaType,
                        mediaTitle = mediaTitle
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
                onNavigateToSearch = { navigationActions.navigateToSearch() },
                onNavigateToGacha = { navigationActions.navigateToGacha() }
            )
        }

        composable<Route.Gacha> {
            GachaScreen(
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateHome = { navigationActions.navigateToHome() },
                onNavigateToMyLibrary = { navigationActions.navigateToMyLibrary() },
                onNavigateToAnimeList = { navigationActions.navigateToAnimeList() },
                onNavigateToMangaList = { navigationActions.navigateToMangaList() },
                onNavigateToSearch = { navigationActions.navigateToSearch() },
                onNavigateToAnimeDetail = { malId -> navigationActions.navigateToAnimeDetail(malId) },
                onNavigateToMangaDetail = { malId -> navigationActions.navigateToMangaDetail(malId) }
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
                animeContext = route.animeContext,
                mediaId = route.mediaId,
                mediaType = route.mediaType,
                mediaTitle = route.mediaTitle,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToAnimeDetail = { malId -> navigationActions.navigateToAnimeDetail(malId) },
                onNavigateToMangaDetail = { malId -> navigationActions.navigateToMangaDetail(malId) },
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
                },
                onNavigateToAIAssistant = { animeContext, mediaId, mediaType, mediaTitle ->
                    navigationActions.navigateToAIAssistant(
                        noteId = null,
                        initialText = null,
                        animeContext = animeContext,
                        mediaId = mediaId,
                        mediaType = mediaType,
                        mediaTitle = mediaTitle
                    )
                }
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        private var lastNavigationAtMillis = 0L

        private fun runNavigation(block: () -> Unit) {
            val now = Clock.System.now().toEpochMilliseconds()
            if (now - lastNavigationAtMillis < NAVIGATION_DEBOUNCE_MS) return
            lastNavigationAtMillis = now
            block()
        }

        private fun navigateToHomeRoot() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) {
                    inclusive = false
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToHome() = runNavigation {
            navigateToHomeRoot()
        }

        override fun navigateToSearch() = runNavigation {
            navController.navigate(Route.Search) {
                popUpTo(Route.Home) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToMyLibrary() = runNavigation {
            navController.navigate(Route.MyLibrary) {
                popUpTo(Route.Home) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToGacha() = runNavigation {
            navController.navigate(Route.Gacha) {
                popUpTo(Route.Home) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToAnimeList() = runNavigation {
            navController.navigate(Route.AnimeList) {
                popUpTo(Route.Home) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToMangaList() = runNavigation {
            navController.navigate(Route.MangaList) {
                popUpTo(Route.Home) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToLibraryEntryEditor(
            mediaId: Int,
            mediaType: String,
            title: String,
            imageUrl: String?,
            totalCount: Int?,
            entryId: Long?
        ) = runNavigation {
            navController.navigate(
                Route.LibraryEntryEditor(
                    mediaId = mediaId,
                    mediaType = mediaType,
                    title = title,
                    imageUrl = imageUrl,
                    totalCount = totalCount,
                    entryId = entryId
                )
            ) {
                launchSingleTop = true
            }
        }

        override fun navigateToSettings() = runNavigation {
            navController.navigate(Route.Settings) {
                popUpTo(Route.Home) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToAddNote(noteId: Long?) = runNavigation {
            navController.navigate(Route.AddNote(noteId)) {
                launchSingleTop = true
            }
        }

        override fun navigateToNoteDetail(noteId: Long) = runNavigation {
            navController.navigate(Route.NoteDetail(noteId)) {
                launchSingleTop = true
            }
        }

        override fun navigateToAIAssistant(
            noteId: Long?,
            initialText: String?,
            animeContext: String?,
            mediaId: Int?,
            mediaType: String?,
            mediaTitle: String?
        ) = runNavigation {
            navController.navigate(
                Route.AIAssistant(
                    noteId = noteId,
                    initialText = initialText,
                    animeContext = animeContext,
                    mediaId = mediaId,
                    mediaType = mediaType,
                    mediaTitle = mediaTitle
                )
            ) {
                launchSingleTop = true
            }
        }

        override fun navigateToAnimeDetail(malId: Int) = runNavigation {
            navController.navigate(Route.AnimeDetail(malId)) {
                launchSingleTop = true
            }
        }

        override fun navigateToMangaDetail(malId: Int) = runNavigation {
            navController.navigate(Route.MangaDetail(malId)) {
                launchSingleTop = true
            }
        }

        override fun navigateBack() = runNavigation {
            if (!navController.popBackStack()) {
                navigateToHomeRoot()
            }
        }
    }
}

private const val NAVIGATION_DEBOUNCE_MS = 240L