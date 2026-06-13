package com.example.pocketguard.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
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
        // BUDGET_LIMIT statis dihapus dari sini
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[Keys.DARK_MODE] ?: false }
    suspend fun setDarkMode(enabled: Boolean) { dataStore.edit { it[Keys.DARK_MODE] = enabled } }

    val sortBy: Flow<String> = dataStore.data.map { it[Keys.SORT_BY] ?: "UPDATED_DESC" }
    suspend fun setSortBy(sortBy: String) { dataStore.edit { it[Keys.SORT_BY] = sortBy } }

    val defaultCategory: Flow<String> = dataStore.data.map { it[Keys.DEFAULT_CATEGORY] ?: "GENERAL" }
    suspend fun setDefaultCategory(category: String) { dataStore.edit { it[Keys.DEFAULT_CATEGORY] = category } }

    val showPreview: Flow<Boolean> = dataStore.data.map { it[Keys.SHOW_PREVIEW] ?: true }
    suspend fun setShowPreview(show: Boolean) { dataStore.edit { it[Keys.SHOW_PREVIEW] = show } }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { it[Keys.ONBOARDING_COMPLETED] ?: false }
    suspend fun setOnboardingCompleted() { dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = true } }

    // ==================== BUDGET LIMIT DINAMIS ====================

    fun getBudgetLimit(monthYear: String): Flow<Double> {
        val dynamicKey = doublePreferencesKey("budget_limit_$monthYear")
        return dataStore.data.map { prefs ->
            prefs[dynamicKey] ?: 0.0
        }
    }

    suspend fun setBudgetLimit(monthYear: String, limit: Double) {
        val dynamicKey = doublePreferencesKey("budget_limit_$monthYear")
        dataStore.edit { prefs ->
            prefs[dynamicKey] = limit
        }
    }
}