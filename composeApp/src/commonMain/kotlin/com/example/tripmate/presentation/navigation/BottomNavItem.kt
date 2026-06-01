package com.example.tripmate.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        label = "Beranda",
        icon = Icons.Default.Home
    )
    data object AI : BottomNavItem(
        route = Screen.AIScreen.route,
        label = "AI",
        icon = Icons.Default.AutoAwesome
    )
    data object Statistics : BottomNavItem(
        route = Screen.Statistics.route,
        label = "Statistik",
        icon = Icons.Default.BarChart
    )
    data object Profile : BottomNavItem(
        route = Screen.Profile.route,
        label = "Profil",
        icon = Icons.Default.Person
    )

    companion object {
        val items = listOf(Home, Statistics, AI, Profile)
    }
}
