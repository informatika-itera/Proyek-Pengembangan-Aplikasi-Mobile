package com.studymate.domain.repository

import com.studymate.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
    suspend fun updateLocalProfile(name: String?, photoPath: String?, nim: String?, major: String?)
    suspend fun updateStreak(streak: Int, lastStudyDate: Long?)
    suspend fun updateMantra(mantra: String)
}
