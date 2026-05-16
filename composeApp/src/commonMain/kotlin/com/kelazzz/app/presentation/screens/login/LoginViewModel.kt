package com.kelazzz.app.presentation.screens.login

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val state = _uiState.value

        // Cegah double-tap saat sedang loading
        if (state.isLoading) return

        // Validasi — single atomic update untuk menghindari UI flicker
        var usernameError: String? = null
        var passwordError: String? = null

        if (state.username.isBlank()) {
            usernameError = "Email ITERA tidak boleh kosong"
        } else if (state.username.contains("@") &&
            !state.username.trim().endsWith("@student.itera.ac.id") &&
            !state.username.trim().endsWith("@itera.ac.id")
        ) {
            usernameError = "Gunakan email ITERA (@student.itera.ac.id)"
        }

        if (state.password.isBlank()) {
            passwordError = "Password tidak boleh kosong"
        } else if (state.password.length < 6) {
            passwordError = "Password minimal 6 karakter"
        }

        if (usernameError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    usernameError = usernameError,
                    passwordError = passwordError,
                    loginError = null
                )
            }
            return
        }

        // Tambahkan suffix email jika belum ada
        val email = if (state.username.contains("@")) {
            state.username.trim()
        } else {
            "${state.username.trim()}@student.itera.ac.id"
        }

        _uiState.update { it.copy(isLoading = true, loginError = null, usernameError = null, passwordError = null) }

        viewModelScope.launch {
            authRepository.login(email, state.password)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, loginSuccessMessage = "Halo, ${user.nama}!") }
                    delay(1500L)
                    _events.emit(LoginEvent.LoginSuccess(user.nama))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = error.message ?: "Login gagal. Periksa kredensial Anda."
                        )
                    }
                }
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val loginSuccessMessage: String? = null
)

sealed interface LoginEvent {
    data class LoginSuccess(val nama: String) : LoginEvent
}
