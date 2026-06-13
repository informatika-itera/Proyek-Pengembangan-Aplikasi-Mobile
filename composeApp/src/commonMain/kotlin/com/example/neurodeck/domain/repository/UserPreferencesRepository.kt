package com.example.neurodeck.domain.repository

import com.example.neurodeck.domain.model.ReminderSettings
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak untuk persist & observe user preferences (profile + settings).
 *
 * Backing store: DataStore Preferences (key-value, local file).
 * Implementasi: UserPreferencesRepositoryImpl di data layer.
 *
 * Dipakai oleh:
 *   - ProfileViewModel       (P3e) — show & edit user profile
 *   - EditProfileViewModel   (P3e) — save form changes
 *   - AppNavHost / Theme     (P3e bonus) — observe theme mode reactively
 *   - HomeViewModel (future) — show user name di greeting card
 */
interface UserPreferencesRepository {

    /**
     * Stream user profile. Re-emit setiap kali ada update.
     * Initial emit: default UserProfile() kalau belum pernah di-set.
     */
    fun observeProfile(): Flow<UserProfile>

    /**
     * Snapshot one-shot (untuk operasi non-reactive).
     * Mostly digunakan oleh EditProfileViewModel saat init form.
     */
    suspend fun getProfile(): UserProfile

    /** Save full profile (replace existing). */
    suspend fun saveProfile(profile: UserProfile)

    /** Stream theme mode untuk reactive theme switching. */
    fun observeThemeMode(): Flow<ThemeMode>

    /** Save theme mode (Light/Dark/System). */
    suspend fun setThemeMode(mode: ThemeMode)

    /** Stream pengaturan reminder belajar harian (enabled + jam + menit). */
    fun observeReminderSettings(): Flow<ReminderSettings>

    /** Save pengaturan reminder belajar harian. */
    suspend fun setReminderSettings(settings: ReminderSettings)

    /**
     * Reset all preferences ke default state. Dipakai di Profile tab
     * "Reset All Data" action (double-confirm).
     *
     * NOTE: Hanya clear preferences DataStore — TIDAK touch SQL database.
     * Database reset terpisah (akan di-handle di ResetAllDataUseCase di future
     * yang panggil ini + DeckRepository.deleteAll() + dll).
     */
    suspend fun resetPreferences()
}