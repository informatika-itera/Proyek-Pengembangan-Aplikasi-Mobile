package com.example.raillog.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.data.local.datastore.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val role: String = ""
)

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel(){
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(usernameInput: String, passwordInput: String) {
        viewModelScope.launch {
            if (usernameInput.isBlank() || passwordInput.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "Username dan Password wajib diisi!")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(1000)

            // Ambil data daftar akun
            val allAccountsString = userPreferences.staffAccounts.first()
            val accountList = if (allAccountsString.isNotEmpty()) {
                allAccountsString.split(";").map { it.split("|") }
            } else {
                emptyList()
            }

            // Cari user di daftar
            val foundAccount = accountList.find { it.size >= 2 && it[0] == usernameInput && it[1] == passwordInput }

            when {
                // 1. DITEMUKAN DI DAFTAR AKUN
                foundAccount != null -> {
                    userPreferences.setUserRole("staff")
                    userPreferences.setActiveUsername(usernameInput) // Set sesi aktif
                    println("DEBUG: User logged in, setting activeUsername to: $usernameInput")
                    _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true, role = "staff")
                }
                // 2. Akun Admin (Hardcoded)
                usernameInput == "admin" && passwordInput == "raillog123" -> {
                    userPreferences.setUserRole("admin")
                    _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true, role = "admin")
                }
                // 3. Fallback Akun Operator Default
                usernameInput == "operator" && passwordInput == "raillog123" -> {
                    userPreferences.setUserRole("staff")
                    _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true, role = "staff")
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Username atau Password salah!"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}