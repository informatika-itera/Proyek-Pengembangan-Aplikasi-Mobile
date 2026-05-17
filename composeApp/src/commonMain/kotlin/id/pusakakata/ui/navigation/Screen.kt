package id.pusakakata.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddEdit : Screen("add_edit?wordId={wordId}") {
        fun passId(id: String? = null) = "add_edit?wordId=$id"
    }
    object Detail : Screen("detail/{wordId}") {
        fun passId(id: String) = "detail/$id"
    }
    object Gacha : Screen("gacha")
    object About : Screen("about")
    object Flashcard : Screen("flashcard")
}
