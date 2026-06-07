package com.studymate.presentation.screens.home

import com.studymate.domain.model.Note
import com.studymate.domain.model.UserProfile

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val userName: String,
        val currentStreak: Int,
        val recentNotes: List<Note>,
        val dailyMantra: String,
        val userProfile: UserProfile? = null
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
