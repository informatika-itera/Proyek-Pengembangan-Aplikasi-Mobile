package id.my.sinanonym.mybawanggacha.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanRequestUsage
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode
import id.my.sinanonym.mybawanggacha.domain.settings.repository.JikanRequestUsageRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiPersonality

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val requestUsageRepository: JikanRequestUsageRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.networkMode,
        settingsRepository.appColorScheme,
        settingsRepository.aiApiSettings,
        requestUsageRepository.usage
    ) { themeMode, networkMode, appColorScheme, aiApiSettings, requestUsage ->
        SettingsUiState(
            themeMode = themeMode,
            networkMode = networkMode,
            appColorScheme = appColorScheme,
            aiApiSettings = aiApiSettings,
            requestUsage = requestUsage.toUiState()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }

    fun setNetworkMode(networkMode: NetworkMode) {
        viewModelScope.launch {
            settingsRepository.setNetworkMode(networkMode)
        }
    }

    fun setAppColorScheme(appColorScheme: AppColorScheme) {
        viewModelScope.launch {
            settingsRepository.setAppColorScheme(appColorScheme)
        }
    }

    fun setAiApiModel(aiApiModel: AiApiModel) {
        viewModelScope.launch {
            settingsRepository.setAiApiModel(aiApiModel)
        }
    }

    fun setAiApiPersonality(aiPersonality: AiPersonality) {
        viewModelScope.launch {
            settingsRepository.setAiApiPersonality(aiPersonality)
        }
    }

    fun setAiApiToken(token: String) {
        viewModelScope.launch {
            settingsRepository.setAiApiToken(token)
        }
    }

    private fun JikanRequestUsage.toUiState(): SettingsRequestUsageUiState {
        return SettingsRequestUsageUiState(
            usedLastSecond = usedLastSecond,
            secondLimit = secondLimit,
            usedLastMinute = usedLastMinute,
            minuteLimit = minuteLimit,
            remainingThisMinute = remainingThisMinute,
            msUntilNextRequest = msUntilNextRequest
        )
    }
}
