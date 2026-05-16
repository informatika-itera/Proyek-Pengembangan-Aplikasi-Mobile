package com.kelazzz.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        nama = user?.nama ?: "Mahasiswa",
                        nim = user?.nim ?: "-",
                        email = user?.email ?: "-",
                        unit = user?.unit ?: "-",
                        level = user?.level ?: "-",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutDialog = true) }
        }
    }

    fun confirmLogout() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutDialog = false) }
            authRepository.logout()
            _events.emit(ProfileEvent.LoggedOut)
        }
    }

    fun dismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }
}

data class ProfileUiState(
    val nama: String = "Mahasiswa",
    val nim: String = "-",
    val email: String = "-",
    val unit: String = "-",
    val level: String = "-",
    val isLoading: Boolean = true,
    val showLogoutDialog: Boolean = false
)

sealed interface ProfileEvent {
    data object LoggedOut : ProfileEvent
}
