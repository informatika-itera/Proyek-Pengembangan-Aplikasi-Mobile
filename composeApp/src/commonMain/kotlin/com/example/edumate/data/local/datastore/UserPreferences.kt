package com.example.edumate.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Enum untuk pilihan Tema
enum class ThemeMode(val displayName: String) {
    LIGHT("Mode Terang"),
    DARK("Mode Gelap"),
    SYSTEM("Sistem Default")
}

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    // Membaca preferensi tema (Default ke SYSTEM)
    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
    }

    // Menyimpan preferensi tema
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }
}