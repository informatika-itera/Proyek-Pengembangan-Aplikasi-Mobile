package id.my.sinanonym.mybawanggacha.data.repository.settings

import id.my.sinanonym.mybawanggacha.data.local.datastore.UserPreferences
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiSettings
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiPersonality

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

    override val networkMode: Flow<NetworkMode> = userPreferences.networkMode.map { value ->
        NetworkMode.fromString(value)
    }

    override val appColorScheme: Flow<AppColorScheme> = userPreferences.colorScheme.map { value ->
        AppColorScheme.fromString(value)
    }

    override val aiApiSettings: Flow<AiApiSettings> = combine(
        userPreferences.aiApiModel,
        userPreferences.aiApiPersonality,
        userPreferences.aiApiToken
    ) { model, personality, token ->
        AiApiSettings(
            model = AiApiModel.fromString(model),
            personality = AiPersonality.fromString(personality),
            token = token
        )
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

    override suspend fun setNetworkMode(networkMode: NetworkMode) {
        userPreferences.setNetworkMode(networkMode.name)
    }

    override suspend fun setAppColorScheme(appColorScheme: AppColorScheme) {
        userPreferences.setColorScheme(appColorScheme.name)
    }

    override suspend fun setAiApiModel(aiApiModel: AiApiModel) {
        userPreferences.setAiApiModel(aiApiModel.name)
    }

    override suspend fun setAiApiPersonality(aiPersonality: AiPersonality) {
        userPreferences.setAiApiPersonality(aiPersonality.name)
    }

    override suspend fun setAiApiToken(token: String) {
        userPreferences.setAiApiToken(token.trim())
    }
}
