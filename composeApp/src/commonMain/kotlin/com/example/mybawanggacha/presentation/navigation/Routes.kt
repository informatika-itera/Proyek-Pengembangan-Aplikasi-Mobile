package com.example.mybawanggacha.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object Home : Route

    @Serializable
    data object MyLibrary : Route

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
        val initialText: String? = null
    ) : Route

    @Serializable
    data class AnimeDetail(val malId: Int) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToMyLibrary()
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
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateToAnimeDetail(malId: Int)
    fun navigateBack()
}
