package com.example.sholatyuk.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Shalat : Route

    @Serializable
    data object Doa : Route

    @Serializable
    data object IslamAI : Route

    // Tambahan rute baru untuk halaman Profil
    @Serializable
    data object Profile : Route
}