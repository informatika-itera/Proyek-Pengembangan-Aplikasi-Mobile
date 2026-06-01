package com.example.tabungin.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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

        val NAMA_USER = stringPreferencesKey("nama_user")

        // Notification settings
        val NOTIFIKASI_AKTIF = booleanPreferencesKey("notifikasi_aktif")
        val NOTIFIKASI_JAM = intPreferencesKey("notifikasi_jam")
        val NOTIFIKASI_MENIT = intPreferencesKey("notifikasi_menit")
        val NOTIF_TARGET_TERCAPAI = booleanPreferencesKey("notif_target_tercapai")
    }

    val namaUser: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.NAMA_USER] ?: "" // Default string kosong jika belum diisi
    }

    suspend fun setNamaUser(name: String) {
        dataStore.edit { prefs ->
            prefs[Keys.NAMA_USER] = name
        }
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    val sortBy: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SORT_BY] ?: "UPDATED_DESC"
    }

    suspend fun setSortBy(sortBy: String) {
        dataStore.edit { prefs ->
            prefs[Keys.SORT_BY] = sortBy
        }
    }

    val defaultCategory: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_CATEGORY] ?: "GENERAL"
    }

    suspend fun setDefaultCategory(category: String) {
        dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_CATEGORY] = category
        }
    }

    val showPreview: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SHOW_PREVIEW] ?: true
    }

    suspend fun setShowPreview(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SHOW_PREVIEW] = show
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

    // Notification Settings
    val notifikasiAktif: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.NOTIFIKASI_AKTIF] ?: true
    }

    suspend fun setNotifikasiAktif(aktif: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFIKASI_AKTIF] = aktif
        }
    }

    val notifikasiJam: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.NOTIFIKASI_JAM] ?: 9 // Default 09:00
    }

    suspend fun setNotifikasiJam(jam: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFIKASI_JAM] = jam
        }
    }

    val notifikasiMenit: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.NOTIFIKASI_MENIT] ?: 0
    }

    suspend fun setNotifikasiMenit(menit: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFIKASI_MENIT] = menit
        }
    }

    val notifTargetTercapai: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.NOTIF_TARGET_TERCAPAI] ?: true
    }

    suspend fun setNotifTargetTercapai(aktif: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIF_TARGET_TERCAPAI] = aktif
        }
    }
}