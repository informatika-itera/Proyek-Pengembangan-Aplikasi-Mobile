package com.example.sholatyuk.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sholatyuk.core.location.LocationService
import com.example.sholatyuk.domain.repository.PrayerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val prayerRepository: PrayerRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchPrayerTimes()
        startClock()
    }

    private fun startClock() {
        viewModelScope.launch {
            while (isActive) {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val timeString = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}:${now.second.toString().padStart(2, '0')}"
                _uiState.update { it.copy(currentTime = timeString) }
                delay(1000)
            }
        }
    }

    fun fetchPrayerTimes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 1. Cek izin lokasi yang SEKARANG SUDAH ASLI!
            val hasPermission = locationService.hasLocationPermission()
            if (!hasPermission) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Izin lokasi belum diberikan. Silakan aktifkan di Pengaturan HP.")
                }
                return@launch
            }

            // 2. Ambil kordinat GPS dari HP secara otomatis
            val location = locationService.getCurrentLocation()
            if (location != null) {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                // 3. Ambil jadwal dari Repository berdasarkan titik pengguna berdiri
                prayerRepository.fetchAndSavePrayerTime(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    date = today
                ).fold(
                    onSuccess = { data ->
                        // Timpa "Asia/Jakarta" dengan nama kota dari GPS
                        val updatedData = data.copy(cityName = location.city ?: data.cityName)
                        _uiState.update { it.copy(isLoading = false, prayerTime = updatedData) }
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(isLoading = false, error = exception.message) }
                    }
                )
            } else {
                // 👇 JIKA GPS MATI, MUNCULKAN POP-UP!
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showGpsDialog = true,
                        error = "Gagal mengambil titik koordinat. Pastikan GPS HP menyala."
                    )
                }
            }
        }
    }

    // 👇 Fungsi untuk dipanggil oleh tombol "Buka Pengaturan" di UI
    fun onOpenGpsSettings() {
        locationService.openLocationSettings()
    }

    // 👇 Fungsi untuk menutup Pop-up
    fun dismissGpsDialog() {
        _uiState.update { it.copy(showGpsDialog = false) }
    }
}