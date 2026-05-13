package com.example.pantaujompo.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    // Menu Bottom Nav
    @Serializable data object Beranda : Route
    @Serializable data object Pemindai : Route
    @Serializable data object Riwayat : Route
    @Serializable data object Artikel : Route
    @Serializable data object Profil : Route

    // Menu CRUD (Disembunyikan dari Bottom Nav)
    @Serializable data class AddEditActivity(val id: Long? = null) : Route
    @Serializable data class ActivityDetail(val id: Long) : Route
}