package com.studymate.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun isDarkTheme(): Flow<Boolean>
    suspend fun setDarkTheme(isDark: Boolean)
}
