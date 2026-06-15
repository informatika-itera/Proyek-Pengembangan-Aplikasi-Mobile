package com.studymate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    val isDarkTheme = preferenceRepository.isDarkTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun toggleTheme() {
        viewModelScope.launch {
            preferenceRepository.setDarkTheme(!isDarkTheme.value)
        }
    }
}
