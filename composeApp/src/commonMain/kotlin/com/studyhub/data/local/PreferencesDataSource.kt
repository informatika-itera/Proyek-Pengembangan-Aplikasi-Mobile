package com.studyhub.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.studyhub.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val USER_NAME = stringPreferencesKey("user_name")
    val DEFAULT_VIEW = stringPreferencesKey("default_view")
    val DEFAULT_SORT_BY = stringPreferencesKey("default_sort_by")
    val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    val IS_AI_REMINDER = booleanPreferencesKey("is_ai_reminder")
    val POMODORO_FOCUS = intPreferencesKey("pomodoro_focus")
    val POMODORO_SHORT_BREAK = intPreferencesKey("pomodoro_short_break")
    val POMODORO_LONG_BREAK = intPreferencesKey("pomodoro_long_break")
    val POMODORO_SESSIONS_BEFORE_LONG = intPreferencesKey("pomodoro_sessions_before_long")
    val CURRENT_STREAK = intPreferencesKey("current_streak")
    val LONGEST_STREAK = intPreferencesKey("longest_streak")
    val LAST_USAGE_TIMESTAMP = longPreferencesKey("last_usage_timestamp")
    val NOTIF_AUTO_DELETE_ENABLED = booleanPreferencesKey("notif_auto_delete_enabled")
    val NOTIF_MAX_HISTORY_COUNT = intPreferencesKey("notif_max_history_count")
    val LAST_NOTIF_CLEANUP = longPreferencesKey("last_notif_cleanup")
}

class PreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {
    // ── Dark Mode ──
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.IS_DARK_MODE] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_DARK_MODE] = enabled
        }
    }

    // ── User Name ──
    val userName: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.USER_NAME] ?: "Pelajar"
        }

    suspend fun setUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_NAME] = name
        }
    }

    // ── Notification ──
    val notificationEnabled: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_ENABLED] ?: true
        }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    // ── AI Reminder ──
    val isAiReminderEnabled: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.IS_AI_REMINDER] ?: true
        }

    suspend fun setAiReminderEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_AI_REMINDER] = enabled
        }
    }

    // ── Pomodoro ──
    val pomodoroFocus: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_FOCUS] ?: 25
        }

    val pomodoroShortBreak: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_SHORT_BREAK] ?: 5
        }

    val pomodoroLongBreak: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_LONG_BREAK] ?: 15
        }

    suspend fun setPomodoroSettings(
        focus: Int, shortBreak: Int, longBreak: Int
    ) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.POMODORO_FOCUS] = focus
            prefs[PreferencesKeys.POMODORO_SHORT_BREAK] = shortBreak
            prefs[PreferencesKeys.POMODORO_LONG_BREAK] = longBreak
        }
    }

    suspend fun updateStreak(streak: Int, longest: Int, timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.CURRENT_STREAK] = streak
            prefs[PreferencesKeys.LONGEST_STREAK] = longest
            prefs[PreferencesKeys.LAST_USAGE_TIMESTAMP] = timestamp
        }
    }

    // ── Notification History Cleanup ──
    val notifAutoDeleteEnabled: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.NOTIF_AUTO_DELETE_ENABLED] ?: true
        }

    val notifMaxHistoryCount: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[PreferencesKeys.NOTIF_MAX_HISTORY_COUNT] ?: 50
        }

    suspend fun getLastNotifCleanup(): Long {
        return dataStore.data.first()[PreferencesKeys.LAST_NOTIF_CLEANUP] ?: 0L
    }

    suspend fun setLastNotifCleanup(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.LAST_NOTIF_CLEANUP] = timestamp
        }
    }

    // ── Get all as UserPreferences snapshot ──
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            UserPreferences(
                userName = prefs[PreferencesKeys.USER_NAME] ?: "Pelajar",
                isDarkMode = prefs[PreferencesKeys.IS_DARK_MODE] ?: false,
                notificationEnabled = prefs[PreferencesKeys.NOTIFICATION_ENABLED] ?: true,
                isAiReminderEnabled = prefs[PreferencesKeys.IS_AI_REMINDER] ?: true,
                pomodoroFocusDuration = prefs[PreferencesKeys.POMODORO_FOCUS] ?: 25,
                pomodoroShortBreak = prefs[PreferencesKeys.POMODORO_SHORT_BREAK] ?: 5,
                pomodoroLongBreak = prefs[PreferencesKeys.POMODORO_LONG_BREAK] ?: 15,
                pomodoroSessionsBeforeLong = prefs[PreferencesKeys.POMODORO_SESSIONS_BEFORE_LONG] ?: 4,
                currentStreak = prefs[PreferencesKeys.CURRENT_STREAK] ?: 0,
                longestStreak = prefs[PreferencesKeys.LONGEST_STREAK] ?: 0,
                lastUsageTimestamp = prefs[PreferencesKeys.LAST_USAGE_TIMESTAMP] ?: 0
            )
        }
}
