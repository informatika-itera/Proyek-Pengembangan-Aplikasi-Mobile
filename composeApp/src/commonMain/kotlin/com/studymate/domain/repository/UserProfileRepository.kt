package com.studymate.domain.repository

import com.studymate.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
}
