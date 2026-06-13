package com.example.masakuy.presentation.navigation

import java.net.URLEncoder

sealed class Routes(val route: String) {    data object Home : Routes("home")
    data object Recommendation : Routes("recommendation/{budget}") {
        fun createRoute(budget: Int) = "recommendation/$budget"
    }
    data object Detail : Routes("detail/{recipeId}/{recipeName}/{budget}") {
        fun createRoute(recipeId: String, recipeName: String, budget: Int): String {
            val encodedName = URLEncoder.encode(recipeName, "UTF-8")
            return "detail/$recipeId/$encodedName/$budget"
        }
    }
    data object Favorite : Routes("favorite")
    data object Search : Routes("search")
}