package com.example.sholatyuk.presentation.screens.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sholatyuk.core.location.LocationService
import com.example.sholatyuk.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PrayerViewModel(
    private val prayerRepository: PrayerRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    init {
        loadPrayerTime()
    }

    fun loadPrayerTime() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val location = locationService.getCurrentLocation()
                if (location == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Lokasi tidak tersedia. Pastikan izin lokasi sudah diberikan.") }
                    return@launch
                }
                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date

                prayerRepository.fetchAndSavePrayerTime(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    date = today
                ).onSuccess { prayerTime ->
                    _uiState.update { it.copy(isLoading = false, prayerTime = prayerTime) }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun retry() = loadPrayerTime()
}