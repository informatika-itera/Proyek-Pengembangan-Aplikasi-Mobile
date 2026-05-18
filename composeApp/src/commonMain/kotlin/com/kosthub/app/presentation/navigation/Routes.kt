package com.kosthub.app.presentation.navigation

object Routes {
    const val Home = "home"
    const val Favorites = "favorites"
    const val Contribute = "contribute"
    const val ContributeAdd = "contribute/add"
    const val ContributeEdit = "contribute/edit/{id}"
    const val ContributeDelete = "contribute/delete/{id}"
    const val Profile = "profile"
    const val Detail = "detail/{id}"

    fun detail(id: Long): String = "detail/$id"
    fun contributeEdit(id: Long): String = "contribute/edit/$id"
    fun contributeDelete(id: Long): String = "contribute/delete/$id"
}
