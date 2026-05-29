package com.example.fitkos.presentation.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitkos.data.local.datastore.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        observeExerciseMinutes()
    }

    private fun observeExerciseMinutes() {
        viewModelScope.launch {
            userPreferences.exerciseMinutesToday.collect { minutes ->
                _uiState.update {
                    it.copy(totalMinutesToday = minutes)
                }
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return

        _uiState.update {
            it.copy(isRunning = true)
        }

        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay(10)
                _uiState.update {
                    it.copy(elapsedMillis = it.elapsedMillis + 10)
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null

        _uiState.update {
            it.copy(isRunning = false)
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null

        _uiState.update {
            it.copy(
                elapsedMillis = 0L,
                isRunning = false,
                message = null
            )
        }
    }

    fun saveSession() {
        val millis = _uiState.value.elapsedMillis

        if (millis <= 0L) {
            _uiState.update {
                it.copy(message = "Mulai stopwatch dulu sebelum menyimpan.")
            }
            return
        }

        val minutes = ((millis + 59_999) / 60_000).toInt().coerceAtLeast(1)

        viewModelScope.launch {
            userPreferences.addExerciseMinutesToday(minutes)

            _uiState.update {
                it.copy(
                    elapsedMillis = 0L,
                    isRunning = false,
                    message = "Berhasil menyimpan $minutes menit olahraga."
                )
            }

            timerJob?.cancel()
            timerJob = null
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(message = null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class ExerciseUiState(
    val elapsedMillis: Long = 0L,
    val totalMinutesToday: Int = 0,
    val dailyTargetMinutes: Int = 30,
    val isRunning: Boolean = false,
    val message: String? = null
) {
    val progress: Float
        get() = if (dailyTargetMinutes > 0) {
            (totalMinutesToday.toFloat() / dailyTargetMinutes.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val formattedTime: String
        get() {
            val totalSeconds = elapsedMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val milliseconds = elapsedMillis % 1000

            return "${minutes.toString().padStart(2, '0')}:" +
                    "${seconds.toString().padStart(2, '0')}." +
                    milliseconds.toString().padStart(3, '0')
        }
}