package com.example.travelplanner.presentation.screens.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.core.service.CityImageService
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MyTripsUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val cityImages: Map<String, String> = emptyMap(),
    val errorMessage: String? = null
)

class MyTripsViewModel(
    private val tripRepository: TripRepository,
    private val cityImageService: CityImageService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTripsUiState())
    val uiState: StateFlow<MyTripsUiState> = _uiState.asStateFlow()

    // No init{} — avoids double-loading. Triggered by LaunchedEffect in screen.

    fun loadTrips() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            tripRepository.getAllTrips()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memuat: ${e.message}") }
                }
                .collect { trips ->
                    // 1. Calculate needed BEFORE updating _uiState with placeholders.
                    val alreadyLoaded = _uiState.value.cityImages
                        .filterValues { !it.contains("loremflickr.com") }
                        .keys
                        .map { it.lowercase().trim() }
                        .toSet()

                    val needed = trips.map { it.destination }
                        .distinct()
                        .filter { it.lowercase().trim() !in alreadyLoaded }

                    // 2. Populate cityImages immediately with cache/loremflickr (preserving existing cache)
                    val immediateImages = trips.associate { trip ->
                        trip.destination to cityImageService.getImmediateUrl(trip.destination)
                    } + _uiState.value.cityImages

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            trips = trips,
                            cityImages = immediateImages,
                            errorMessage = null
                        )
                    }
                    if (needed.isNotEmpty()) fetchImages(needed)
                }
        }
    }

    private fun fetchImages(destinations: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val unique = destinations.distinct()
            val deferred = unique.map { city ->
                async { city to cityImageService.getImageUrl(city) }
            }
            val results = deferred.map { it.await() }.toMap()
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(cityImages = it.cityImages + results) }
            }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            try {
                tripRepository.deleteTrip(tripId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Gagal menghapus perjalanan: ${e.message}") }
            }
        }
    }
}
