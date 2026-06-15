package com.studymate.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Notes : Screen("notes")
    object Quiz : Screen("quiz")
    object Calendar : Screen("calendar?date={date}") {
        fun createRoute(date: String? = null) = if (date != null) "calendar?date=$date" else "calendar"
    }
    object Profile : Screen("profile")
    object SelectNoteForQuiz : Screen("select_note_for_quiz")
    object AdvancedQuiz : Screen("advanced_quiz")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long) = "note_detail/$noteId"
    }
}

object NavArgs {
    const val NOTE_ID = "noteId"
    const val DATE = "date"
}
