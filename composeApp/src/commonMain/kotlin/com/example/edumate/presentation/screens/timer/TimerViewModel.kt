package com.example.edumate.presentation.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val timeRemaining: Int = 25 * 60, // Default 25 menit
    val isRunning: Boolean = false,
    val isBreak: Boolean = false,
    val progress: Float = 1f
)

class TimerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()
    private var timerJob: Job? = null

    private val totalTime: Int
        get() = if (_uiState.value.isBreak) 5 * 60 else 25 * 60

    fun toggleTimer() {
        if (_uiState.value.isRunning) pauseTimer() else startTimer()
    }

    private fun startTimer() {
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0) {
                delay(1000)
                _uiState.update {
                    val newTime = it.timeRemaining - 1
                    it.copy(
                        timeRemaining = newTime,
                        progress = newTime.toFloat() / totalTime.toFloat()
                    )
                }
            }
            // Jika timer selesai, otomatis pause
            _uiState.update { it.copy(isRunning = false, progress = 0f) }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun setMode(isBreak: Boolean) {
        timerJob?.cancel()
        val newTime = if (isBreak) 5 * 60 else 25 * 60
        _uiState.update {
            it.copy(
                timeRemaining = newTime,
                isRunning = false,
                isBreak = isBreak,
                progress = 1f
            )
        }
    }
}