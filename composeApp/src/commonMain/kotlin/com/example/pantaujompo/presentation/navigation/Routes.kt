package com.example.pantaujompo.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object Dashboard : Route

    @Serializable
    data class AddEditActivity(val activityId: Long? = null) : Route

    @Serializable
    data class ActivityDetail(val activityId: Long) : Route
}

interface NavigationActions {
    fun navigateToDashboard()
    fun navigateToAddActivity(activityId: Long? = null)
    fun navigateToActivityDetail(activityId: Long)
    fun navigateBack()
}