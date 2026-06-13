package com.example.fitgen.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isSplashFinished = MutableStateFlow(false)
    val isSplashFinished: StateFlow<Boolean> = _isSplashFinished.asStateFlow()

    private val _navigateToRoute = MutableStateFlow<String?>(null)
    val navigateToRoute: StateFlow<String?> = _navigateToRoute.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Tunda 2 detik untuk efek splash screen
            
            val isOnboarded = userPreferences.isOnboardingCompleted.first()
            if (isOnboarded) {
                _navigateToRoute.value = "home"
            } else {
                _navigateToRoute.value = "onboarding"
            }
            _isSplashFinished.value = true
        }
    }
}
