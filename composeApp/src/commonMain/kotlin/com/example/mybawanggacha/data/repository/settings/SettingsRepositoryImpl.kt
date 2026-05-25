package com.example.mybawanggacha.data.repository.settings

import com.example.mybawanggacha.data.local.datastore.UserPreferences
import com.example.mybawanggacha.domain.settings.model.ThemeMode
import com.example.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val userPreferences: UserPreferences
) : SettingsRepository {

    override val themeMode: Flow<ThemeMode> = userPreferences.isDarkMode.map { enabled ->
        when (enabled) {
            null -> ThemeMode.System
            true -> ThemeMode.Dark
            false -> ThemeMode.Light
        }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        userPreferences.setDarkMode(
            when (themeMode) {
                ThemeMode.System -> null
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }
        )
    }
}
