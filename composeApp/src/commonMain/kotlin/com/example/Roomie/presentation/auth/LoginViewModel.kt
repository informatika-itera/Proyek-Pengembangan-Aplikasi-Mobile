package com.example.Roomie.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val idNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
) {
    val isButtonEnabled: Boolean get() = idNumber.isNotBlank() && !isLoading
}

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onIdNumberChange(value: String) {
        _state.update { it.copy(idNumber = value, error = null) }
    }

    fun login() {
        val currentId = _state.value.idNumber
        if (currentId.isBlank()) {
            _state.update { it.copy(error = "NIM/NIP tidak boleh kosong") }
            return
        }

        // Smart Validation using Regex
        // NIM Mahasiswa ITERA: diawali 12, total 9 digit
        // NIP Admin: total 3-5 digit (asumsi)
        val nimRegex = Regex("^12[0-9]{7}$")
        val nipRegex = Regex("^[0-9]{3,5}$")

        if (!nimRegex.matches(currentId) && !nipRegex.matches(currentId)) {
            _state.update { it.copy(error = "Format NIM (9 digit) atau NIP tidak valid") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase(currentId)
            result.onSuccess {
                _state.update { it.copy(isLoading = false, loginSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message ?: "Login gagal") }
            }
        }
    }
}
