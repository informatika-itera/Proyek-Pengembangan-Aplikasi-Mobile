package com.soundletter.app.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface PreferenceRepository {
    val isDarkMode: StateFlow<Boolean>
    fun toggleDarkMode(enabled: Boolean)
}
