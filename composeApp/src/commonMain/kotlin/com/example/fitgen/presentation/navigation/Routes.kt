package com.example.fitgen.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Home : Route

    @Serializable
    data class AIAssistant(
        val initialText: String? = null
    ) : Route

    // Workout
    @Serializable
    data object WorkoutList : Route

    @Serializable
    data object AddWorkout : Route

    // Nutrition
    @Serializable
    data object Nutrition : Route

    @Serializable
    data object AddMeal : Route

    // Dashboard
    @Serializable
    data object Dashboard : Route

    // Profile
    @Serializable
    data object Profile : Route

    // AI Dynamic Workout
    @Serializable
    data object DynamicWorkout : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToDashboard()
    fun navigateToAIAssistant(initialText: String? = null)
    fun navigateToDynamicWorkout()
    fun navigateToWorkoutList()
    fun navigateToAddWorkout()
    fun navigateToNutrition()
    fun navigateToAddMeal()
    fun navigateToProfile()
    fun navigateBack()
}