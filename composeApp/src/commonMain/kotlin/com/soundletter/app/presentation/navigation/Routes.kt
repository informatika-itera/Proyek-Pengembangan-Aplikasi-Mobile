package com.soundletter.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Compose : Screen("compose")
    data object History : Screen("history")
    data object DetailMessage : Screen("detail/{messageId}") {
        fun createRoute(messageId: String) = "detail/$messageId"
    }
}
