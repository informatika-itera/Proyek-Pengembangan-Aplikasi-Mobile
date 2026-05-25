package com.studymate.presentation.screens.notes

import com.studymate.domain.model.Note

sealed interface NotesUiState {
    object Loading : NotesUiState
    data class Success(val notes: List<Note>, val selectedNote: Note? = null) : NotesUiState
    data class Refining(val notes: List<Note>, val noteBeingRefined: Long) : NotesUiState
    data class Error(val message: String) : NotesUiState
    object Empty : NotesUiState
}
