package com.example.fitgen.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.usecase.GetAllWorkoutsUseCase
import com.example.fitgen.domain.usecase.GetDailyCaloriesUseCase
import com.example.fitgen.domain.usecase.UpdateLoginStreakUseCase
import com.example.fitgen.domain.usecase.AddWaterGlassUseCase
import com.example.fitgen.domain.usecase.GetDailyWaterGlassesUseCase
import com.example.fitgen.domain.usecase.GetLoginStreakUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeDashboardUiState(
    val isLoading: Boolean       = true,
    val totalCaloriesToday: Int  = 0,
    val calorieTarget: Int       = 2000,
    val activeStreakDays: Int    = 0,
    val waterGlassesToday: Int   = 0,
    val lastWorkout: WorkoutLog? = null,
    val errorMessage: String?    = null
) {
    val calorieProgress: Float
        get() = if (calorieTarget > 0) totalCaloriesToday.toFloat() / calorieTarget else 0f
}

class HomeDashboardViewModel(
    private val getDailyCaloriesUseCase: GetDailyCaloriesUseCase,
    private val getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    private val updateLoginStreakUseCase: UpdateLoginStreakUseCase,
    private val getDailyWaterGlassesUseCase: GetDailyWaterGlassesUseCase,
    private val getLoginStreakUseCase: GetLoginStreakUseCase,
    private val addWaterGlassUseCase: AddWaterGlassUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeDashboardUiState())
    val uiState: StateFlow<HomeDashboardUiState> = _uiState.asStateFlow()

    init { loadDashboard() }

    private fun loadDashboard() {
        // Trigger update streak saat dashboard dimuat
        viewModelScope.launch {
            updateLoginStreakUseCase()
        }

        combine(
            getDailyCaloriesUseCase(),
            getAllWorkoutsUseCase(),
            getDailyWaterGlassesUseCase(),
            getLoginStreakUseCase()
        ) { meals, workouts, water, streak ->
            val totalKalori = getDailyCaloriesUseCase.calculateTotal(meals)
            val lastWorkout = workouts.firstOrNull()

            _uiState.update {
                it.copy(
                    isLoading          = false,
                    totalCaloriesToday = totalKalori,
                    lastWorkout        = lastWorkout,
                    activeStreakDays   = streak,
                    waterGlassesToday  = water,
                    errorMessage       = null
                )
            }
        }
        .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        .launchIn(viewModelScope)
    }

    fun addWater() {
        viewModelScope.launch {
            addWaterGlassUseCase()
        }
    }
    
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    private fun calculateStreak(sortedDates: List<kotlinx.datetime.LocalDate>): Int {
        if (sortedDates.isEmpty()) return 0
        var streak = 1
        for (i in 0 until sortedDates.size - 1) {
            val diff = sortedDates[i].toEpochDays() - sortedDates[i + 1].toEpochDays()
            if (diff == 1) streak++ else break
        }
        return streak
    }
}