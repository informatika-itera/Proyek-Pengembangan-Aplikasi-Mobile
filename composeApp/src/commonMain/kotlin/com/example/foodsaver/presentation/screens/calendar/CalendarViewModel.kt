package com.example.foodsaver.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CalendarUiState(
    val items: List<FoodItem> = emptyList(),
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
                // Filter only unconsumed items and sort by expiry date
                val sortedItems = items.filter { !it.isConsumed }
                    .sortedBy { it.expiryDate }
                _state.update { it.copy(items = sortedItems, isLoading = false) }
            }
        }
    }
}
