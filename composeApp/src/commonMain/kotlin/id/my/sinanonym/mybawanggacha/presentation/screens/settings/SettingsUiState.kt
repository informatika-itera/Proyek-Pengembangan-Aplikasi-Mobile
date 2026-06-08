package id.my.sinanonym.mybawanggacha.presentation.screens.settings

import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiSettings
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val networkMode: NetworkMode = NetworkMode.Auto,
    val appColorScheme: AppColorScheme = AppColorScheme.CodeGeass,
    val aiApiSettings: AiApiSettings = AiApiSettings(),
    val requestUsage: SettingsRequestUsageUiState = SettingsRequestUsageUiState()
)

data class SettingsRequestUsageUiState(
    val usedLastSecond: Int = 0,
    val secondLimit: Int = 3,
    val usedLastMinute: Int = 0,
    val minuteLimit: Int = 60,
    val remainingThisMinute: Int = 60,
    val msUntilNextRequest: Long = 0L
) {
    val minuteProgress: Float
        get() = if (minuteLimit <= 0) 0f else usedLastMinute.toFloat() / minuteLimit.toFloat()

    val cooldownLabel: String
        get() = if (msUntilNextRequest <= 0L) {
            "Siap request"
        } else {
            "Cooldown ${msUntilNextRequest}ms"
        }
}
