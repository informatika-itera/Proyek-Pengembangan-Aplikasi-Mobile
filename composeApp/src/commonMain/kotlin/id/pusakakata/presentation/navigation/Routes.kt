package id.pusakakata.presentation.navigation

sealed class Routes(val route: String) {
    object Main : Routes("main")
    object Home : Routes("home")
    object AddEdit : Routes("add_edit?wordId={wordId}") {
        fun passId(id: String? = null) = "add_edit?wordId=$id"
    }
    object Detail : Routes("detail/{wordId}") {
        fun passId(id: String) = "detail/$id"
    }
    object Gacha : Routes("gacha")
    object About : Routes("about")
    object Flashcard : Routes("flashcard")
    object Settings : Routes("settings")
    object Quiz : Routes("quiz")
    object Favorite : Routes("favorite")
    object Profile : Routes("profile")
    object Collection : Routes("collection")
}
