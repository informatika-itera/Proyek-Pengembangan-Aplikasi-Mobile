package com.example.movein.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movein.data.local.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val database: NoteDatabase) : ViewModel() {

    private val _loginState = mutableStateOf<LoginResultState>(LoginResultState.Idle)
    val loginState: State<LoginResultState> = _loginState

    private val _registerState = mutableStateOf<RegisterResultState>(RegisterResultState.Idle)
    val registerState: State<RegisterResultState> = _registerState

    fun login(usernameInput: String, passwordInput: String) {
        _loginState.value = LoginResultState.Loading

        viewModelScope.launch {
            try {
                // Memeriksa kesesuaian data ke UserEntity SQLDelight
                val user = withContext(Dispatchers.Default) {
                    database.noteQueries.getUserByCredentials(
                        username = usernameInput,
                        password = passwordInput
                    ).executeAsOneOrNull()
                }

                if (user != null) {
                    _loginState.value = LoginResultState.Success(user.username)
                } else {
                    _loginState.value = LoginResultState.Error("Username atau password salah!")
                }
            } catch (e: Exception) {
                _loginState.value = LoginResultState.Error("Gagal terhubung ke database.")
            }
        }
    }

    fun register(usernameInput: String, passwordInput: String) {
        _registerState.value = RegisterResultState.Loading

        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    database.noteQueries.insertUser(
                        username = usernameInput,
                        password = passwordInput
                    )
                }
                _registerState.value = RegisterResultState.Success(usernameInput)
            } catch (e: Exception) {
                // Biasanya error karena username UNIQUE constraint
                _registerState.value = RegisterResultState.Error("Username sudah terdaftar atau gagal menyimpan.")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginResultState.Idle
        _registerState.value = RegisterResultState.Idle
    }
}

sealed interface LoginResultState {
    object Idle : LoginResultState
    object Loading : LoginResultState
    data class Success(val username: String) : LoginResultState
    data class Error(val message: String) : LoginResultState
}

sealed interface RegisterResultState {
    object Idle : RegisterResultState
    object Loading : RegisterResultState
    data class Success(val username: String) : RegisterResultState
    data class Error(val message: String) : RegisterResultState
}