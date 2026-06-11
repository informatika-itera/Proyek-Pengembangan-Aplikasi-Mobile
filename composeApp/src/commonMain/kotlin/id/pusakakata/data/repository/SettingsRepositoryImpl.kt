package id.pusakakata.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import id.pusakakata.domain.repository.SettingsRepository
import id.pusakakata.presentation.screens.settings.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    override fun getTheme(): Flow<AppTheme> {
        return dataStore.data.map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME_KEY] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_KEY] = theme.name
        }
    }
}
