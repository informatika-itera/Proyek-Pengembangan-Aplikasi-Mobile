package com.studyhub.domain.usecase.preferences

import com.studyhub.domain.repository.PreferencesRepository

class SetDarkModeUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) =
        preferencesRepository.setDarkMode(enabled)
}
