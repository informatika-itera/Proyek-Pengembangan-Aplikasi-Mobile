package com.example.foodsaver.presentation.screens.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.MealPlan
import com.example.foodsaver.domain.usecase.GetMealPlansForDateUseCase
import com.example.foodsaver.domain.usecase.RemoveMealPlanUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class MealPlannerUiState(
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val mealPlans: List<MealPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MealPlannerViewModel(
    private val getMealPlansForDateUseCase: GetMealPlansForDateUseCase,
    private val removeMealPlanUseCase: RemoveMealPlanUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MealPlannerUiState())
    val state: StateFlow<MealPlannerUiState> = _state.asStateFlow()

    init {
        observeMealPlans()
    }

    private fun observeMealPlans() {
        _state.map { it.selectedDate }
            .distinctUntilChanged()
            .onEach { date ->
                _state.update { it.copy(isLoading = true) }
                getMealPlansForDateUseCase(date)
                    .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                    .collect { plans ->
                        _state.update { it.copy(isLoading = false, mealPlans = plans, error = null) }
                    }
            }.launchIn(viewModelScope)
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }

    fun removeMealPlan(id: Long) {
        viewModelScope.launch {
            try {
                removeMealPlanUseCase(id)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
