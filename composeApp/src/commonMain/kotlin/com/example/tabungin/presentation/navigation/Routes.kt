package com.example.tabungin.presentation.navigation

import kotlinx.serialization.Serializable
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Routes(val route: String) {
    data object Home     : Routes("home")
    data object Riwayat  : Routes("riwayat")
    data object Settings : Routes("settings")

    data object Detail : Routes("detail/{targetId}") {
        fun createRoute(targetId: Long) = "detail/$targetId"
        val arguments = listOf(
            navArgument("targetId") { type = NavType.LongType }
        )
    }

    data object AddEdit : Routes("add_edit?targetId={targetId}") {
        fun createRoute(targetId: Long? = null) =
            if (targetId != null) "add_edit?targetId=$targetId" else "add_edit"
        val arguments = listOf(
            navArgument("targetId") {
                type         = NavType.LongType
                defaultValue = -1L
            }
        )
    }
}

sealed interface Route {
    
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
    fun navigateToHome()
    fun navigateToAddNote(noteId: Long? = null)
    fun navigateToNoteDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateBack()
}
