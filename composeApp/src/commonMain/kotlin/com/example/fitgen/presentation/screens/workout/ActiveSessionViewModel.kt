package com.example.fitgen.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.model.Exercise
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ActiveSessionViewModel(
    private val workoutRepository: WorkoutRepository,
    private val userPreferences: com.example.fitgen.data.local.datastore.UserPreferences,
    private val apiService: com.example.fitgen.data.remote.api.ExerciseApiService
) : ViewModel() {

    private val DEFAULT_DURATION = 60L
    private val _remainingSeconds = MutableStateFlow(DEFAULT_DURATION)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _exercisesList = MutableStateFlow<List<String>>(emptyList())
    val exercisesList: StateFlow<List<String>> = _exercisesList.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _currentExerciseName = MutableStateFlow("")
    val currentExerciseName: StateFlow<String> = _currentExerciseName.asStateFlow()
    
    private val _isLastExercise = MutableStateFlow(true)
    val isLastExercise: StateFlow<Boolean> = _isLastExercise.asStateFlow()

    private var timerJob: Job? = null
    private var isInitialized = false
    
    // Track total time for the whole session
    private var totalSessionSeconds = 0L

    fun initialize(exerciseName: String, challengeId: String?, challengeDay: Int?) {
        if (isInitialized) return
        isInitialized = true

        if (challengeId != null && challengeDay != null) {
            viewModelScope.launch {
                val result = apiService.getChallengeProgram(challengeId)
                if (result is com.example.fitgen.core.network.NetworkResult.Success) {
                    val challenge = result.data
                    val dayData = challenge.days.find { it.dayNumber == challengeDay }
                    
                    if (dayData != null && dayData.exercises.isNotEmpty()) {
                        val names = dayData.exercises.map { it.name ?: "Unknown" }
                        _exercisesList.value = names
                        _currentExerciseName.value = names[0]
                        _isLastExercise.value = names.size <= 1
                    } else {
                        // Fallback
                        _exercisesList.value = listOf(exerciseName)
                        _currentExerciseName.value = exerciseName
                        _isLastExercise.value = true
                    }
                } else {
                    _exercisesList.value = listOf(exerciseName)
                    _currentExerciseName.value = exerciseName
                    _isLastExercise.value = true
                }
            }
        } else {
            _exercisesList.value = listOf(exerciseName)
            _currentExerciseName.value = exerciseName
            _isLastExercise.value = true
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isPlaying.value = true
        timerJob = viewModelScope.launch {
            while (_isPlaying.value && _remainingSeconds.value > 0) {
                delay(1000L)
                _remainingSeconds.value -= 1
                totalSessionSeconds += 1
            }
            if (_remainingSeconds.value <= 0) {
                _isPlaying.value = false
            }
        }
    }

    private fun pauseTimer() {
        _isPlaying.value = false
        timerJob?.cancel()
    }
    
    fun skipToNext() {
        if (_currentIndex.value < _exercisesList.value.size - 1) {
            pauseTimer()
            _currentIndex.value += 1
            _currentExerciseName.value = _exercisesList.value[_currentIndex.value]
            _remainingSeconds.value = DEFAULT_DURATION
            _isLastExercise.value = _currentIndex.value == _exercisesList.value.size - 1
            startTimer()
        }
    }

    fun finishSession(challengeId: String? = null, challengeDay: Int? = null) {
        pauseTimer()
        viewModelScope.launch {
            val minutes = totalSessionSeconds / 60
            val seconds = totalSessionSeconds % 60
            val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

            // Save all exercises done
            for (i in 0.._currentIndex.value) {
                val exercise = Exercise(
                    nama = _exercisesList.value.getOrNull(i) ?: "Unknown",
                    sets = 1,
                    reps = 1,
                    beban = 0.0
                )
                val catatan = if (i == 0) "Sesi Selesai - Total Durasi: $formattedTime" else ""
                workoutRepository.addExerciseToTodayLog(exercise, catatan)
            }
            
            if (challengeId != null && challengeDay != null) {
                userPreferences.markChallengeDayCompleted(challengeId, challengeDay)
            }
            
            _isFinished.value = true
        }
    }
}
