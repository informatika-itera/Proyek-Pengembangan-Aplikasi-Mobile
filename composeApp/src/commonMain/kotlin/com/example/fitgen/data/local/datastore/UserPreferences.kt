package com.example.fitgen.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fitgen.domain.model.UserProfile
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
        
        // --- Sprint 3: User Profile ---
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_HEIGHT = doublePreferencesKey("user_height")
        val USER_WEIGHT = doublePreferencesKey("user_weight")
        val USER_GOAL = stringPreferencesKey("user_goal")
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
    
    // ==================== USER PROFILE ====================
    
    /**
     * Observe User Profile
     */
    val userProfile: Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[Keys.USER_NAME] ?: "",
            age = prefs[Keys.USER_AGE] ?: 0,
            heightCm = prefs[Keys.USER_HEIGHT] ?: 0.0,
            weightKg = prefs[Keys.USER_WEIGHT] ?: 0.0,
            goal = prefs[Keys.USER_GOAL] ?: ""
        )
    }
    
    /**
     * Save User Profile
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = profile.name
            prefs[Keys.USER_AGE] = profile.age
            prefs[Keys.USER_HEIGHT] = profile.heightCm
            prefs[Keys.USER_WEIGHT] = profile.weightKg
            prefs[Keys.USER_GOAL] = profile.goal
        }
    }
}
