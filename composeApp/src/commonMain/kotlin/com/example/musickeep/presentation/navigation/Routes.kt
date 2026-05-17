package com.example.musickeep.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route
    
    @Serializable
    data object AddMusic : Route
    
    @Serializable
    data class MusicDetail(val musicId: Long) : Route
}
