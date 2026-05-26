package com.example.hujjah.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val READING_DURATION_SECONDS = intPreferencesKey("reading_duration_seconds")
        val CURRENT_STREAK_DAYS = intPreferencesKey("current_streak_days")
        val LAST_READ_DATE = stringPreferencesKey("last_read_date")
        val LAST_READ_QURAN_LOC = stringPreferencesKey("last_read_quran_loc") // e.g. "QS. Al-Kahfi: Ayat 10"
        val ARABIC_FONT_SIZE = intPreferencesKey("arabic_font_size")
        val PROFILE_IMAGE_BASE64 = stringPreferencesKey("profile_image_base64")
    }
    
    // ==================== DARK MODE ====================
    
    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    // ==================== USER NAME ====================
    
    val userName: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.USER_NAME] ?: "Awi Septian Prasetyo"
    }
    
    suspend fun setUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }
    
    // ==================== PROFILE IMAGE ====================
    
    val profileImageBase64: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.PROFILE_IMAGE_BASE64] ?: ""
    }
    
    suspend fun setProfileImageBase64(base64: String) {
        dataStore.edit { prefs ->
            prefs[Keys.PROFILE_IMAGE_BASE64] = base64
        }
    }
    
    // ==================== QURAN ENGAGEMENT ANALYTICS ====================
    
    val readingDurationSeconds: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.READING_DURATION_SECONDS] ?: 0
    }
    
    suspend fun addReadingDuration(seconds: Int) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.READING_DURATION_SECONDS] ?: 0
            prefs[Keys.READING_DURATION_SECONDS] = current + seconds
        }
    }
    
    suspend fun resetReadingDuration() {
        dataStore.edit { prefs ->
            prefs[Keys.READING_DURATION_SECONDS] = 0
        }
    }

    // ==================== STREAK COUNTER ====================
    
    val currentStreakDays: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.CURRENT_STREAK_DAYS] ?: 0
    }
    
    suspend fun updateStreak() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        dataStore.edit { prefs ->
            val lastReadDate = prefs[Keys.LAST_READ_DATE] ?: ""
            if (lastReadDate != today) {
                val currentStreak = prefs[Keys.CURRENT_STREAK_DAYS] ?: 0
                prefs[Keys.CURRENT_STREAK_DAYS] = currentStreak + 1
                prefs[Keys.LAST_READ_DATE] = today
            }
        }
    }
    
    // ==================== THE GOLDEN CARD (LAST READ) ====================
    
    val lastReadQuranLocation: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.LAST_READ_QURAN_LOC] ?: ""
    }
    
    suspend fun setLastReadQuranLocation(location: String) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_READ_QURAN_LOC] = location
        }
    }

    // ==================== FONT SIZE ====================
    
    val arabicFontSize: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.ARABIC_FONT_SIZE] ?: 22
    }
    
    suspend fun setArabicFontSize(size: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.ARABIC_FONT_SIZE] = size
        }
    }
}
