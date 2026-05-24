package com.example.mapenumkm.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Splash : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object ForgotPassword : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object ProductList : Route

    @Serializable
    data object History : Route

    @Serializable
    data object Transaction : Route

    @Serializable
    data object Report : Route

    @Serializable
    data class AddNote(
        val noteId: Long? = null
    ) : Route

    @Serializable
    data class NoteDetail(
        val noteId: Long
    ) : Route

    @Serializable
    data class AIAssistant(
        val noteId: Long? = null,
        val initialText: String? = null
    ) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToAddNote(noteId: Long? = null)
    fun navigateToNoteDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateBack()
}