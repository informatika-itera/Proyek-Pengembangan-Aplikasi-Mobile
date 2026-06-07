package com.studyhub.domain.usecase.preferences

import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

open class GetUserPreferencesUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    open operator fun invoke(): Flow<UserPreferences> =
        preferencesRepository.userPreferences
}
