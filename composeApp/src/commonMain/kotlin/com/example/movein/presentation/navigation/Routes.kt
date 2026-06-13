package com.example.movein.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object MoodSelection : Route

    @Serializable
    data object Main : Route

    @Serializable
    data class ActivityGenerator(val moodId: String) : Route

    // Legacy route lama disimpan agar file lama tidak langsung error saat migrasi.
    @Serializable
    data object Home : Route

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
    fun navigateToLogin()
    fun navigateToRegister()
    fun navigateToMain()
    fun navigateToMoodSelection(clearBackStack: Boolean = false)
    fun navigateToActivityGenerator(moodId: String)

    fun navigateToHome()
    fun navigateToAddNote(noteId: Long? = null)
    fun navigateToNoteDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateBack()
}