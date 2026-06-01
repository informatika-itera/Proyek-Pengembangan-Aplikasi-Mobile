package com.example.fitkos.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val USER_NAME = stringPreferencesKey("user_name")
        val WATER_TARGET = intPreferencesKey("water_target")

        val AI_CACHED_PROMPT = stringPreferencesKey("ai_cached_prompt")
        val AI_CACHED_RESPONSE = stringPreferencesKey("ai_cached_response")
        val AI_CACHED_UPDATED_AT = stringPreferencesKey("ai_cached_updated_at")

        val EXERCISE_MINUTES_TODAY = intPreferencesKey("exercise_minutes_today")
        val EXERCISE_LAST_DATE = stringPreferencesKey("exercise_last_date")
    }
    
    // ==================== PROFILE ====================

    /**
     * Observe user name
     */
    val userName: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.USER_NAME] ?: "Sobat Kos"
    }

    /**
     * Set user name
     */
    suspend fun setUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }

    /**
     * Observe water target
     */
    val waterTarget: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.WATER_TARGET] ?: 8
    }

    /**
     * Set water target
     */
    suspend fun setWaterTarget(target: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.WATER_TARGET] = target
        }
    }

    // ==================== EXERCISE ====================

    /**
     * Observe exercise minutes today
     */
    val exerciseMinutesToday: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.EXERCISE_MINUTES_TODAY] ?: 0
    }

    /**
     * Set exercise minutes today
     */
    suspend fun setExerciseMinutesToday(minutes: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.EXERCISE_MINUTES_TODAY] = minutes.coerceAtLeast(0)
        }
    }

    /**
     * Add exercise minutes today
     */
    suspend fun addExerciseMinutesToday(minutes: Int) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.EXERCISE_MINUTES_TODAY] ?: 0
            prefs[Keys.EXERCISE_MINUTES_TODAY] = (current + minutes).coerceAtLeast(0)
        }
    }

    /**
     * Reset exercise minutes today
     */
    suspend fun resetExerciseMinutesToday() {
        dataStore.edit { prefs ->
            prefs[Keys.EXERCISE_MINUTES_TODAY] = 0
        }
    }

    suspend fun resetExerciseIfNewDay(todayDate: String) {
        dataStore.edit { prefs ->
            val lastDate = prefs[Keys.EXERCISE_LAST_DATE]

            if (lastDate != todayDate) {
                prefs[Keys.EXERCISE_MINUTES_TODAY] = 0
                prefs[Keys.EXERCISE_LAST_DATE] = todayDate
            }
        }
    }

    // ==================== AI CACHE ====================

    /**
     * Observe cached AI prompt
     */
    val cachedAIPrompt: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.AI_CACHED_PROMPT] ?: ""
    }

    /**
     * Observe cached AI response
     */
    val cachedAIResponse: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.AI_CACHED_RESPONSE] ?: ""
    }

    /**
     * Observe cached AI updated time
     */
    val cachedAIUpdatedAt: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.AI_CACHED_UPDATED_AT] ?: ""
    }

    /**
     * Save latest AI response cache
     */
    suspend fun saveAIResponseCache(
        prompt: String,
        response: String,
        updatedAt: String
    ) {
        dataStore.edit { prefs ->
            prefs[Keys.AI_CACHED_PROMPT] = prompt
            prefs[Keys.AI_CACHED_RESPONSE] = response
            prefs[Keys.AI_CACHED_UPDATED_AT] = updatedAt
        }
    }

    /**
     * Clear AI response cache
     */
    suspend fun clearAIResponseCache() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.AI_CACHED_PROMPT)
            prefs.remove(Keys.AI_CACHED_RESPONSE)
            prefs.remove(Keys.AI_CACHED_UPDATED_AT)
        }
    }

    // ==================== DARK MODE ====================
    
    /**
     * Observe dark mode setting
     */
    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }
    
    /**
     * Set dark mode
     */
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
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
