package com.example.neurodeck.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * DataStore-backed implementation of [UserPreferencesRepository].
 *
 * Pattern: setiap field UserProfile + ThemeMode dipetakan ke satu key di
 * Preferences. Read via Flow (auto-reactive), write via `dataStore.edit { }`.
 *
 * Storage location:
 *   Android: `/data/data/com.example.neurodeck/files/neurodeck.preferences_pb`
 *
 * Format: binary protobuf (managed by DataStore library).
 */
class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    // ════════════════════════════════════════════════════════════════════════
    // KEYS — DataStore preference keys
    // ════════════════════════════════════════════════════════════════════════
    // Pakai prefix "user_" supaya tidak collision dengan key dari modul lain
    // (e.g. kalau nanti ada SettingsRepository terpisah).

    private object Keys {
        val NAME = stringPreferencesKey("user_name")
        val USERNAME = stringPreferencesKey("user_username")
        val BIO = stringPreferencesKey("user_bio")
        val AVATAR_URI = stringPreferencesKey("user_avatar_uri")
        val MEMBER_SINCE = longPreferencesKey("user_member_since")  // epoch millis
        val THEME_MODE = stringPreferencesKey("settings_theme_mode")
    }

    // ════════════════════════════════════════════════════════════════════════
    // PROFILE
    // ════════════════════════════════════════════════════════════════════════

    override fun observeProfile(): Flow<UserProfile> = dataStore.data.map { prefs ->
        prefs.toUserProfile()
    }

    override suspend fun getProfile(): UserProfile = dataStore.data.first().toUserProfile()

    override suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { prefs ->
            prefs[Keys.NAME] = profile.name
            prefs[Keys.USERNAME] = profile.username
            prefs[Keys.BIO] = profile.bio

            // Avatar URI nullable — kalau null, REMOVE key (bukan set "")
            // supaya bedanya kentara antara "belum pernah set" vs "explicit clear".
            if (profile.avatarUri != null) {
                prefs[Keys.AVATAR_URI] = profile.avatarUri
            } else {
                prefs.remove(Keys.AVATAR_URI)
            }

            // memberSince: hanya set kalau belum pernah ada (first launch).
            // Setelah set, jangan timpa supaya angka "Bergabung sejak X" tidak
            // berubah saat user edit profile.
            if (!prefs.contains(Keys.MEMBER_SINCE)) {
                prefs[Keys.MEMBER_SINCE] = profile.memberSince.toEpochMilliseconds()
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // THEME
    // ════════════════════════════════════════════════════════════════════════

    override fun observeThemeMode(): Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.fromName(prefs[Keys.THEME_MODE])
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // RESET
    // ════════════════════════════════════════════════════════════════════════

    override suspend fun resetPreferences() {
        dataStore.edit { it.clear() }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Mapper: Preferences → UserProfile dengan default values.
     * Kalau key tidak ada (belum pernah set), pakai default dari companion.
     */
    private fun Preferences.toUserProfile(): UserProfile = UserProfile(
        name = this[Keys.NAME] ?: UserProfile.DEFAULT_NAME,
        username = this[Keys.USERNAME] ?: UserProfile.DEFAULT_USERNAME,
        bio = this[Keys.BIO] ?: "",
        avatarUri = this[Keys.AVATAR_URI],  // null = belum set
        memberSince = this[Keys.MEMBER_SINCE]
            ?.let { Instant.fromEpochMilliseconds(it) }
            ?: Clock.System.now(),  // first time → set now
    )
}