package com.studymate.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Notes : Screen("notes")
    object Quiz : Screen("quiz")
    object Calendar : Screen("calendar")
    object Profile : Screen("profile")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long) = "note_detail/$noteId"
    }
}

object NavArgs {
    const val NOTE_ID = "noteId"
}
