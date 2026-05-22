package com.example.rewind.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Home : Route

    @Serializable
    data class AddMovie(val movieId: Long? = null) : Route

    @Serializable
    data class MovieDetail(val movieId: Long) : Route

    @Serializable
    data object AIAssistant : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToAddMovie(movieId: Long? = null)
    fun navigateToMovieDetail(movieId: Long)
    fun navigateToAIAssistant()
    fun navigateBack()
}