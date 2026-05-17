package com.studymate.domain.usecase

import com.studymate.domain.model.UserProfile
import com.studymate.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(
    private val profileRepository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return profileRepository.getProfile()
    }
}
