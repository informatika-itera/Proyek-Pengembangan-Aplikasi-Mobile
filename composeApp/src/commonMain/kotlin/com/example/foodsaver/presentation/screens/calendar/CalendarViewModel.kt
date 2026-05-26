package com.example.foodsaver.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CalendarUiState(
    val upcomingItems: List<FoodItem> = emptyList(),
    val pastItems: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val getAllFoodUseCase: GetAllFoodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllFoodUseCase().collect { items ->
                // Filter only active items (not consumed, not discarded)
                val activeItems = items.filter { !it.isConsumed && !it.isDiscarded }
                
                val upcoming = activeItems.filter { 
                    it.getStatus() == FoodStatus.SAFE || it.getStatus() == FoodStatus.NEAR_EXPIRY 
                }.sortedBy { it.expiryDate }
                
                val past = activeItems.filter { 
                    it.getStatus() == FoodStatus.EXPIRED || it.getStatus() == FoodStatus.EXPIRED_TODAY 
                }.sortedByDescending { it.expiryDate }

                _state.update { it.copy(
                    upcomingItems = upcoming, 
                    pastItems = past,
                    isLoading = false
                ) }
            }
        }
    }
}
