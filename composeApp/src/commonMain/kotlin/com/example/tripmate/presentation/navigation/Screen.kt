package com.example.tripmate.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddTrip : Screen("add_trip")
    data object AIScreen : Screen("ai_screen")
    data object DetailTrip : Screen("detail_trip/{tripId}") {
        fun createRoute(tripId: Long) = "detail_trip/$tripId"
    }
    data object EditTrip : Screen("edit_trip/{tripId}") {
        fun createRoute(tripId: Long) = "edit_trip/$tripId"
    }
}
