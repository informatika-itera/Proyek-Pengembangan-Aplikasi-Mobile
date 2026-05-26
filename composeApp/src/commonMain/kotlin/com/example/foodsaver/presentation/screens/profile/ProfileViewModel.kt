package com.example.foodsaver.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val totalItems: Int = 0,
    val safeCount: Int = 0,
    val nearlyExpiredCount: Int = 0,
    val expiredCount: Int = 0,
    val consumedCount: Int = 0,
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val getAllFoodUseCase: GetAllFoodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllFoodUseCase().collect { items ->
                val activeItems = items.filter { !it.isConsumed }
                _state.update { it.copy(
                    totalItems = activeItems.size,
                    safeCount = activeItems.count { it.getStatus() == FoodStatus.SAFE },
                    nearlyExpiredCount = activeItems.count { it.getStatus() == FoodStatus.NEAR_EXPIRY },
                    expiredCount = activeItems.count { it.getStatus() == FoodStatus.EXPIRED },
                    consumedCount = items.count { it.isConsumed },
                    isLoading = false
                ) }
            }
        }
    }
}
