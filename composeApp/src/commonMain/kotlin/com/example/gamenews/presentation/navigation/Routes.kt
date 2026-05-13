package com.example.gamenews.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data class GameDetail(val gameId: Long) : Route

    @Serializable
    data object AIAssistant : Route
}