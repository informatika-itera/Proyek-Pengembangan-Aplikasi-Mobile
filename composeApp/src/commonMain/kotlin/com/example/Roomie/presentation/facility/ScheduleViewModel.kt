package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.usecase.GetAllBookingsUseCase
import com.example.Roomie.data.repository.BookingRepositoryImpl
import com.example.Roomie.domain.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface ScheduleUiState {
    data object Loading : ScheduleUiState
    data class Success(val bookings: List<Booking>) : ScheduleUiState
    data class Error(val message: String) : ScheduleUiState
}

class ScheduleViewModel(
    private val getAllBookingsUseCase: GetAllBookingsUseCase,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        // Seed initial data if needed
        (bookingRepository as? BookingRepositoryImpl)?.let {
            viewModelScope.launch {
                it.seedDummyBookings()
            }
        }
        observeBookings()
    }

    private fun observeBookings() {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            getAllBookingsUseCase().collectLatest { bookings ->
                _uiState.value = ScheduleUiState.Success(bookings)
            }
        }
    }
}
