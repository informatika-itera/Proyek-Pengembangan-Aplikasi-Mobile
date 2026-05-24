package com.example.mapenumkm.presentation.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun resetPassword(identifier: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = ForgotPasswordState.Loading
            try {
                val user = userRepository.getUserByEmailOrPhone(identifier)
                if (user != null) {
                    userRepository.updatePassword(identifier, newPassword)
                    _state.value = ForgotPasswordState.Success
                } else {
                    _state.value = ForgotPasswordState.Error("Email atau nomor HP tidak terdaftar")
                }
            } catch (e: Exception) {
                _state.value = ForgotPasswordState.Error("Gagal mengatur ulang kata sandi: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = ForgotPasswordState.Idle
    }
}

sealed interface ForgotPasswordState {
    data object Idle : ForgotPasswordState
    data object Loading : ForgotPasswordState
    data object Success : ForgotPasswordState
    data class Error(val message: String) : ForgotPasswordState
}
