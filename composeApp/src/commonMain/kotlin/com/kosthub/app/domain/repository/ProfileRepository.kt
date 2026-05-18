package com.kosthub.app.domain.repository

import com.kosthub.app.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): Profile?
    suspend fun saveProfile(profile: Profile)
    suspend fun clearProfile()
}
