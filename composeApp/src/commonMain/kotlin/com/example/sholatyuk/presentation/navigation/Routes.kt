package com.example.sholatyuk.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Shalat : Route

    @Serializable
    data object Doa : Route

    @Serializable
    data object IslamAI : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object KajianNotes : Route

    @Serializable
    data object Qibla : Route

    // Rute baru untuk halaman detail doa
    @Serializable
    data class DoaDetail(val id: Long) : Route
}