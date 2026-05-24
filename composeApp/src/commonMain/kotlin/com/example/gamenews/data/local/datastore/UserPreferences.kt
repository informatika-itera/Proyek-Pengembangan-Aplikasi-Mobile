package com.example.gamenews.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SORT_BY = stringPreferencesKey("sort_by")
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val SHOW_PREVIEW = booleanPreferencesKey("show_preview")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val FAVORITE_GENRE = stringPreferencesKey("favorite_genre")
    }

    // ==================== DARK MODE ====================
    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.DARK_MODE] = enabled }
    }

    // ==================== SORT BY ====================
    val sortBy: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SORT_BY] ?: "UPDATED_DESC"
    }
    suspend fun setSortBy(sortBy: String) {
        dataStore.edit { prefs -> prefs[Keys.SORT_BY] = sortBy }
    }

    // ==================== DEFAULT CATEGORY ====================
    val defaultCategory: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_CATEGORY] ?: "GENERAL"
    }
    suspend fun setDefaultCategory(category: String) {
        dataStore.edit { prefs -> prefs[Keys.DEFAULT_CATEGORY] = category }
    }

    // ==================== SHOW PREVIEW ====================
    val showPreview: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SHOW_PREVIEW] ?: true
    }
    suspend fun setShowPreview(show: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.SHOW_PREVIEW] = show }
    }

    // ==================== ONBOARDING ====================
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }
    suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs -> prefs[Keys.ONBOARDING_COMPLETED] = true }
    }

    // ==================== FAVORITE GENRE ====================
    val favoriteGenre: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.FAVORITE_GENRE]
    }
    suspend fun setFavoriteGenre(genre: String?) {
        dataStore.edit { prefs ->
            if (genre != null) prefs[Keys.FAVORITE_GENRE] = genre
            else prefs.remove(Keys.FAVORITE_GENRE)
        }
    }
}