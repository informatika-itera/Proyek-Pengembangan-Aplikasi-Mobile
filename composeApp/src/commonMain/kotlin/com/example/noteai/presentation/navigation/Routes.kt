package com.example.noteai.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    // ==================== HUJJAH SPRINT 2 ROUTES ====================

    @Serializable
    data object HujjahLens : Route

    @Serializable
    data class HujjahResult(val topicId: String) : Route

    @Serializable
    data class ReferenceDetail(val referenceId: String) : Route

    @Serializable
    data object Bookmark : Route

    // ==================== NOTEAI ROUTES ====================
    // File lama belum saya hilangin.

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
    fun navigateToHujjahLens()
    fun navigateToHujjahResult(topicId: String)
    fun navigateToReferenceDetail(referenceId: String)
    fun navigateToBookmarks()

    fun navigateToHome()
    fun navigateToAddNote(noteId: Long? = null)
    fun navigateToNoteDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateBack()
}
