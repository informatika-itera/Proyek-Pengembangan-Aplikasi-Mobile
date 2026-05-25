package com.studymate.presentation.screens.profile

import com.studymate.domain.model.UserProfile

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
    data class EditMode(val profile: UserProfile) : ProfileUiState
    object Empty : ProfileUiState
}
