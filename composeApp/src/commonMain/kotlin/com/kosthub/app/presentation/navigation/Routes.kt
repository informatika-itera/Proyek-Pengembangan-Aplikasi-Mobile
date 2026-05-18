package com.kosthub.app.presentation.navigation

object Routes {
    const val Home = "home"
    const val Favorites = "favorites"
    const val Contribute = "contribute"
    const val Profile = "profile"
    const val Detail = "detail/{id}"

    fun detail(id: Long): String = "detail/$id"
}
