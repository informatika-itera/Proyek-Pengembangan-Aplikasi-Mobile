package com.studyhub.data.repository

import com.studyhub.data.local.PreferencesDataSource
import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*

class PreferencesRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : PreferencesRepository {

    override val isDarkMode: Flow<Boolean> =
        dataSource.isDarkMode

    override val userName: Flow<String> =
        dataSource.userName

    override val userPreferences: Flow<UserPreferences> =
        dataSource.userPreferences

    override val notificationEnabled: Flow<Boolean> =
        dataSource.notificationEnabled

    override val isAiReminderEnabled: Flow<Boolean> =
        dataSource.isAiReminderEnabled

    override suspend fun setDarkMode(enabled: Boolean) =
        dataSource.setDarkMode(enabled)

    override suspend fun setUserName(name: String) =
        dataSource.setUserName(name)

    override suspend fun setNotificationEnabled(enabled: Boolean) =
        dataSource.setNotificationEnabled(enabled)

    override suspend fun setAiReminderEnabled(enabled: Boolean) =
        dataSource.setAiReminderEnabled(enabled)

    override suspend fun setPomodoroSettings(
        focus: Int, shortBreak: Int, longBreak: Int
    ) = dataSource.setPomodoroSettings(focus, shortBreak, longBreak)

    override suspend fun updateStreak(): Int? {
        val prefs = dataSource.userPreferences.first()
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        if (prefs.lastUsageTimestamp == 0L) {
            dataSource.updateStreak(1, 1, now.toEpochMilliseconds())
            return 1
        }

        val lastUsageDate = Instant.fromEpochMilliseconds(prefs.lastUsageTimestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

        if (lastUsageDate == today) return null // Sudah update hari ini

        val yesterday = today.minus(1, DateTimeUnit.DAY)
        
        return if (lastUsageDate == yesterday) {
            val newStreak = prefs.currentStreak + 1
            val newLongest = if (newStreak > prefs.longestStreak) newStreak else prefs.longestStreak
            dataSource.updateStreak(newStreak, newLongest, now.toEpochMilliseconds())
            newStreak
        } else {
            // Streak putus
            dataSource.updateStreak(1, prefs.longestStreak, now.toEpochMilliseconds())
            1
        }
    }
}
