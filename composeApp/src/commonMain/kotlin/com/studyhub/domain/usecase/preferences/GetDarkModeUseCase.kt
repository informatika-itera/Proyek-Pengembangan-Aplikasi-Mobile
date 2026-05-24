package com.studyhub.domain.usecase.preferences

import com.studyhub.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetDarkModeUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> =
        preferencesRepository.isDarkMode
}
