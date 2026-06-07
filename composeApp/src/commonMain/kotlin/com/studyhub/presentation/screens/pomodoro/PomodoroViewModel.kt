package com.studyhub.presentation.screens.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.manager.PomodoroManager
import com.studyhub.domain.model.PomodoroState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PomodoroViewModel(
    private val pomodoroManager: PomodoroManager
) : ViewModel() {

    val uiState: StateFlow<PomodoroState> = pomodoroManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PomodoroState()
        )

    fun start() = pomodoroManager.startTimer()
    fun pause() = pomodoroManager.pauseTimer()
    fun reset() = pomodoroManager.resetTimer()
    fun skipPhase() = pomodoroManager.skipPhase()
}
