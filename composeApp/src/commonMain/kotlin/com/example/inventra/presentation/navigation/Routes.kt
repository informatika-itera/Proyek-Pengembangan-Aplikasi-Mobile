package com.example.inventra.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Dashboard : Route
    @Serializable data object Catalog : Route
    @Serializable data object History : Route
    @Serializable data object AIAssistant : Route
    @Serializable data class ItemDetail(val itemId: Long) : Route
    @Serializable data class AddEditItem(val itemId: Long? = null) : Route
}
