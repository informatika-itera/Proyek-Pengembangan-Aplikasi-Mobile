package com.example.noteai.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.local.datastore.UserPreferences
import com.example.noteai.domain.model.User
import com.example.noteai.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val currentUser: StateFlow<User?> = userRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isDarkMode: StateFlow<Boolean> = userPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
