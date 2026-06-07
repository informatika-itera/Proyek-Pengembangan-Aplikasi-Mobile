package com.example.synesthesia.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Constellation : Route // Home Screen (Main Graph)

    @Serializable
    data object SonicZen : Route // Placeholder for Tab 2

    @Serializable
    data object Sanctuary : Route // Placeholder for Tab 3

    @Serializable
    data object Insights : Route // Placeholder for Tab 4
    
    @Serializable
    data class AddMemory(val memoryId: Long? = null) : Route // Add/Edit Note
    
    @Serializable
    data class MemoryDetail(val memoryId: Long) : Route // Detail Note
    
    @Serializable
    data class AIAssistant(
        val noteId: Long? = null,
        val initialText: String? = null
    ) : Route

    @Serializable
    data object Settings : Route
}

interface NavigationActions {
    fun navigateToOnboarding()
    fun navigateToConstellation()
    fun navigateToAddMemory(memoryId: Long? = null)
    fun navigateToMemoryDetail(memoryId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateToSettings()
    fun navigateBack()
}
