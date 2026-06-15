package com.studyhub.core.manager

import com.studyhub.core.notification.NotificationManager
import com.studyhub.domain.model.*
import com.studyhub.domain.repository.NotifHistoryRepository
import com.studyhub.domain.repository.PomodoroRepository
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.task.GetTaskByIdUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PomodoroManager(
    private val pomodoroRepository: PomodoroRepository,
    private val notifHistoryRepository: NotifHistoryRepository,
    private val notificationManager: NotificationManager,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val externalScope: CoroutineScope? = null
) {
    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private val scope = externalScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null
    private var transitionJob: Job? = null
    
    private var settings = PomodoroSettings()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        scope.launch {
            getUserPreferencesUseCase().collectLatest { prefs ->
                settings = PomodoroSettings(
                    focusDurationMinutes = prefs.pomodoroFocusDuration,
                    shortBreakMinutes = prefs.pomodoroShortBreak,
                    longBreakMinutes = prefs.pomodoroLongBreak,
                    sessionsBeforeLongBreak = prefs.pomodoroSessionsBeforeLong
                )
                if (!_state.value.isRunning && !isTransitional()) {
                    resetTimer()
                }
            }
        }
    }

    fun startTimer() {
        if (_state.value.isRunning || isTransitional()) return
        _state.update { it.copy(isRunning = true) }
        
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive && _state.value.isRunning && _state.value.timeRemainingSeconds > 0) {
                delay(1000)
                _state.update { it.copy(timeRemainingSeconds = it.timeRemainingSeconds - 1) }
                
                // Show ongoing notification every 30s
                if (_state.value.timeRemainingSeconds % 30 == 0) {
                    showOngoingNotification()
                }
            }
            if (_state.value.timeRemainingSeconds == 0) {
                handleTimerFinished()
            }
        }
    }

    fun pauseTimer() {
        _state.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        notificationManager.cancelNotification(2003) // POMODORO_ONGOING
    }

    fun resetTimer() {
        pauseTimer()
        transitionJob?.cancel()
        val duration = when (_state.value.phase) {
            PomodoroPhase.FOCUS -> settings.focusDurationMinutes * 60
            PomodoroPhase.SHORT_BREAK -> settings.shortBreakMinutes * 60
            PomodoroPhase.LONG_BREAK -> settings.longBreakMinutes * 60
        }
        _state.update { it.copy(timeRemainingSeconds = duration, totalSeconds = duration) }
    }

    fun skipPhase() {
        timerJob?.cancel()
        transitionJob?.cancel()
        scope.launch {
            handleTimerFinished()
        }
    }

    fun linkTask(taskId: String?) {
        scope.launch {
            if (taskId == null) {
                _state.update { it.copy(linkedTaskId = null, linkedTaskTitle = null) }
            } else {
                getTaskByIdUseCase(taskId).firstOrNull()?.let { task ->
                    _state.update { it.copy(linkedTaskId = taskId, linkedTaskTitle = task.title) }
                }
            }
        }
    }

    private suspend fun handleTimerFinished() {
        val currentState = _state.value
        
        // Save session
        try {
            pomodoroRepository.saveSession(
                taskId = currentState.linkedTaskId,
                taskTitle = currentState.linkedTaskTitle,
                durationMinutes = when (currentState.phase) {
                    PomodoroPhase.FOCUS -> settings.focusDurationMinutes
                    PomodoroPhase.SHORT_BREAK -> settings.shortBreakMinutes
                    PomodoroPhase.LONG_BREAK -> settings.longBreakMinutes
                },
                phase = currentState.phase,
                wasCompleted = true
            )
        } catch (e: Exception) { }

        // Notify and Start Transition
        when (currentState.phase) {
            PomodoroPhase.FOCUS -> {
                notificationManager.showPomodoroNotification(
                    2001, "Sesi Fokus Selesai", "Waktunya istirahat!", "FOCUS", 0
                )
                saveToHistory("Sesi Fokus Selesai", "Ambil istirahat sejenak")
                startTransition(true)
            }
            else -> {
                notificationManager.showPomodoroNotification(
                    2002, "Istirahat Selesai", "Siap untuk fokus kembali?", "BREAK", 0
                )
                saveToHistory("Istirahat Selesai", "Waktunya kembali produktif")
                startTransition(false)
            }
        }
    }

    private fun startTransition(toBreak: Boolean) {
        transitionJob?.cancel()
        transitionJob = scope.launch {
            // Logic for 10s delay
            delay(10000)
            
            advancePhase(toBreak)
        }
    }

    private fun advancePhase(toBreak: Boolean) {
        val currentState = _state.value
        if (toBreak) {
            val isLongBreak = currentState.currentSession % settings.sessionsBeforeLongBreak == 0
            val nextPhase = if (isLongBreak) PomodoroPhase.LONG_BREAK else PomodoroPhase.SHORT_BREAK
            val duration = (if (isLongBreak) settings.longBreakMinutes else settings.shortBreakMinutes) * 60
            
            _state.update { it.copy(
                phase = nextPhase,
                timeRemainingSeconds = duration,
                totalSeconds = duration,
                isRunning = false
            ) }
            
            if (settings.autoStartBreak) startTimer()
        } else {
            val nextSession = if (currentState.phase == PomodoroPhase.LONG_BREAK) 1 else currentState.currentSession + 1
            val duration = settings.focusDurationMinutes * 60
            
            _state.update { it.copy(
                phase = PomodoroPhase.FOCUS,
                currentSession = nextSession,
                timeRemainingSeconds = duration,
                totalSeconds = duration,
                isRunning = false
            ) }
            
            if (settings.autoStartFocus) startTimer()
        }
    }

    private fun showOngoingNotification() {
        val s = _state.value
        notificationManager.showPomodoroNotification(
            2003, "StudyHub Pomodoro", 
            "${s.phase.displayName} · ${s.timeRemainingSeconds / 60}:${(s.timeRemainingSeconds % 60).toString().padStart(2, '0')} tersisa",
            s.phase.name, s.timeRemainingSeconds, true
        )
    }

    private suspend fun saveToHistory(title: String, reason: String) {
        try {
            notifHistoryRepository.addToHistory(
                taskId = "pomodoro",
                taskTitle = title,
                taskSubject = "Pomodoro",
                aiReason = reason,
                type = NotifType.POMODORO
            )
        } catch (e: Exception) { }
    }

    private fun isTransitional() = transitionJob?.isActive == true
}
