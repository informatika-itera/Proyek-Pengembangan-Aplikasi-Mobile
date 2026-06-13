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
import kotlinx.datetime.*

class PrayerViewModel(
    private val prayerRepository: PrayerRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    init {
        loadPrayerTimes()
    }

    fun loadPrayerTimes(date: LocalDate = _uiState.value.selectedDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, selectedDate = date) }

            val hasPermission = locationService.hasLocationPermission()
            if (!hasPermission) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Izin lokasi belum diberikan.")
                }
                return@launch
            }

            val location = locationService.getCurrentLocation()
            if (location != null) {
                prayerRepository.fetchAndSavePrayerTime(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    date = date
                ).fold(
                    onSuccess = { data ->
                        _uiState.update { it.copy(isLoading = false, prayerTime = data) }
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(isLoading = false, error = exception.message) }
                    }
                )
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = "Gagal mengambil lokasi GPS.")
                }
            }
        }
    }

    fun onPreviousDate() {
        val prevDate = _uiState.value.selectedDate.minus(1, DateTimeUnit.DAY)
        loadPrayerTimes(prevDate)
    }

    fun onNextDate() {
        val nextDate = _uiState.value.selectedDate.plus(1, DateTimeUnit.DAY)
        loadPrayerTimes(nextDate)
    }
}