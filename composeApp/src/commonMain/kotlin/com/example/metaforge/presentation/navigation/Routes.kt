package com.example.metaforge.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data object DraftSetup : Route
    @Serializable data class DraftSimActive(
        val rank: String,
        val partySize: Int,
        val pickPosition: Int,
        val isFirstPick: Boolean,
        val preferredRole: String
    ) : Route
    @Serializable data class HeroSelect(val slotIndex: Int, val isAlly: Boolean, val isBan: Boolean = false) : Route
    @Serializable data object HeroList : Route
    @Serializable data class HeroInfo(val heroId: Int, val heroName: String, val heroRole: String) : Route
    @Serializable data object CounterPick : Route
    @Serializable data object Synergy : Route
}