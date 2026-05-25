package com.example.mybawanggacha.domain.settings.repository

import com.example.mybawanggacha.domain.settings.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(themeMode: ThemeMode)
}
