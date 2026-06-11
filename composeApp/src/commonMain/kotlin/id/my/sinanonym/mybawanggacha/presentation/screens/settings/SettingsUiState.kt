package id.my.sinanonym.mybawanggacha.presentation.screens.settings

import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiSettings
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val networkMode: NetworkMode = NetworkMode.Auto,
    val appColorScheme: AppColorScheme = AppColorScheme.CodeGeass,
    val aiApiSettings: AiApiSettings = AiApiSettings(),
    val requestUsage: SettingsRequestUsageUiState = SettingsRequestUsageUiState(),
    val aiTokenUsage: SettingsAiTokenUsageUiState = SettingsAiTokenUsageUiState(),
    val release: SettingsReleaseUiState = SettingsReleaseUiState()
)

data class SettingsReleaseUiState(
    val isChecking: Boolean = false,
    val latestVersion: String = "",
    val latestName: String = "",
    val releaseUrl: String = "",
    val message: String = "Belum dicek",
    val isUpdateAvailable: Boolean = false,
    val error: String = ""
) {
    val canOpenRelease: Boolean
        get() = releaseUrl.isNotBlank()
}

data class SettingsRequestUsageUiState(
    val usedLastSecond: Int = 0,
    val secondLimit: Int = 3,
    val usedLastMinute: Int = 0,
    val minuteLimit: Int = 60,
    val remainingThisMinute: Int = 60,
    val msUntilNextRequest: Long = 0L,
    val serviceStatus: SettingsJikanServiceStatusUiState = SettingsJikanServiceStatusUiState()
) {
    val minuteProgress: Float
        get() = if (minuteLimit <= 0) 0f else usedLastMinute.toFloat() / minuteLimit.toFloat()

    val isRequestReady: Boolean
        get() = msUntilNextRequest <= 0L

    val requestReadyLabel: String
        get() = if (isRequestReady) {
            "request ready"
        } else {
            "cooldown"
        }
}

data class SettingsJikanServiceStatusUiState(
    val label: String = "checking",
    val isActive: Boolean = false,
    val isChecking: Boolean = true,
    val statusCode: Int? = null,
    val type: String = "",
    val message: String = ""
) {
    val shortDetail: String
        get() = when {
            isChecking -> ""
            isActive -> ""
            statusCode != null && type.isNotBlank() -> "$statusCode • $type"
            statusCode != null -> statusCode.toString()
            type.isNotBlank() -> type
            else -> message
        }
}

data class SettingsAiTokenUsageUiState(
    val entries: List<SettingsAiModelTokenUsageUiState> = AiApiModel.entries.map { model ->
        SettingsAiModelTokenUsageUiState(
            model = model,
            label = model.label,
            modelId = model.modelId,
            inputTokenLimit = model.inputTokenLimit,
            outputTokenLimit = model.outputTokenLimit,
            appOutputTokenLimit = model.appOutputTokenLimit,
            effectiveOutputTokenLimit = model.effectiveOutputTokenLimit
        )
    }
) {
    val totalRequests: Long
        get() = entries.sumOf { it.requestCount }

    val totalTokens: Long
        get() = entries.sumOf { it.totalTokens }
}

data class SettingsAiModelTokenUsageUiState(
    val model: AiApiModel,
    val label: String,
    val modelId: String,
    val requestCount: Long = 0L,
    val promptTokens: Long = 0L,
    val candidatesTokens: Long = 0L,
    val thoughtsTokens: Long = 0L,
    val cachedContentTokens: Long = 0L,
    val totalTokens: Long = 0L,
    val lastPromptTokens: Int = 0,
    val lastCandidatesTokens: Int = 0,
    val lastThoughtsTokens: Int = 0,
    val lastCachedContentTokens: Int = 0,
    val lastTotalTokens: Int = 0,
    val inputTokenLimit: Int,
    val outputTokenLimit: Int,
    val appOutputTokenLimit: Int,
    val effectiveOutputTokenLimit: Int,
    val updatedAtMillis: Long = 0L
) {
    val lastInputProgress: Float
        get() = if (inputTokenLimit <= 0) 0f else lastPromptTokens.toFloat() / inputTokenLimit.toFloat()
}
