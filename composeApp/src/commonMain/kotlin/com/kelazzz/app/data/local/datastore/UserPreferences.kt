package com.kelazzz.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kelazzz.app.domain.model.ThemeMode
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
        val THEME_MODE = stringPreferencesKey("theme_mode")
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
    
    // ==================== THEME MODE ====================
    
    /**
     * Observe theme mode setting.
     *
     * Jika preferensi baru belum ada, gunakan nilai boolean lama sebagai migrasi ringan.
     * Default tetap SYSTEM agar aplikasi mengikuti tema perangkat.
     */
    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        val storedMode = prefs[Keys.THEME_MODE]
        if (storedMode != null) {
            ThemeMode.fromStoredValue(storedMode)
        } else {
            when (prefs[Keys.DARK_MODE]) {
                true -> ThemeMode.DARK
                false -> ThemeMode.SYSTEM
                null -> ThemeMode.SYSTEM
            }
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
            prefs[Keys.DARK_MODE] = mode == ThemeMode.DARK
        }
    }

    /**
     * Observe dark mode setting.
     *
     * Dipertahankan untuk kompatibilitas kode lama. Untuk UI baru gunakan themeMode.
     */
    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        when (ThemeMode.fromStoredValue(prefs[Keys.THEME_MODE])) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT,
            ThemeMode.SYSTEM -> prefs[Keys.DARK_MODE] ?: false
        }
    }
    
    /**
     * Set dark mode.
     *
     * Dipertahankan untuk kompatibilitas kode lama.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        setThemeMode(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
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
     * Clear only session-related preferences during logout, preserving app preferences (theme, onboarding).
     */
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.AUTH_TOKEN)
            prefs[Keys.IS_LOGGED_IN] = false
            prefs.remove(Keys.USER_NIM)
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_PHOTO_URL)
            prefs.remove(Keys.DEVICE_NAME)
            prefs.remove(Keys.DEVICE_ID)
        }
    }

    /**
     * Clear all preferences (full factory reset)
     */
    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
