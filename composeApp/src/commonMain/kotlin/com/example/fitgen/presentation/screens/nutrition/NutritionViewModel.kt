package com.example.fitgen.presentation.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.model.MealLog
import com.example.fitgen.domain.model.MealType
import com.example.fitgen.domain.usecase.GetDailyCaloriesUseCase
import com.example.fitgen.domain.usecase.LogMealUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class NutritionUiState(
    val isLoading: Boolean = true,
    val todayMeals: List<MealLog> = emptyList(),
    val totalCaloriesToday: Int = 0,
    val calorieTarget: Int = 2000,
    val errorMessage: String? = null
) {
    val calorieProgress: Float
        get() = if (calorieTarget > 0) totalCaloriesToday.toFloat() / calorieTarget else 0f
    val remainingCalories: Int
        get() = maxOf(0, calorieTarget - totalCaloriesToday)
}

class NutritionViewModel(
    private val getDailyCaloriesUseCase: GetDailyCaloriesUseCase,
    private val logMealUseCase: LogMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    init { observeTodayMeals() }

    private fun observeTodayMeals() {
        getDailyCaloriesUseCase()
            .onEach { meals ->
                _uiState.update {
                    it.copy(
                        isLoading          = false,
                        todayMeals         = meals,
                        totalCaloriesToday = getDailyCaloriesUseCase.calculateTotal(meals),
                        errorMessage       = null
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun deleteMeal(mealId: Long) {
        _uiState.update { state ->
            state.copy(todayMeals = state.todayMeals.filter { it.id != mealId })
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    fun groupByMealType(): Map<MealType, List<MealLog>> =
        _uiState.value.todayMeals.groupBy { it.jenisMakan }
}