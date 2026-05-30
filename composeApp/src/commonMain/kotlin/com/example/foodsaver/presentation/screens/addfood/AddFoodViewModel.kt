package com.example.foodsaver.presentation.screens.addfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.usecase.GetFoodDetailUseCase
import com.example.foodsaver.domain.usecase.SaveFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

data class AddFoodUiState(
    val id: Long = 0,
    val name: String = "",
    val quantity: String = "1",
    val unit: String = FoodItem.UNITS[0],
    val category: String = FoodItem.CATEGORIES[0],
    val storageLocation: String = FoodItem.STORAGE_LOCATIONS[0],
    val notes: String = "",
    val expiryDate: Instant = Clock.System.now().plus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
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
                        storageLocation = food.storageLocation,
                        notes = food.notes ?: "",
                        expiryDate = food.expiryDate,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Aduh, datanya nggak ketemu nih.") }
            }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name, error = null) }
    fun onQuantityChange(q: String) = _state.update { it.copy(quantity = q, error = null) }
    fun onUnitChange(unit: String) = _state.update { it.copy(unit = unit) }
    
    fun onCategoryChange(cat: String) {
        val durationDays = when (cat) {
            "Sayur" -> 5
            "Buah" -> 7
            "Daging" -> 3
            "Susu & Telur" -> 7
            "Minuman" -> 30
            "Camilan" -> 90
            "Bumbu" -> 180
            else -> 7
        }
        val newExpiry = Clock.System.now().plus(durationDays, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        _state.update { it.copy(category = cat, expiryDate = newExpiry) }
    }
    
    fun onStorageLocationChange(loc: String) = _state.update { it.copy(storageLocation = loc) }
    fun onNotesChange(notes: String) = _state.update { it.copy(notes = notes) }
    fun onDateChange(date: Instant) = _state.update { it.copy(expiryDate = date) }

    fun saveFood() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "Jangan lupa isi nama makanannya ya.") }
            return
        }
        
        val qty = currentState.quantity.toDoubleOrNull() ?: 0.0
        if (qty <= 0) {
            _state.update { it.copy(error = "Jumlahnya harus lebih dari 0 ya.") }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val food = FoodItem(
                    id = currentState.id,
                    name = currentState.name,
                    quantity = qty,
                    unit = currentState.unit,
                    category = currentState.category,
                    storageLocation = currentState.storageLocation,
                    notes = currentState.notes.ifBlank { null },
                    buyDate = Clock.System.now(),
                    expiryDate = currentState.expiryDate
                )
                saveFoodUseCase(food)
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Maaf, gagal simpan data nih: ${e.message}") }
            }
        }
    }
}
