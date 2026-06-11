package com.example.travelplanner.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.core.service.CityImageService
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.UserProfile
import com.example.travelplanner.domain.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUiState(
    val isLoading: Boolean = false,
    val recentTrips: List<Trip> = emptyList(),
    val cityImages: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
    val userProfile: UserProfile = UserProfile("Taufik Hidayat", "taufik@traveler.com")
)

class HomeViewModel(
    private val tripRepository: TripRepository,
    private val cityImageService: CityImageService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // NOTE: No init{} here — loadRecentTrips() is triggered only by LaunchedEffect in HomeScreen
    // Prevents double-loading race condition where two coroutines overwrite each other's cityImages.

    fun loadRecentTrips() {
        viewModelScope.launch {
            // Atomic update using StateFlow.update{} — thread-safe CAS operation
            _uiState.update { it.copy(isLoading = true) }

            tripRepository.getAllTrips()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memuat: ${e.message}") }
                }
                .collect { trips ->
                    // 1. Calculate needed BEFORE updating _uiState with placeholders.
                    // We only treat a city as "already loaded" if it has a real Wikipedia or Unsplash image.
                    // Placeholder images (containing loremflickr.com) are treated as NOT loaded so they can be upgraded.
                    val alreadyLoaded = _uiState.value.cityImages
                        .filterValues { !it.contains("loremflickr.com") }
                        .keys
                        .map { it.lowercase().trim() }
                        .toSet()

                    val needed = trips.take(5).map { it.destination }
                        .distinct()
                        .filter { it.lowercase().trim() !in alreadyLoaded }

                    // 2. Populate cityImages immediately with cache/loremflickr (preserving existing cache)
                    val immediateImages = trips.associate { trip ->
                        trip.destination to cityImageService.getImmediateUrl(trip.destination)
                    } + _uiState.value.cityImages

                    // .update{} reads-modify-write atomically → cityImages never lost
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recentTrips = trips,
                            cityImages = immediateImages,
                            errorMessage = null
                        )
                    }

                    if (needed.isNotEmpty()) {
                        fetchImages(needed)
                    }
                }
        }
    }

    /**
     * Fetch images on IO dispatcher. Results applied atomically — concurrent state updates
     * from the Flow collector will never erase already-loaded cityImages.
     */
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

    fun loadUserProfile() {
        viewModelScope.launch {
            tripRepository.getProfile()
                .catch { /* silent error */ }
                .collect { profile ->
                    if (profile != null) {
                        _uiState.update { it.copy(userProfile = profile) }
                    }
                }
        }
    }

    fun updateProfile(name: String, email: String = "taufik@traveler.com") {
        viewModelScope.launch {
            val updated = UserProfile(name, email)
            _uiState.update { it.copy(userProfile = updated) }
            tripRepository.saveProfile(updated)
        }
    }
}