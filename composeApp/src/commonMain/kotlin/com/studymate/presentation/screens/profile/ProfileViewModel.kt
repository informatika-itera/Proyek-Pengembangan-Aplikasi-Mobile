package com.studymate.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.model.UserProfile
import com.studymate.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.getProfile().collect { profile ->
                if (profile == null) {
                    _uiState.emit(ProfileUiState.Empty)
                } else {
                    _uiState.emit(ProfileUiState.Success(profile))
                }
            }
        }
    }

    fun saveProfile(name: String, nim: String) {
        if (name.isBlank() || nim.isBlank()) return
        viewModelScope.launch {
            val currentProfile = (uiState.value as? ProfileUiState.Success)?.profile
            val newProfile = UserProfile(
                id = currentProfile?.id ?: 1,
                name = name.trim(),
                nim = nim.trim(),
                currentStreak = currentProfile?.currentStreak ?: 0,
                lastStudyDate = currentProfile?.lastStudyDate
            )
            profileRepository.saveProfile(newProfile)
        }
    }

    fun enterEditMode() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            viewModelScope.launch {
                _uiState.emit(ProfileUiState.EditMode(currentState.profile))
            }
        }
    }

    fun cancelEdit() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.EditMode) {
            viewModelScope.launch {
                _uiState.emit(ProfileUiState.Success(currentState.profile))
            }
        }
    }
}
