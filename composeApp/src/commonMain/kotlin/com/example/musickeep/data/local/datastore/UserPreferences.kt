package com.example.musickeep.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {
    private val THEME_KEY = booleanPreferencesKey("is_dark_mode")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    // Default tema adalah dark (true) untuk music app
    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: true
    }

    val userName: Flow<String> = dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: "Music Explorer"
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = enabled
        }
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }
}
