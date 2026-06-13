package com.example.raillog.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Welcome : Route
    @Serializable data object Login : Route
    @Serializable data object Register : Route
    @Serializable data object StaffMain : Route
    @Serializable data class RequisitionWizard(val draftId: String? = null) : Route

    @Serializable data object AdminMain : Route
    @Serializable data class VerificationDetail(val requisitionId: Long) : Route

    // Contextual AI Assistant - tanpa parameter karena context-aware
    // dari inventory real-time, bukan per-item
    @Serializable data object ContextualAI : Route
}