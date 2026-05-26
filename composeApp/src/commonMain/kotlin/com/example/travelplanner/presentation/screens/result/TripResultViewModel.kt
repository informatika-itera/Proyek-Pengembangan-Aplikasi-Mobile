package com.example.travelplanner.presentation.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TripResultUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val totalExpenses: Double = 0.0,
    val errorMessage: String? = null
)

class TripResultViewModel(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripResultUiState>(TripResultUiState())
    val uiState: StateFlow<TripResultUiState> = _uiState.asStateFlow()

    fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val tripFlow = tripRepository.getTripById(tripId)
            val expensesFlow = expenseRepository.getTotalExpensesForTrip(tripId)

            combine(tripFlow, expensesFlow) { trip, total ->
                TripResultUiState(
                    isLoading = false,
                    trip = trip,
                    totalExpenses = total,
                    errorMessage = if (trip == null) "Perjalanan tidak ditemukan." else null
                )
            }.catch { e ->
                _uiState.value = TripResultUiState(
                    isLoading = false,
                    errorMessage = "Gagal memuat rincian: ${e.message}"
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
