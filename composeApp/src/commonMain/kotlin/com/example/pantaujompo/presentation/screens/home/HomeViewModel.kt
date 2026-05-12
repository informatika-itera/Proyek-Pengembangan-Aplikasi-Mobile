package com.example.pantaujompo.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.domain.model.FitnessLog
import com.example.pantaujompo.domain.model.LogType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// MAIN UI State untuk Dashboard
data class HomeUIState(
    val consistencyStatus: String = "Pejuang Sehat",
    val weeklyGoalStatus: Float = 0.60f, // 60% bar
    val satelliteTrackingState: SatelliteTrackingState = SatelliteTrackingState.Idle,
    val lastActivities: List<FitnessLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// MAIN GPS State
sealed class SatelliteTrackingState {
    object Idle : SatelliteTrackingState()
    object RequestingLocation : SatelliteTrackingState()
    object Tracking : SatelliteTrackingState()
    data class Error(val message: String) : SatelliteTrackingState()
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState = _uiState.asStateFlow()

    // ViewModel property untuk GPS actual declaration di androidMain
    // expect fun getPlatformLocationProvider(): LocationProvider (di Koin setup)

    init {
        loadData()
    }

    private fun loadData() {
        // Implementasi proper CRUD state management di Sprint 2 (READ part)
        // vm.activities = repository.getActivities().collect { ... }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulasi load data
            _uiState.update { it.copy(
                isLoading = false,
                lastActivities = listOf(
                    FitnessLog(id = 1, type = LogType.RUN, title = "Lari di Embung ITERA", timestamp = Clock.System.now(), details = mapOf("Calories" to "300 kkal", "Distance" to "2.5 km"))
                )
            ) }
        }
    }

    fun startTracking() {
        // MAIN: Fitur GPS part (ViewModel logic)
        _uiState.update { it.copy(satelliteTrackingState = SatelliteTrackingState.RequestingLocation) }
        // vm.getPlatformLocationProvider().requestSingleUpdate { ... update state }
        // Untuk prototype, kita langsung pindah ke state 'Tracking' setelah simulasi
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(satelliteTrackingState = SatelliteTrackingState.Tracking) }
        }
    }

    fun stopTracking() {
        // MAIN: Fitur GPS part (ViewModel logic)
        // Save data to CRUD repo, vm.activities.addLog(...)
        _uiState.update { it.copy(satelliteTrackingState = SatelliteTrackingState.Idle) }
    }
}