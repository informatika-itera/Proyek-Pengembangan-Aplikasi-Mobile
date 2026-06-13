package com.example.raillog.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SORT_BY = stringPreferencesKey("sort_by")
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val SHOW_PREVIEW = booleanPreferencesKey("show_preview")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

        // Akun & Sesi
        val USER_ROLE = stringPreferencesKey("user_role")
        val ACTIVE_USERNAME = stringPreferencesKey("active_username")
        
        // Multi-account storage (format: user|pass|name|id|phone;user2|...)
        val STAFF_ACCOUNTS = stringPreferencesKey("staff_accounts")
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs -> prefs[Keys.DARK_MODE] ?: false }
    suspend fun setDarkMode(enabled: Boolean) { dataStore.edit { prefs -> prefs[Keys.DARK_MODE] = enabled } }

    // ==================== USER ROLE & SESSION ====================
    val userRole: Flow<String> = dataStore.data.map { prefs -> prefs[Keys.USER_ROLE] ?: "" }
    open val activeUsername: Flow<String> = dataStore.data.map { prefs -> prefs[Keys.ACTIVE_USERNAME] ?: "" }

    suspend fun setUserRole(role: String) {
        dataStore.edit { prefs -> prefs[Keys.USER_ROLE] = role }
    }

    suspend fun setActiveUsername(username: String) {
        dataStore.edit { prefs -> prefs[Keys.ACTIVE_USERNAME] = username }
    }

    suspend fun clearUserSession() {
        dataStore.edit { prefs -> 
            prefs[Keys.USER_ROLE] = ""
            prefs[Keys.ACTIVE_USERNAME] = ""
        }
    }

    // ==================== REGISTER STAFF DATA ====================
    val staffAccounts: Flow<String> = dataStore.data.map { prefs -> prefs[Keys.STAFF_ACCOUNTS] ?: "" }

    suspend fun registerStaff(name: String, user: String, pass: String, employeeId: String, phone: String) {
        dataStore.edit { prefs ->
            val currentAccounts = prefs[Keys.STAFF_ACCOUNTS] ?: ""
            val newAccount = "$user|$pass|$name|$employeeId|$phone"
            prefs[Keys.STAFF_ACCOUNTS] = if (currentAccounts.isEmpty()) newAccount else "$currentAccounts;$newAccount"
        }
    }
}
