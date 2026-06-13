package com.example.fitgen.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
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
        val USER_GENDER = stringPreferencesKey("user_gender")
        val USER_HEIGHT = doublePreferencesKey("user_height")
        val USER_WEIGHT = doublePreferencesKey("user_weight")
        val USER_GOAL = stringPreferencesKey("user_goal")
        val USER_PROFILE_IMAGE_BASE64 = stringPreferencesKey("user_profile_image_base64")
        
        // --- Sprint 4: Streak & Hydration ---
        val LAST_LOGIN_DATE = longPreferencesKey("last_login_date")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LAST_HYDRATION_DATE = longPreferencesKey("last_hydration_date")
        val WATER_GLASSES = intPreferencesKey("water_glasses")
        
        // --- Sprint 5: Challenges ---
        val COMPLETED_CHALLENGE_DAYS = stringPreferencesKey("completed_challenge_days")
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
            gender = prefs[Keys.USER_GENDER] ?: "",
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
            prefs[Keys.USER_GENDER] = profile.gender
            prefs[Keys.USER_HEIGHT] = profile.heightCm
            prefs[Keys.USER_WEIGHT] = profile.weightKg
            prefs[Keys.USER_GOAL] = profile.goal
        }
    }

    /**
     * Menghapus semua preferensi user (Logout / Reset)
     */
    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    
    val userProfileImageBase64: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.USER_PROFILE_IMAGE_BASE64]
    }
    
    suspend fun saveUserProfileImageBase64(base64: String?) {
        dataStore.edit { prefs ->
            if (base64 == null) {
                prefs.remove(Keys.USER_PROFILE_IMAGE_BASE64)
            } else {
                prefs[Keys.USER_PROFILE_IMAGE_BASE64] = base64
            }
        }
    }
    
    // ==================== STREAK & HYDRATION ====================
    
    val lastLoginDate: Flow<Long> = dataStore.data.map { prefs -> prefs[Keys.LAST_LOGIN_DATE] ?: 0L }
    val currentStreak: Flow<Int> = dataStore.data.map { prefs -> prefs[Keys.CURRENT_STREAK] ?: 0 }
    
    suspend fun setLoginStreak(lastDate: Long, streak: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_LOGIN_DATE] = lastDate
            prefs[Keys.CURRENT_STREAK] = streak
        }
    }
    
    val lastHydrationDate: Flow<Long> = dataStore.data.map { prefs -> prefs[Keys.LAST_HYDRATION_DATE] ?: 0L }
    val waterGlasses: Flow<Int> = dataStore.data.map { prefs -> prefs[Keys.WATER_GLASSES] ?: 0 }
    
    suspend fun setHydration(lastDate: Long, glasses: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_HYDRATION_DATE] = lastDate
            prefs[Keys.WATER_GLASSES] = glasses
        }
    }

    // ==================== CHALLENGES ====================
    val completedChallengeDays: Flow<Set<String>> = dataStore.data.map { prefs ->
        val savedString = prefs[Keys.COMPLETED_CHALLENGE_DAYS] ?: ""
        if (savedString.isEmpty()) emptySet() else savedString.split(",").toSet()
    }

    suspend fun markChallengeDayCompleted(challengeId: String, day: Int) {
        dataStore.edit { prefs ->
            val savedString = prefs[Keys.COMPLETED_CHALLENGE_DAYS] ?: ""
            val currentSet = if (savedString.isEmpty()) emptySet() else savedString.split(",").toSet()
            val newSet = currentSet.toMutableSet()
            newSet.add("${challengeId}_$day")
            prefs[Keys.COMPLETED_CHALLENGE_DAYS] = newSet.joinToString(",")
        }
    }
}
