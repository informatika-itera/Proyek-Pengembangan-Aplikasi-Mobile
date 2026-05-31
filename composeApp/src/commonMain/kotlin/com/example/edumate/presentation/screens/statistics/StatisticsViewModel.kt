package com.example.edumate.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatisticsUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completionRate: Float = 0f,
    val isLoading: Boolean = true
)

class StatisticsViewModel(repository: TaskRepository) : ViewModel() {
    val uiState: StateFlow<StatisticsUiState> = repository.getAllTasks()
        .map { tasks ->
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed
            val rate = if (total > 0) completed.toFloat() / total.toFloat() else 0f

            StatisticsUiState(
                totalTasks = total,
                completedTasks = completed,
                pendingTasks = pending,
                completionRate = rate,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatisticsUiState(isLoading = true)
        )
}