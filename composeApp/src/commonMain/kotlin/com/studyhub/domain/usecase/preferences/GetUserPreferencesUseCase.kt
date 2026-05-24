package com.studyhub.domain.usecase.preferences

import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetUserPreferencesUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> =
        preferencesRepository.userPreferences
}
