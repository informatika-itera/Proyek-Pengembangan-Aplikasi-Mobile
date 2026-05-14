package com.kelazzz.app.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes untuk KelazZz
 * 
 * Screens (7 total):
 * 1. Login — Form login dengan kredensial ITERA
 * 2. Home / Dashboard — Ringkasan akademik + AI early warning + agenda terdekat
 * 3. Daftar Presensi — Rekap kehadiran per mata kuliah + analytics
 * 4. Presensi — QR scan (ML Kit) + input token manual
 * 5. Kalender Akademik — Buat dan kelola jadwal pribadi secara offline
 * 6. AI Asisten — Chatbot akademik berbasis Gemini API
 * 7. Notifikasi — Pusat notifikasi class reminder dan attendance warning
 */
sealed interface Route {
    
    @Serializable
    data object Login : Route
    
    @Serializable
    data object Home : Route
    
    @Serializable
    data object DaftarPresensi : Route
    
    @Serializable
    data object Presensi : Route
    
    @Serializable
    data object Kalender : Route
    
    @Serializable
    data object AIAsisten : Route
    
    @Serializable
    data object Notifikasi : Route
}

/**
 * Navigation actions interface
 */
interface NavigationActions {
    fun navigateToLogin()
    fun navigateToHome()
    fun navigateToDaftarPresensi()
    fun navigateToPresensi()
    fun navigateToKalender()
    fun navigateToAIAsisten()
    fun navigateToNotifikasi()
    fun navigateBack()
}
