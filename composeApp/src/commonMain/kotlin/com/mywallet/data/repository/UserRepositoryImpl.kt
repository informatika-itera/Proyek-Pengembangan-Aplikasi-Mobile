package com.mywallet.data.repository

import com.mywallet.domain.repository.UserRepository
import com.mywallet.presentation.screens.profile.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepositoryImpl : UserRepository {
    private val _profileState = MutableStateFlow(ProfileUiState())
    override val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    override fun updateProfile(name: String, bio: String, phone: String, email: String, biometric: Boolean) {
        _profileState.value = _profileState.value.copy(
            name = name,
            bio = bio,
            phone = phone,
            email = email,
            isBiometricEnabled = biometric
        )
    }

    override fun toggleBiometric(enabled: Boolean) {
        _profileState.value = _profileState.value.copy(isBiometricEnabled = enabled)
    }
}
