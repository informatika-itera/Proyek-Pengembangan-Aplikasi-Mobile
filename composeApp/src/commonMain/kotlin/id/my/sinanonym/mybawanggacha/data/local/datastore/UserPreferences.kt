package id.my.sinanonym.mybawanggacha.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * User Preferences menggunakan DataStore
 * 
 * DataStore adalah pengganti SharedPreferences yang lebih modern:
 * - Asynchronous dengan Coroutines dan Flow
 * - Type-safe dengan Preferences Keys
 * - Tidak blocking main thread
 * 
 * @param dataStore Instance DataStore dari platform
 */
class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    // ==================== PREFERENCE KEYS ====================
    
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SORT_BY = stringPreferencesKey("sort_by")
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val SHOW_PREVIEW = booleanPreferencesKey("show_preview")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val NETWORK_MODE = stringPreferencesKey("network_mode")
        val COLOR_SCHEME = stringPreferencesKey("color_scheme")
        val AI_API_MODEL = stringPreferencesKey("ai_api_model")
        val AI_API_PERSONALITY = stringPreferencesKey("ai_api_personality")
        val AI_API_TOKEN = stringPreferencesKey("ai_api_token")
        val AI_TOKEN_USAGE = stringPreferencesKey("ai_token_usage")
        val GACHA_PREFERENCE = stringPreferencesKey("gacha_preference")
        val GACHA_HISTORY = stringPreferencesKey("gacha_history")
    }
    
    // ==================== DARK MODE ====================
    
    /**
     * Observe dark mode setting
     */
    val isDarkMode: Flow<Boolean?> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE]
    }
    
    /**
     * Set dark mode. Null means follow system theme.
     */
    suspend fun setDarkMode(enabled: Boolean?) {
        dataStore.edit { prefs ->
            if (enabled == null) {
                prefs.remove(Keys.DARK_MODE)
            } else {
                prefs[Keys.DARK_MODE] = enabled
            }
        }
    }
    
    // ==================== SORT BY ====================
    
    /**
     * Observe sort preference
     */
    val sortBy: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SORT_BY] ?: "UPDATED_DESC"
    }
    
    /**
     * Set sort preference
     */
    suspend fun setSortBy(sortBy: String) {
        dataStore.edit { prefs ->
            prefs[Keys.SORT_BY] = sortBy
        }
    }
    
    // ==================== DEFAULT CATEGORY ====================
    
    /**
     * Observe default category
     */
    val defaultCategory: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_CATEGORY] ?: "GENERAL"
    }
    
    /**
     * Set default category
     */
    suspend fun setDefaultCategory(category: String) {
        dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_CATEGORY] = category
        }
    }
    
    // ==================== SHOW PREVIEW ====================
    
    /**
     * Observe show preview setting
     */
    val showPreview: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SHOW_PREVIEW] ?: true
    }
    
    /**
     * Set show preview
     */
    suspend fun setShowPreview(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SHOW_PREVIEW] = show
        }
    }
    

    // ==================== NETWORK MODE ====================

    /**
     * Observe network mode.
     *
     * Values are stored as enum names from domain settings. The datastore layer
     * intentionally stays string-based so it does not depend on domain models.
     */
    val networkMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.NETWORK_MODE] ?: "Auto"
    }

    suspend fun setNetworkMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[Keys.NETWORK_MODE] = mode
        }
    }

    // ==================== COLOR SCHEME ====================

    val colorScheme: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.COLOR_SCHEME] ?: "CodeGeass"
    }

    suspend fun setColorScheme(value: String) {
        dataStore.edit { prefs ->
            prefs[Keys.COLOR_SCHEME] = value
        }
    }

    // ==================== AI API ====================

    val aiApiModel: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.AI_API_MODEL] ?: "Gemini35Flash"
    }

    suspend fun setAiApiModel(value: String) {
        dataStore.edit { prefs ->
            prefs[Keys.AI_API_MODEL] = value
        }
    }

    val aiApiPersonality: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.AI_API_PERSONALITY] ?: "Default"
    }

    suspend fun setAiApiPersonality(value: String) {
        dataStore.edit { prefs ->
            prefs[Keys.AI_API_PERSONALITY] = value
        }
    }

    val aiApiToken: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.AI_API_TOKEN] ?: ""
    }

    suspend fun setAiApiToken(value: String) {
        dataStore.edit { prefs ->
            prefs[Keys.AI_API_TOKEN] = value
        }
    }

    val aiTokenUsageJson: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.AI_TOKEN_USAGE] ?: ""
    }

    suspend fun setAiTokenUsageJson(value: String) {
        dataStore.edit { prefs ->
            if (value.isBlank()) {
                prefs.remove(Keys.AI_TOKEN_USAGE)
            } else {
                prefs[Keys.AI_TOKEN_USAGE] = value
            }
        }
    }

    // ==================== GACHA ====================

    val gachaPreferenceJson: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.GACHA_PREFERENCE] ?: ""
    }

    suspend fun setGachaPreferenceJson(value: String) {
        dataStore.edit { prefs ->
            prefs[Keys.GACHA_PREFERENCE] = value
        }
    }

    val gachaHistoryJson: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.GACHA_HISTORY] ?: "[]"
    }

    suspend fun setGachaHistoryJson(value: String) {
        dataStore.edit { prefs ->
            prefs[Keys.GACHA_HISTORY] = value
        }
    }

    // ==================== ONBOARDING ====================
    
    /**
     * Check if onboarding completed
     */
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }
    
    /**
     * Set onboarding completed
     */
    suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
        }
    }
}
