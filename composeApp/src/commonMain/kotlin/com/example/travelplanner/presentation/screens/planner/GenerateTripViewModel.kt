package com.example.travelplanner.presentation.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.domain.usecase.GenerateItineraryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.datetime.Clock

data class GenerateTripUiState(
    val isLoading: Boolean = false,
    val successTripId: String? = null,
    val errorMessage: String? = null
)

class GenerateTripViewModel(
    private val generateItineraryUseCase: GenerateItineraryUseCase,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GenerateTripUiState>(GenerateTripUiState())
    val uiState: StateFlow<GenerateTripUiState> = _uiState.asStateFlow()

    fun generateTrip(
        departureCity: String,
        destination: String,
        startDate: String,
        endDate: String,
        duration: String,
        vibe: String,
        specialNotes: String
    ) {
        viewModelScope.launch {
            _uiState.value = GenerateTripUiState(isLoading = true)
            try {
                // Request AI Itinerary
                val aiResponse = generateItineraryUseCase.execute(
                    destination = destination,
                    duration = duration,
                    vibe = vibe
                )

                // Check for errors in response
                if (aiResponse.contains("\"error\"")) {
                    val errorMsg = try {
                        Json.parseToJsonElement(aiResponse).jsonObject["error"]?.jsonPrimitive?.content
                    } catch (e: Exception) {
                        "Gagal menyusun itinerary liburan lewat AI."
                    }
                    _uiState.value = GenerateTripUiState(
                        isLoading = false,
                        errorMessage = errorMsg ?: "Gagal menghasilkan itinerary"
                    )
                    return@launch
                }

                // Decode list of items dengan pembersihan JSON murni
                val cleanedResponse = cleanJson(aiResponse)
                val items = try {
                    Json.decodeFromString<List<ItineraryItem>>(cleanedResponse)
                } catch (e: Exception) {
                    emptyList()
                }

                if (items.isEmpty()) {
                    _uiState.value = GenerateTripUiState(
                        isLoading = false,
                        errorMessage = "Respons format AI tidak cocok atau terputus. Silakan coba kembali."
                    )
                    return@launch
                }

                // Generate dynamic ID based on timestamp
                val tripIdStr = "trip_${Clock.System.now().toEpochMilliseconds()}"

                val newTrip = Trip(
                    id = tripIdStr,
                    destination = destination,
                    startDate = startDate,
                    endDate = endDate,
                    duration = "$departureCity|$duration",
                    vibe = vibe,
                    itineraryItems = items
                )

                // Save to local SQLite
                tripRepository.saveTrip(newTrip)

                _uiState.value = GenerateTripUiState(
                    isLoading = false,
                    successTripId = tripIdStr
                )
            } catch (e: Exception) {
                _uiState.value = GenerateTripUiState(
                    isLoading = false,
                    errorMessage = "Terjadi kegagalan jaringan: ${e.message}"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = GenerateTripUiState()
    }

    private fun cleanJson(rawText: String): String {
        val trimmed = rawText.trim()
        val startIndex = trimmed.indexOfAny(charArrayOf('[', '{'))
        if (startIndex == -1) return trimmed
        val endIndex = trimmed.lastIndexOfAny(charArrayOf(']', '}'))
        if (endIndex == -1 || endIndex < startIndex) return trimmed
        return trimmed.substring(startIndex, endIndex + 1)
    }
}
