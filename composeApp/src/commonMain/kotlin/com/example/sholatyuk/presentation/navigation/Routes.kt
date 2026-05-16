package com.example.sholatyuk.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object Home : Route

    @Serializable
    data object Shalat : Route

    @Serializable
    data object IslamAI : Route
    
    @Serializable
    data class AddNote(val noteId: Long? = null) : Route
    
    @Serializable
    data class NoteDetail(val noteId: Long) : Route
    
    @Serializable
    data class AIAssistant(
        val noteId: Long? = null,
        val initialText: String? = null
    ) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToShalat()
    fun navigateToIslamAI()
    fun navigateToAddNote(noteId: Long? = null)
    fun navigateToNoteDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateBack()
}
