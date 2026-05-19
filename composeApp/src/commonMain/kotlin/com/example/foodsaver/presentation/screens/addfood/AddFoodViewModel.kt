package com.example.foodsaver.presentation.screens.addfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.usecase.GetFoodDetailUseCase
import com.example.foodsaver.domain.usecase.SaveFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class AddFoodUiState(
    val id: Long = 0,
    val name: String = "",
    val quantity: String = "",
    val unit: String = "kg",
    val category: String = "Lainnya",
    val expiryDate: Instant = Clock.System.now(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class AddFoodViewModel(
    private val saveFoodUseCase: SaveFoodUseCase,
    private val getFoodDetailUseCase: GetFoodDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddFoodUiState())
    val state: StateFlow<AddFoodUiState> = _state.asStateFlow()

    fun loadFood(id: Long) {
        if (id <= 0) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val food = getFoodDetailUseCase(id)
            if (food != null) {
                _state.update { 
                    it.copy(
                        id = food.id,
                        name = food.name,
                        quantity = food.quantity.toString(),
                        unit = food.unit,
                        category = food.category,
                        expiryDate = food.expiryDate,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Item tidak ditemukan") }
            }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name) }
    fun onQuantityChange(q: String) = _state.update { it.copy(quantity = q) }
    fun onUnitChange(unit: String) = _state.update { it.copy(unit = unit) }
    fun onCategoryChange(cat: String) = _state.update { it.copy(category = cat) }
    fun onDateChange(date: Instant) = _state.update { it.copy(expiryDate = date) }

    fun saveFood() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "Nama tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val food = FoodItem(
                    id = currentState.id,
                    name = currentState.name,
                    quantity = currentState.quantity.toDoubleOrNull() ?: 0.0,
                    unit = currentState.unit,
                    category = currentState.category,
                    expiryDate = currentState.expiryDate
                )
                saveFoodUseCase(food)
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
