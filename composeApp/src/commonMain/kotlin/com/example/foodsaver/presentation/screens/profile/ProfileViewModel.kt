package com.example.foodsaver.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.data.local.datastore.ThemeMode
import com.example.foodsaver.data.local.datastore.UserPreferences
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val totalItems: Int = 0,
    val safeCount: Int = 0,
    val nearlyExpiredCount: Int = 0,
    val expiredCount: Int = 0,
    val consumedCount: Int = 0,
    val discardedCount: Int = 0,
    val isLoading: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val reminderDays: Int = 1
)

class ProfileViewModel(
    private val getAllFoodUseCase: GetAllFoodUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadStats()
        observePreferences()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllFoodUseCase().collect { items ->
                val activeItems = items.filter { !it.isConsumed && !it.isDiscarded }
                
                _state.update { it.copy(
                    totalItems = activeItems.size,
                    safeCount = activeItems.count { it.getStatus() == FoodStatus.SAFE },
                    nearlyExpiredCount = activeItems.count { it.getStatus() == FoodStatus.NEAR_EXPIRY },
                    expiredCount = activeItems.count { it.getStatus() == FoodStatus.EXPIRED || it.getStatus() == FoodStatus.EXPIRED_TODAY },
                    consumedCount = items.count { it.isConsumed },
                    discardedCount = items.count { it.isDiscarded },
                    isLoading = false
                ) }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                userPreferences.themeMode,
                userPreferences.notificationsEnabled,
                userPreferences.reminderDays
            ) { theme, notify, days ->
                Triple(theme, notify, days)
            }.collect { (theme, notify, days) ->
                _state.update { it.copy(
                    themeMode = theme,
                    notificationsEnabled = notify,
                    reminderDays = days
                ) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }

    fun setReminderDays(days: Int) {
        viewModelScope.launch {
            userPreferences.setReminderDays(days)
        }
    }
}
