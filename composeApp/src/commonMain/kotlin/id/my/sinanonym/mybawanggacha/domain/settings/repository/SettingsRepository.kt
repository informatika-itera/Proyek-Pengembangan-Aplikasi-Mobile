package id.my.sinanonym.mybawanggacha.domain.settings.repository

import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiSettings
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiPersonality

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    val networkMode: Flow<NetworkMode>
    val appColorScheme: Flow<AppColorScheme>
    val aiApiSettings: Flow<AiApiSettings>

    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setNetworkMode(networkMode: NetworkMode)
    suspend fun setAppColorScheme(appColorScheme: AppColorScheme)
    suspend fun setAiApiModel(aiApiModel: AiApiModel)
    suspend fun setAiApiPersonality(aiPersonality: AiPersonality)
    suspend fun setAiApiToken(token: String)
}
