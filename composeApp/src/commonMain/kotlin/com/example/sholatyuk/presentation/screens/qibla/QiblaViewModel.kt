package com.example.sholatyuk.presentation.screens.qibla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sholatyuk.core.location.LocationService
import com.example.sholatyuk.core.sensor.ICompassSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.PI       // <-- Tambahkan import PI dari kotlin.math
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class QiblaUiState(
    val isLoading: Boolean = true,
    val azimuth: Float = 0f,
    val qiblaAngle: Float = 0f,
    val needleAngle: Float = 0f,
    val cityName: String = "",
    val distanceKm: Int = 0,
    val error: String? = null,
    val isCompassAvailable: Boolean = true
)

class QiblaViewModel(
    private val locationService: LocationService,
    private val compassSensor: ICompassSensor
) : ViewModel() {

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private val kaabaLat = 21.4225
    private val kaabaLon = 39.8262

    init {
        checkCompassAvailability()
        fetchLocationAndQibla()
        startCompassUpdates()
    }

    private fun checkCompassAvailability() {
        if (!compassSensor.isCompassAvailable()) {
            _uiState.update { it.copy(isCompassAvailable = false) }
        }
    }

    private fun fetchLocationAndQibla() {
        viewModelScope.launch {
            val location = locationService.getCurrentLocation()
            if (location != null) {
                val qiblaAngle = calculateQiblaAngle(location.latitude, location.longitude)
                val distance   = calculateDistanceKm(location.latitude, location.longitude)
                _uiState.update {
                    it.copy(
                        isLoading  = false,
                        qiblaAngle = qiblaAngle,
                        cityName   = location.city ?: "",
                        distanceKm = distance,
                        error      = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Gagal mendapatkan lokasi. Pastikan GPS aktif."
                    )
                }
            }
        }
    }

    private fun startCompassUpdates() {
        viewModelScope.launch {
            compassSensor.azimuthFlow.collect { azimuth: Float ->
                _uiState.update { state ->
                    // Menggunakan modulus dengan aman untuk Float
                    val diff = state.qiblaAngle - azimuth
                    val needleAngle = ((diff % 360f) + 360f) % 360f

                    state.copy(azimuth = azimuth, needleAngle = needleAngle)
                }
            }
        }
    }

    private fun calculateQiblaAngle(userLat: Double, userLon: Double): Float {
        val lat1 = toRad(userLat)
        val lon1 = toRad(userLon)
        val lat2 = toRad(kaabaLat)
        val lon2 = toRad(kaabaLon)
        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return ((toDeg(atan2(y, x)) + 360.0) % 360.0).toFloat()
    }

    private fun calculateDistanceKm(userLat: Double, userLon: Double): Int {
        val earthRadius = 6371.0
        val dLat = toRad(kaabaLat - userLat)
        val dLon = toRad(kaabaLon - userLon)

        // Memperbaiki pembagian dan parameter pow() menjadi Double (2.0)
        val a = sin(dLat / 2.0).pow(2.0) +
                cos(toRad(userLat)) * cos(toRad(kaabaLat)) * sin(dLon / 2.0).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return (earthRadius * c).toInt()
    }

    // Memperbaiki Math.PI menjadi PI bawaan kotlin.math
    private fun toRad(deg: Double) = deg * (PI / 180.0)
    private fun toDeg(rad: Double) = rad * (180.0 / PI)
}