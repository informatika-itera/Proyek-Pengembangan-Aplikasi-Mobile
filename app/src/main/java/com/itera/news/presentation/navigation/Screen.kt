package com.itera.news.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Bookmark : Screen("bookmark")
    object About : Screen("about")
    
    // Argument passing: URL artikel
    object Detail : Screen("detail/{articleUrl}") {
        fun createRoute(url: String): String {
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "detail/$encodedUrl"
        }
    }
}