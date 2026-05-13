package com.example.pantaujompo.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    // 5 Tab Utama di Bottom Navigation
    @Serializable data object Beranda : Route
    @Serializable data object Pemindai : Route
    @Serializable data object Riwayat : Route
    @Serializable data object Artikel : Route
    @Serializable data object Profil : Route

    // Rute tambahan untuk CRUD (Ini yang tadi ketinggalan bray!)
    @Serializable data class AddEditActivity(val id: Long? = null) : Route
    @Serializable data class DetailRiwayat(val id: Long) : Route
}