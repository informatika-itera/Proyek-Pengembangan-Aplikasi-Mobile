package com.studymate.presentation.screens.home

import com.studymate.domain.model.Note

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val userName: String,
        val currentStreak: Int,
        val recentNotes: List<Note>,
        val dailyMantra: String
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
