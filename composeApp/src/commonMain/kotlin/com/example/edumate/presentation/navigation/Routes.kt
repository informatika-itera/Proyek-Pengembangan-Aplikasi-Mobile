package com.example.edumate.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data class AddEditTask(val taskId: Long? = null) : Route

    @Serializable
    data class TaskDetail(val taskId: Long) : Route

    @Serializable
    data class AIAssistant(
        val taskId: Long? = null,
        val initialText: String? = null
    ) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToAddEditTask(taskId: Long? = null)
    fun navigateToTaskDetail(taskId: Long)
    fun navigateToAIAssistant(taskId: Long? = null, initialText: String? = null)
    fun navigateBack()
}