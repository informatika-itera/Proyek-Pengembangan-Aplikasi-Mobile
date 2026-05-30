package com.example.foodsaver.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

/**
 * User Preferences using DataStore for FoodSaver.
 */
class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SORT_BY = stringPreferencesKey("sort_by")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        
        // Notifications
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val REMINDER_DAYS = intPreferencesKey("reminder_days") // 0, 1, 3
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
    }
    
    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        val mode = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(mode)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }
    
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }
    
    val sortBy: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SORT_BY] ?: "EXPIRY_ASC"
    }
    
    suspend fun setSortBy(sortBy: String) {
        dataStore.edit { prefs ->
            prefs[Keys.SORT_BY] = sortBy
        }
    }
    
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }
    
    suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
        }
    }

    // Notifications logic
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val reminderDays: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.REMINDER_DAYS] ?: 1
    }

    suspend fun setReminderDays(days: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.REMINDER_DAYS] = days
        }
    }

    val reminderTime: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.REMINDER_TIME] ?: "08:00"
    }

    suspend fun setReminderTime(time: String) {
        dataStore.edit { prefs ->
            prefs[Keys.REMINDER_TIME] = time
        }
    }
}
