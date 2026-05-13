package com.example.fitgen.presentation.navigation

import kotlinx.serialization.Serializable

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

    // ── Sprint 2: Workout routes ──
    @Serializable
    data object WorkoutList : Route

    @Serializable
    data object AddWorkout : Route

    // ── Sprint 3: Profile route ──
    @Serializable
    data object Profile : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToAddNote(noteId: Long? = null)
    fun navigateToNoteDetail(noteId: Long)
    fun navigateToAIAssistant(noteId: Long? = null, initialText: String? = null)
    fun navigateToWorkoutList()
    fun navigateToAddWorkout()
    fun navigateToProfile()
    fun navigateBack()
}
