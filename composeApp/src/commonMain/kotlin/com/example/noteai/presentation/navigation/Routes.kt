package com.example.noteai.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route // Screen 1: Dashboard

    @Serializable
    data class AddNote(val noteId: Long? = null) : Route // Screen 2: Workspace

    @Serializable
    data class NoteDetail(val noteId: Long) : Route // Screen 3: Detail

    @Serializable
    data class AIAssistant(
        val noteId: Long? = null,
        val initialText: String? = null
    ) : Route // Screen 4: AI Contextual Assistant

    @Serializable
    data object Vault : Route // Screen 5: Phrase Vault

    @Serializable
    data object Settings : Route // Screen 6: Settings & Profile

    @Serializable
    data object Insights : Route // Screen 7: Learning Insights
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToTranslate(noteId: Long? = null)
    fun navigateToDetail(noteId: Long)
    fun navigateToAI(noteId: Long? = null, initialText: String? = null)
    fun navigateToVault()
    fun navigateToSettings()
    fun navigateToInsights()
    fun navigateBack()
}