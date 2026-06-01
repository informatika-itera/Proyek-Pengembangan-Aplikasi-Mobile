package com.mywallet.domain.repository

import com.mywallet.presentation.screens.profile.ProfileUiState
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val profileState: StateFlow<ProfileUiState>
    fun updateProfile(name: String, bio: String, phone: String, email: String, biometric: Boolean)
    fun toggleBiometric(enabled: Boolean)
}
