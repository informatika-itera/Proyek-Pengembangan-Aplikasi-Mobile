package com.kelazzz.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kelazzz.app.presentation.screens.home.HomeScreen

/**
 * Main navigation host untuk KelazZz
 * 
 * Saat ini hanya menampilkan HomeScreen sebagai placeholder.
 * Screen lainnya akan di-implement di sprint berikutnya:
 * - Sprint 2: Login, Home, Navigation
 * - Sprint 3: DaftarPresensi, Presensi (QR + manual)
 * - Sprint 4: Kalender, AI Asisten, Notifikasi
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen()
        }
        
        // TODO: Sprint 2 — Login screen
        // composable<Route.Login> { LoginScreen(...) }
        
        // TODO: Sprint 3 — Presensi screens
        // composable<Route.DaftarPresensi> { DaftarPresensiScreen(...) }
        // composable<Route.Presensi> { PresensiScreen(...) }
        
        // TODO: Sprint 4 — Kalender, AI, Notifikasi
        // composable<Route.Kalender> { KalenderScreen(...) }
        // composable<Route.AIAsisten> { AIAsistenScreen(...) }
        // composable<Route.Notifikasi> { NotifikasiScreen(...) }
    }
}
