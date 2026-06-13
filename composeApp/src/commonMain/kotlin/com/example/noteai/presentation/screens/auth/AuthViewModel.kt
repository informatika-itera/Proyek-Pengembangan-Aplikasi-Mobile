package com.example.noteai.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.User
import com.example.noteai.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, error = null) }
    }

    fun authenticate() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank() || (!state.isLoginMode && state.name.isBlank())) {
            _uiState.update { it.copy(error = "Mohon isi semua field") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            if (state.isLoginMode) {
                userRepository.login(state.email, state.password)
                    .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                    .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            } else {
                // Register then automatically login
                userRepository.register(User(email = state.email, name = state.name, password = state.password))
                    .onSuccess {
                        userRepository.login(state.email, state.password)
                            .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                    }
                    .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = "Pendaftaran gagal: ${e.message}") } }
            }
        }
    }
}
