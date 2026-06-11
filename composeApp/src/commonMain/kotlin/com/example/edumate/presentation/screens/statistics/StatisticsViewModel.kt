package com.example.edumate.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class StatisticsUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completionRate: Float = 0f,
    val mostProductiveDay: String = "-",
    val mostProductiveHour: String = "-",
    val isLoading: Boolean = true
)

class StatisticsViewModel(repository: TaskRepository) : ViewModel() {
    val uiState: StateFlow<StatisticsUiState> = repository.getAllTasks()
        .map { tasks ->
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed
            val rate = if (total > 0) completed.toFloat() / total.toFloat() else 0f

            val completedTasksList = tasks.filter { it.isCompleted }
            var bestDay = "-"
            var bestHour = "-"

            if (completedTasksList.isNotEmpty()) {
                val dayCounts = mutableMapOf<String, Int>()
                val hourCounts = mutableMapOf<Int, Int>()

                completedTasksList.forEach { task ->
                    val localTime = task.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                    val dayName = localTime.dayOfWeek.name
                    val hour = localTime.hour

                    dayCounts[dayName] = dayCounts.getOrElse(dayName) { 0 } + 1
                    hourCounts[hour] = hourCounts.getOrElse(hour) { 0 } + 1
                }

                val maxDay = dayCounts.maxByOrNull { it.value }?.key
                val maxHour = hourCounts.maxByOrNull { it.value }?.key

                bestDay = maxDay?.let { translateDay(it) } ?: "-"
                bestHour = maxHour?.let { "${it.toString().padStart(2, '0')}:00 WIB" } ?: "-"
            }

            StatisticsUiState(
                totalTasks = total,
                completedTasks = completed,
                pendingTasks = pending,
                completionRate = rate,
                mostProductiveDay = bestDay,
                mostProductiveHour = bestHour,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatisticsUiState(isLoading = true)
        )

    private fun translateDay(day: String): String {
        return when (day.uppercase()) {
            "MONDAY" -> "Senin"
            "TUESDAY" -> "Selasa"
            "WEDNESDAY" -> "Rabu"
            "THURSDAY" -> "Kamis"
            "FRIDAY" -> "Jumat"
            "SATURDAY" -> "Sabtu"
            "SUNDAY" -> "Minggu"
            else -> day
        }
    }
}