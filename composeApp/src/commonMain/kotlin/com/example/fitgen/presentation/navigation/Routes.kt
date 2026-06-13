package com.example.fitgen.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route

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

    @Serializable
    data object EditProfile : Route

    // AI Dynamic Workout
    @Serializable
    data object DynamicWorkout : Route

    // Exercise Execution Flow
    @Serializable
    data class ExerciseDetail(
        val name: String,
        val bodyPart: String,
        val gifUrl: String,
        val instructions: String
    ) : Route

    @Serializable
    data class ActiveSession(
        val exerciseName: String,
        val challengeId: String? = null,
        val challengeDay: Int? = null
    ) : Route

    // Challenges
    @Serializable
    data class ChallengeDetail(
        val challengeId: String
    ) : Route

    @Serializable
    data class ChallengeDay(
        val challengeId: String,
        val day: Int
    ) : Route
}

interface NavigationActions {
    fun navigateToSplash()
    fun navigateToOnboarding()
    fun navigateToHome()
    fun navigateToDashboard()
    fun navigateToAIAssistant(initialText: String? = null)
    fun navigateToDynamicWorkout()
    fun navigateToWorkoutList()
    fun navigateToAddWorkout()
    fun navigateToNutrition()
    fun navigateToAddMeal()
    fun navigateToProfile()
    fun navigateToEditProfile()
    fun navigateToExerciseDetail(name: String, bodyPart: String, gifUrl: String, instructions: String)
    fun navigateToActiveSession(exerciseName: String, challengeId: String? = null, challengeDay: Int? = null)
    fun navigateToChallengeDetail(challengeId: String)
    fun navigateToChallengeDay(challengeId: String, day: Int)
    fun navigateBack()
}