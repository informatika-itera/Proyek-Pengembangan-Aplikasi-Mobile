package com.example.nutriscan.data.repository

import com.example.nutriscan.domain.model.UserProfile
import com.example.nutriscan.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserProfileRepository : UserProfileRepository {

    private val _profile = MutableStateFlow<UserProfile?>(null)

    override fun getProfile(): Flow<UserProfile?> = _profile

    override suspend fun saveProfile(profile: UserProfile) {
        _profile.value = profile
    }

    override suspend fun updateProfile(profile: UserProfile) {
        _profile.value = profile
    }

    override suspend fun deleteProfile() {
        _profile.value = null
    }

    override suspend fun hasProfile(): Boolean = _profile.value != null
}