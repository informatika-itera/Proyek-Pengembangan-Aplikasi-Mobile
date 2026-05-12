package com.example.pantaujompo.presentation.navigation

sealed class Route(val route: String) {
    object Beranda : Route("beranda_dashboard")
    object Pemindai : Route("pemindai_gizi_ai")
    object Riwayat : Route("riwayat_terpadu")
    object Artikel : Route("literasi_kesehatan")
    object Profil : Route("metrik_pengguna")
}