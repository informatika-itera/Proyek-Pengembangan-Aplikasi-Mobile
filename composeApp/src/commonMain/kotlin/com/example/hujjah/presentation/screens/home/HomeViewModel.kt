package com.example.hujjah.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hujjah.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class HomeViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private var timerJob: Job? = null
    val isTimerRunning = MutableStateFlow(false)

    // Target harian membaca dalam detik (contoh: 15 menit = 900 detik)
    val dailyTargetSeconds = 900

    val readingDurationSeconds: StateFlow<Int> = userPreferences.readingDurationSeconds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val currentStreakDays: StateFlow<Int> = userPreferences.currentStreakDays
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val lastReadLocation: StateFlow<String> = userPreferences.lastReadQuranLocation
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val userName: StateFlow<String> = userPreferences.userName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Awi"
        )

    val profileImageBase64: StateFlow<String> = userPreferences.profileImageBase64
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    // Kutipan hari ini secara default. Nantinya, ini bisa dicocokkan dinamis berdasarkan input Lens
    val quoteOfTheDay: StateFlow<QuoteData> = lastReadLocation.map { _ ->
        // Default Quote
        QuoteData(
            arabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            translation = "Ingatlah, hanya dengan mengingati Allah-lah hati menjadi tenteram.",
            reference = "QS. Ar-Ra'd: 28"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuoteData(
            arabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            translation = "Ingatlah, hanya dengan mengingati Allah-lah hati menjadi tenteram.",
            reference = "QS. Ar-Ra'd: 28"
        )
    )

    fun toggleTimer() {
        if (isTimerRunning.value) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return
        isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            // Update streak on first start of the day
            userPreferences.updateStreak()
            while (true) {
                delay(1000) // 1 second
                userPreferences.addReadingDuration(1)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        isTimerRunning.value = false
    }

    fun resetReadingTime() {
        viewModelScope.launch {
            userPreferences.resetReadingDuration()
        }
        stopTimer()
    }
}

data class QuoteData(
    val arabic: String,
    val translation: String,
    val reference: String
)
