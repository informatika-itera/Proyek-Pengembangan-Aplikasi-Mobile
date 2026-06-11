package com.example.inventra.presentation.screens.auth

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.User
import com.example.inventra.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: TextFieldValue = TextFieldValue(""),
    val password: TextFieldValue = TextFieldValue(""),
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: User? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    fun onEmailChange(email: TextFieldValue) =
        _uiState.update { it.copy(email = email, error = null) }

    fun onPasswordChange(password: TextFieldValue) =
        _uiState.update { it.copy(password = password, error = null) }

    fun login() {
        val state = _uiState.value
        val emailStr = state.email.text
        val passwordStr = state.password.text

        if (emailStr.isBlank() || passwordStr.isBlank()) {
            _uiState.update { it.copy(error = "Email dan password harus diisi") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            authRepository.login(emailStr, passwordStr)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "Login gagal")
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { LoginUiState() }
        }
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            if (authRepository.isLoggedIn) {
                val user = authRepository.getCurrentUser()
                _uiState.update { it.copy(loggedInUser = user) }
            }
        }
    }
}