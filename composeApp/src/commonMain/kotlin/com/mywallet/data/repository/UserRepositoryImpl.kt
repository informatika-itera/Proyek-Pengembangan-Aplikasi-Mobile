package com.mywallet.data.repository

import com.mywallet.data.local.UserPreferences
import com.mywallet.domain.repository.UserRepository
import com.mywallet.presentation.screens.profile.ProfileUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserRepositoryImpl(
    private val userPreferences: UserPreferences
) : UserRepository {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _profileState = MutableStateFlow(ProfileUiState())
    override val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    init {
        scope.launch {
            userPreferences.userDataFlow.collect { userData ->
                _profileState.value = ProfileUiState(
                    name = userData.name,
                    bio = userData.bio,
                    phone = userData.phone,
                    email = userData.email,
                    isBiometricEnabled = userData.isBiometricEnabled
                )
            }
        }
    }

    override fun updateProfile(name: String, bio: String, phone: String, email: String, biometric: Boolean) {
        scope.launch {
            userPreferences.updateProfile(name, bio, phone, email, biometric)
        }
    }

    override fun toggleBiometric(enabled: Boolean) {
        scope.launch {
            userPreferences.toggleBiometric(enabled)
        }
    }
}
