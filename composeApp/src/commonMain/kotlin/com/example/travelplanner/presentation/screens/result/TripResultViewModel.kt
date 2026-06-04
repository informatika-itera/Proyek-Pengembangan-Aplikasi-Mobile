package com.example.travelplanner.presentation.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.core.service.CityImageService
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.domain.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString

data class TripResultUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val totalExpenses: Double = 0.0,
    val cityPhotoUrl: String? = null,
    val errorMessage: String? = null
)

class TripResultViewModel(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
    private val cityImageService: CityImageService,
    private val aiRepository: com.example.travelplanner.domain.repository.AIRepository
) : ViewModel() {
    private val jsonParser = kotlinx.serialization.json.Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private val _uiState = MutableStateFlow(TripResultUiState())
    val uiState: StateFlow<TripResultUiState> = _uiState.asStateFlow()

    fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                tripRepository.getTripById(tripId),
                expenseRepository.getTotalExpensesForTrip(tripId)
            ) { trip, total ->
                TripResultUiState(
                    isLoading = false,
                    trip = trip,
                    totalExpenses = total,
                    cityPhotoUrl = null,
                    errorMessage = if (trip == null) "Perjalanan tidak ditemukan." else null
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memuat: ${e.message}") }
            }.collect { newState ->
                // Preserve cityPhotoUrl if already loaded
                val currentUrl = _uiState.value.cityPhotoUrl
                _uiState.update { _ -> newState.copy(cityPhotoUrl = currentUrl) }

                // Only fetch if not already loaded
                if (currentUrl == null && newState.trip != null) {
                    val destination = newState.trip.destination

                    // ── Step 1: Set loremflickr URL IMMEDIATELY (no network call, no wait)
                    // This ensures the hero card always shows an image right away.
                    val immediateUrl = cityImageService.loremflickr(destination)
                    _uiState.update { it.copy(cityPhotoUrl = immediateUrl) }

                    // ── Step 2: Try to upgrade to Wikipedia image (cached = instant)
                    viewModelScope.launch(Dispatchers.IO) {
                        val betterUrl = cityImageService.getImageUrl(destination)
                        if (betterUrl != immediateUrl) {
                            withContext(Dispatchers.Main) {
                                _uiState.update { it.copy(cityPhotoUrl = betterUrl) }
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkAndTranslateItinerary(trip: Trip, isEnglish: Boolean) {
        if (!isEnglish) return // Only run translation when English is needed
        
        val needsTranslation = trip.itineraryItems.any { it.activityEn.isBlank() }
        if (!needsTranslation) return

        viewModelScope.launch {
            try {
                val jsonItinerary = jsonParser.encodeToString(trip.itineraryItems)
                val translatedJson = aiRepository.translateItinerary(jsonItinerary)
                
                val startIndex = translatedJson.indexOfAny(charArrayOf('[', '{'))
                val endIndex = translatedJson.lastIndexOfAny(charArrayOf(']', '}'))
                if (startIndex == -1 || endIndex == -1 || endIndex < startIndex) return@launch
                val cleanJson = translatedJson.substring(startIndex, endIndex + 1)
                
                val translatedItems = jsonParser.decodeFromString<List<com.example.travelplanner.domain.model.ItineraryItem>>(cleanJson)
                if (translatedItems.isNotEmpty()) {
                    val updatedTrip = trip.copy(itineraryItems = translatedItems)
                    tripRepository.saveTrip(updatedTrip)
                    // saveTrip uses INSERT OR REPLACE so the Flow from getTripById will re-emit → UI updates
                }
            } catch (e: Exception) {
                // Silently fail; translation is best-effort
            }
        }
    }
}
