package com.dailybliss.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailybliss.app.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val nickname: StateFlow<String> = userPreferences.nickname
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "User")

    val aiLanguageStyle: StateFlow<String> = userPreferences.aiLanguageStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Santai/Kasual")

    val colorTheme: StateFlow<String> = userPreferences.colorTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sage Green")

    fun setNickname(name: String) {
        viewModelScope.launch {
            userPreferences.setNickname(name)
        }
    }

    fun setAiLanguageStyle(style: String) {
        viewModelScope.launch {
            userPreferences.setAiLanguageStyle(style)
        }
    }

    fun setColorTheme(theme: String) {
        viewModelScope.launch {
            userPreferences.setColorTheme(theme)
        }
    }

    val languageStyles = listOf("Santai/Kasual", "Formal/Baku", "Puitis/Puitik", "Motivasi")
    
    val themes = listOf("Sage Green", "Ocean Blue", "Rose Pink", "Lavender", "Monochrome")
}
