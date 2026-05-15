package com.example.raillog.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object Home : Route
    
    @Serializable
    data class AddSupply(val itemId: Long? = null) : Route
    
    @Serializable
    data class SupplyDetail(val itemId: Long) : Route
    
    @Serializable
    data class AIAssistant(
        val itemId: Long? = null,
        val initialText: String? = null
    ) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToAddSupply(noteId: Long? = null)
    fun navigateToSupplyDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateBack()
}
