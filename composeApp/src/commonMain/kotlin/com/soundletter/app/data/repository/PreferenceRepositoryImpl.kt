package com.soundletter.app.data.repository

import com.soundletter.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenceRepositoryImpl : PreferenceRepository {
    private val _isDarkMode = MutableStateFlow(false)
    override val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    override fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }
}
