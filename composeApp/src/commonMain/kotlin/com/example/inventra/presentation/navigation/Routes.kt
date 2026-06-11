package com.example.inventra.presentation.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash_screen")       // Sprint 3 fix
    object Login : Routes("login_screen")
    object Dashboard : Routes("dashboard_screen")
    object Catalog : Routes("catalog_screen")
    object History : Routes("history_screen")
    object AskAI : Routes("ask_ai_screen")
    object AddEditItem : Routes("add_edit_item_screen")
    object Profile : Routes("profile_screen")
    object UserManagement : Routes("user_management_screen")
    object UserDetail : Routes("user_detail_screen/{userId}") {
        fun createRoute(userId: String) = "user_detail_screen/$userId"
    }

    object ItemDetail : Routes("item_detail_screen/{itemId}") {
        fun createRoute(itemId: String) = "item_detail_screen/$itemId"
        fun createRoute(itemId: Long) = "item_detail_screen/$itemId"
    }
}
