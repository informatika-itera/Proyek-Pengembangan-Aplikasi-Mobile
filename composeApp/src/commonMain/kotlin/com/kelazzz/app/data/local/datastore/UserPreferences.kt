package com.kelazzz.app.data.local.datastore

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
 * Menyimpan preferensi dan session token untuk KelazZz:
 * - Bearer token dari Pocket ITERA API
 * - Dark mode preference
 * - Onboarding status
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
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_NIM = stringPreferencesKey("user_nim")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val DEVICE_NAME = stringPreferencesKey("device_name")
        val DEVICE_ID = stringPreferencesKey("device_id")
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
    
    // ==================== AUTH TOKEN (SESSION) ====================
    
    /**
     * Observe auth token (Bearer token dari Pocket ITERA)
     */
    val authToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.AUTH_TOKEN]
    }
    
    /**
     * Save auth token setelah login berhasil
     */
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { prefs ->
            prefs[Keys.AUTH_TOKEN] = token
            prefs[Keys.IS_LOGGED_IN] = true
        }
    }
    
    /**
     * Clear auth token saat logout
     */
    suspend fun clearAuthToken() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.AUTH_TOKEN)
            prefs[Keys.IS_LOGGED_IN] = false
        }
    }
    
    // ==================== USER INFO ====================
    
    val userNim: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.USER_NIM]
    }
    
    val userName: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.USER_NAME]
    }
    
    val userEmail: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.USER_EMAIL]
    }
    
    val userPhotoUrl: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.USER_PHOTO_URL]
    }
    
    suspend fun saveUserInfo(nim: String, name: String, email: String, photoUrl: String) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_NIM] = nim
            prefs[Keys.USER_NAME] = name
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.USER_PHOTO_URL] = photoUrl
        }
    }
    
    // ==================== LOGIN STATE ====================
    
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_LOGGED_IN] ?: false
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
    
    // ==================== DEVICE INFO ====================
    
    val deviceId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.DEVICE_ID]
    }
    
    val deviceName: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.DEVICE_NAME]
    }
    
    suspend fun saveDeviceInfo(device: String, deviceId: String) {
        dataStore.edit { prefs ->
            prefs[Keys.DEVICE_NAME] = device
            prefs[Keys.DEVICE_ID] = deviceId
        }
    }
    
    /**
     * Clear all preferences (full logout)
     */
    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
