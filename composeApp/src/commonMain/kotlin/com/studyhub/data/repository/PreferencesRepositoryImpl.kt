package com.studyhub.data.repository

import com.studyhub.data.local.PreferencesDataSource
import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

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
}
