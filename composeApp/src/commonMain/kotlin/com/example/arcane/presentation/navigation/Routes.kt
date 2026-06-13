package com.example.arcane.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Splash : Route
    @Serializable data object Home : Route
    @Serializable data class Explore(val searchQuery: String = "") : Route
    @Serializable data object Letterbox : Route
    @Serializable data object Settings : Route
    @Serializable data class BookDetail(val googleBookId: String, val localBookId: Long) : Route
    @Serializable data class ResearchAssistant(val bookTitle: String, val bookDescription: String) : Route
    @Serializable data class FolderDetail(val folderId: Long) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToExplore(query: String = "")
    fun navigateToLetterbox()
    fun navigateToBookDetail(googleBookId: String, localBookId: Long = 0L)
    fun navigateToFolderDetail(folderId: Long)
    fun navigateToResearchAssistant(bookTitle: String, bookDescription: String)
    fun navigateToSettings()
    fun navigateBack()
}