package com.example.inventra.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface UserPreferences {
    val language: Flow<String>
    suspend fun setLanguage(languageCode: String)
    val isDarkMode: Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    val sortBy: Flow<String>
    suspend fun setSortBy(sortBy: String)
    val defaultCategory: Flow<String>
    suspend fun setDefaultCategory(category: String)
    val showPreview: Flow<Boolean>
    suspend fun setShowPreview(show: Boolean)
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted()
}

class UserPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferences {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SORT_BY = stringPreferencesKey("sort_by")
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val SHOW_PREVIEW = booleanPreferencesKey("show_preview")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LANGUAGE = stringPreferencesKey("language")
    }
    
    override val language: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE] ?: "id"
    }
    
    override suspend fun setLanguage(languageCode: String) {
        dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = languageCode
        }
    }
    
    override val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }
    
    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }
    
    override val sortBy: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SORT_BY] ?: "UPDATED_DESC"
    }
    
    override suspend fun setSortBy(sortBy: String) {
        dataStore.edit { prefs ->
            prefs[Keys.SORT_BY] = sortBy
        }
    }
    
    override val defaultCategory: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_CATEGORY] ?: "GENERAL"
    }
    
    override suspend fun setDefaultCategory(category: String) {
        dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_CATEGORY] = category
        }
    }
    
    override val showPreview: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SHOW_PREVIEW] ?: true
    }
    
    override suspend fun setShowPreview(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SHOW_PREVIEW] = show
        }
    }
    
    override val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }
    
    override suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
        }
    }
}
