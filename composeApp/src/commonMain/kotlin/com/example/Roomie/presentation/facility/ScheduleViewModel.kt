package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.usecase.CancelBookingUseCase
import com.example.Roomie.domain.usecase.GetAllBookingsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ScheduleUiState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ScheduleViewModel(
    private val getAllBookingsUseCase: GetAllBookingsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        getAllBookingsUseCase()
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { list -> 
                _uiState.update { it.copy(bookings = list.reversed(), isLoading = false) }
            }
            .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun cancelBooking(id: String) {
        viewModelScope.launch {
            cancelBookingUseCase(id)
        }
    }
}
