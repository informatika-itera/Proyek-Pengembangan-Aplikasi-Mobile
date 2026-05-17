package com.example.Roomie.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.Roomie.core.util.SecurityUtils
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = intPreferencesKey("theme_mode") // 0: System, 1: Light, 2: Dark
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_NIM = stringPreferencesKey("user_nim")
        val USER_ROLE = stringPreferencesKey("user_role")
    }
    
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
        }
    }
    
    /**
     * Theme Mode: 0 = System, 1 = Light, 2 = Dark
     */
    val themeMode: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: 0
    }
    
    suspend fun setThemeMode(mode: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode
        }
    }

    val userData: Flow<User?> = dataStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID]
        if (id == null) return@map null
        
        val obfuscatedNim = prefs[Keys.USER_NIM] ?: ""
        
        User(
            id = id,
            name = prefs[Keys.USER_NAME] ?: "",
            nim = SecurityUtils.deobfuscate(obfuscatedNim),
            role = UserRole.valueOf(prefs[Keys.USER_ROLE] ?: UserRole.STUDENT.name)
        )
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = user.id
            prefs[Keys.USER_NAME] = user.name
            prefs[Keys.USER_NIM] = SecurityUtils.obfuscate(user.nim)
            prefs[Keys.USER_ROLE] = user.role.name
        }
    }

    suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_NIM)
            prefs.remove(Keys.USER_ROLE)
        }
    }
}
