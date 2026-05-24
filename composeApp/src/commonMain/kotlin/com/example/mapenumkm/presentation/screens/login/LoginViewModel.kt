package com.example.mapenumkm.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.data.local.datastore.UserPreferences
import com.example.mapenumkm.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(identifier: String, password: String) {
        if (identifier.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email/No. HP dan password wajib diisi")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val user = userRepository.getUserByEmailOrPhone(identifier)
                if (user != null && user.password == password) {
                    // Save login state
                    userPreferences.setLoggedIn(true, identifier)
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Email/No. HP atau password salah")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data object Success : LoginState
    data class Error(val message: String) : LoginState
}
