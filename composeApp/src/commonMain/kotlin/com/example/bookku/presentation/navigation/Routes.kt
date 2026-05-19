package com.example.bookku.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object Home : Route
    
    @Serializable
    data class AddBook(val noteId: Long? = null) : Route
    
    @Serializable
    data class BookDetail(val noteId: Long) : Route
    
    @Serializable
    data class AIAssistant(
        val noteId: Long? = null,
        val initialText: String? = null
    ) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToAddBook(noteId: Long? = null)
    fun navigateToBookDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateBack()
}



