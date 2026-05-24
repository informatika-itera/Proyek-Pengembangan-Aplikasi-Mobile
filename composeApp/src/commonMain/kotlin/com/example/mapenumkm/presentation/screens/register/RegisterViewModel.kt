package com.example.mapenumkm.presentation.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.data.local.datastore.UserPreferences
import com.example.mapenumkm.domain.model.User
import com.example.mapenumkm.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class RegisterViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                // Check if user already exists
                val existingEmail = userRepository.getUserByEmailOrPhone(email)
                val existingPhone = userRepository.getUserByEmailOrPhone(phone)

                if (existingEmail != null || existingPhone != null) {
                    _registerState.value = RegisterState.Error("Email atau nomor HP sudah terdaftar")
                    return@launch
                }

                val user = User(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
                userRepository.insertUser(user)
                
                // Automatically log in after registration
                userPreferences.setLoggedIn(true, email)

                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Gagal mendaftar: ${e.message}")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}

sealed interface RegisterState {
    data object Idle : RegisterState
    data object Loading : RegisterState
    data object Success : RegisterState
    data class Error(val message: String) : RegisterState
}
