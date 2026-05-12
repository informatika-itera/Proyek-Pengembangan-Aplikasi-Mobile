package com.example.pantaujompo.presentation.screens.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.domain.model.ActivityModel
import com.example.pantaujompo.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

// State Management (P1 Goal)
sealed class TrackingState {
    object Idle : TrackingState()
    object Saving : TrackingState()
    object Success : TrackingState()
    data class Error(val message: String) : TrackingState()
}

class TrackingViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TrackingState>(TrackingState.Idle)
    val state: StateFlow<TrackingState> = _state.asStateFlow()

    // Fungsi CREATE untuk CRUD (P0 Goal)
    fun saveActivity(distance: Double, duration: Int, calories: Int) {
        viewModelScope.launch {
            _state.value = TrackingState.Saving
            try {
                repository.insertActivity(
                    ActivityModel(
                        title = "Lari Santai (Tracking)",
                        distanceKm = distance,
                        durationMins = duration,
                        calories = calories,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                )
                _state.value = TrackingState.Success
            } catch (e: Exception) {
                _state.value = TrackingState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}