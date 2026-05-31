package com.example.pantaujompo.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    // --- 🚀 TAMBAHAN BARU: Rute Layar Awal ---
    @Serializable data object Splash : Route
    @Serializable data object ProfileSetup : Route

    // 5 Tab Utama di Bottom Navigation
    @Serializable data object Beranda : Route
    @Serializable data object Pemindai : Route
    @Serializable data object Riwayat : Route
    @Serializable data object Artikel : Route
    @Serializable data object Profil : Route

    // Layar Tracking (Sekarang menerima parameter jenis aktivitas)
    @Serializable data class Tracking(val jenis: String = "Lari") : Route

    // Layar Simpan Aktivitas (Menerima parameter hasil tracking)
    @Serializable data class SaveActivity(
        val jenis: String,
        val jarak: Float,
        val kalori: Int,
        val durasi: Int,
        val pace: String
    ) : Route

    // Rute Chat AI
    @Serializable data object AiChat : Route

    // Rute tambahan untuk CRUD
    @Serializable data class AddEditActivity(val id: Long? = null) : Route
    @Serializable data class DetailRiwayat(val id: Long) : Route
    
    // Rute Pengaturan
    @Serializable data object Settings : Route
    
    // Rute Tentang Aplikasi
    @Serializable data object About : Route
}