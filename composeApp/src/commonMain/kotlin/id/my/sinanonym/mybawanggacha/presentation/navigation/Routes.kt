package id.my.sinanonym.mybawanggacha.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object Home : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object MyLibrary : Route

    @Serializable
    data object Gacha : Route

    @Serializable
    data object AnimeList : Route

    @Serializable
    data object MangaList : Route

    @Serializable
    data class LibraryEntryEditor(
        val mediaId: Int,
        val mediaType: String,
        val title: String,
        val imageUrl: String? = null,
        val totalCount: Int? = null,
        val entryId: Long? = null
    ) : Route

    @Serializable
    data object Settings : Route
    
    @Serializable
    data class AddNote(val noteId: Long? = null) : Route
    
    @Serializable
    data class NoteDetail(val noteId: Long) : Route
    
    @Serializable
    data class AIAssistant(
        val noteId: Long? = null,
        val initialText: String? = null,
        val animeContext: String? = null,
        val mediaId: Int? = null,
        val mediaType: String? = null,
        val mediaTitle: String? = null
    ) : Route

    @Serializable
    data class AnimeDetail(val malId: Int) : Route

    @Serializable
    data class MangaDetail(val malId: Int) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToSearch()
    fun navigateToMyLibrary()
    fun navigateToGacha()
    fun navigateToAnimeList()
    fun navigateToMangaList()
    fun navigateToLibraryEntryEditor(
        mediaId: Int,
        mediaType: String,
        title: String,
        imageUrl: String? = null,
        totalCount: Int? = null,
        entryId: Long? = null
    )
    fun navigateToSettings()
    fun navigateToAddNote(noteId: Long? = null)
    fun navigateToNoteDetail(noteId: Long)
    fun navigateToAIAssistant(
        noteId: Long? = null,
        initialText: String? = null,
        animeContext: String? = null,
        mediaId: Int? = null,
        mediaType: String? = null,
        mediaTitle: String? = null
    )
    fun navigateToAnimeDetail(malId: Int)
    fun navigateToMangaDetail(malId: Int)
    fun navigateBack()
}
