package com.studymate.data.repository

import com.studymate.domain.model.UserProfile
import com.studymate.domain.repository.AuthRepository
import com.studymate.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.*

class AuthRepositoryImpl(
    private val userProfileRepository: UserProfileRepository
) : AuthRepository {
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    override suspend fun signIn(
        email: String,
        displayName: String?,
        photoUrl: String?
    ): Result<UserProfile> {
        return try {
            // Get existing profile if any
            val existing = userProfileRepository.getProfile().first()
            
            val updatedProfile = UserProfile(
                id = existing?.id ?: 1,
                email = email,
                googleName = displayName,
                googlePhotoUrl = photoUrl,
                localName = existing?.localName,
                localPhotoPath = existing?.localPhotoPath,
                nim = existing?.nim ?: "",
                major = existing?.major ?: "",
                currentStreak = existing?.currentStreak ?: 0,
                lastStudyDate = existing?.lastStudyDate,
                dailyMantra = existing?.dailyMantra ?: "Semangat Belajar!"
            )
            
            userProfileRepository.saveProfile(updatedProfile)
            _currentUser.value = updatedProfile
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }
}
