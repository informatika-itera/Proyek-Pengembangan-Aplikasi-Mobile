package id.pusakakata.domain.repository

import id.pusakakata.presentation.screens.settings.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
}
