package com.studyhub.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Tasks : Screen

    @Serializable
    data object Calendar : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data class AddTask(val taskId: Long = -1L) : Screen

    @Serializable
    data class TaskDetail(val taskId: Long) : Screen
}
