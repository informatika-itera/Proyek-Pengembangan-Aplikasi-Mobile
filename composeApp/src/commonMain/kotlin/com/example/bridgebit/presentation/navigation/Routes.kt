package com.example.bridgebit.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Dashboard : Route // Screen 1: Dashboard (sebelumnya Home)

    @Serializable
    data class Workspace(val translationId: Long? = null) : Route // Screen 2: Workspace (sebelumnya AddNote)

    @Serializable
    data class TranslationDetail(val translationId: Long) : Route // Screen 3: Detail (sebelumnya NoteDetail)

    @Serializable
    data class AIAssistant(
        val translationId: Long? = null,
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
    fun navigateToDashboard()
    fun navigateToWorkspace(translationId: Long? = null)
    fun navigateToTranslationDetail(translationId: Long)
    fun navigateToAI(translationId: Long? = null, initialText: String? = null)
    fun navigateToVault()
    fun navigateToSettings()
    fun navigateToInsights()
    fun navigateBack()
}