package com.example.todomaster.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object TaskList : Route

    @Serializable
    data class AddTask(val taskId: Long? = null) : Route

    @Serializable
    data class TaskDetail(val taskId: Long) : Route

    @Serializable
    data class QuadrantDetail(val quadrantId: Long) : Route

    @Serializable
    data object Calendar : Route

    @Serializable
    data object Statistics : Route

    @Serializable
    data class AIAssistant(
        val taskId: Long? = null,
        val initialText: String? = null
    ) : Route
}

interface NavigationActions {
    fun navigateToTaskList()
    fun navigateToAddTask(taskId: Long? = null)
    fun navigateToTaskDetail(taskId: Long)
    fun navigateToQuadrantDetail(quadrantId: Long)
    fun navigateToCalendar()
    fun navigateToStatistics()
    fun navigateToAIAssistant(taskId: Long? = null, initialText: String? = null)
    fun navigateBack()
}