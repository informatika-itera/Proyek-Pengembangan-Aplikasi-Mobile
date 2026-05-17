package com.example.masakuy.presentation.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Home : Routes("home")
    data object Recommendation : Routes("recommendation/{budget}") {
        fun createRoute(budget: Int) = "recommendation/$budget"
    }
    data object Detail : Routes("detail/{recipeId}") {
        fun createRoute(recipeId: String) = "detail/$recipeId"
    }
    data object Favorite : Routes("favorite")
    data object Search : Routes("search")
}