package com.studyhub.domain.fake

import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePreferencesRepository : PreferencesRepository {
    private val _isDarkMode = MutableStateFlow(false)
    override val isDarkMode: Flow<Boolean> = _isDarkMode

    var isDarkModeValue: Boolean
        get() = _isDarkMode.value
        set(value) { _isDarkMode.value = value }

    private val _userName = MutableStateFlow("Pelajar")
    override val userName: Flow<String> = _userName
    
    var userNameValue: Boolean = false // This was in the previous version but seems unused or wrong type
    // Let's fix it
    var userNameString: String
        get() = _userName.value
        set(value) { _userName.value = value }

    private val _userPreferences = MutableStateFlow(UserPreferences())
    override val userPreferences: Flow<UserPreferences> = _userPreferences

    override val notificationEnabled: Flow<Boolean> = MutableStateFlow(true)
    override val isAiReminderEnabled: Flow<Boolean> = MutableStateFlow(true)

    var setDarkModeCalledWith: Boolean? = null

    override suspend fun setDarkMode(enabled: Boolean) {
        setDarkModeCalledWith = enabled
        _isDarkMode.value = enabled
        _userPreferences.update { it.copy(isDarkMode = enabled) }
    }

    override suspend fun setUserName(name: String) {
        _userName.value = name
        _userPreferences.update { it.copy(userName = name) }
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        _userPreferences.update { it.copy(notificationEnabled = enabled) }
    }

    override suspend fun setAiReminderEnabled(enabled: Boolean) {
        _userPreferences.update { it.copy(isAiReminderEnabled = enabled) }
    }

    override suspend fun setPomodoroSettings(
        focus: Int, shortBreak: Int, longBreak: Int
    ) {
        _userPreferences.update { 
            it.copy(
                pomodoroFocusDuration = focus,
                pomodoroShortBreak = shortBreak,
                pomodoroLongBreak = longBreak
            )
        }
    }
}
