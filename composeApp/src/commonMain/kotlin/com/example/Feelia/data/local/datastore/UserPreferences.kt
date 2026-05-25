package com.example.Feelia.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SORT_BY = stringPreferencesKey("sort_by")
        val SHOW_PREVIEW = booleanPreferencesKey("show_preview")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }

    // Theme
    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    // Sort
    val sortBy: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SORT_BY] ?: "UPDATED_DESC"
    }

    suspend fun setSortBy(sortBy: String) {
        dataStore.edit { it[Keys.SORT_BY] = sortBy }
    }

    // Show Preview
    val showPreview: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SHOW_PREVIEW] ?: true
    }

    suspend fun setShowPreview(show: Boolean) {
        dataStore.edit { it[Keys.SHOW_PREVIEW] = show }
    }

    // Onboarding
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = true }
    }

    // Notification
    val isNotificationEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATION_ENABLED] ?: true
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.NOTIFICATION_ENABLED] = enabled }
    }
}

enum class ThemeMode(val displayName: String) {
    LIGHT("Terang"),
    DARK("Gelap"),
    SYSTEM("Ikuti Sistem")
}