package com.example.pantaujompo.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

object TrackingManager {
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _hasStarted = MutableStateFlow(false)
    val hasStarted: StateFlow<Boolean> = _hasStarted.asStateFlow()

    private val _seconds = MutableStateFlow(0)
    val seconds: StateFlow<Int> = _seconds.asStateFlow()

    private val _totalDistanceMeters = MutableStateFlow(0.0)
    val totalDistanceMeters: StateFlow<Double> = _totalDistanceMeters.asStateFlow()

    private val _routePoints = MutableStateFlow<List<GeoPoint>>(emptyList())
    val routePoints: StateFlow<List<GeoPoint>> = _routePoints.asStateFlow()
    
    private val _jenisOlahraga = MutableStateFlow("Lari")
    val jenisOlahraga: StateFlow<String> = _jenisOlahraga.asStateFlow()

    fun startTracking(jenis: String) {
        _jenisOlahraga.value = jenis
        _hasStarted.value = true
        _isTracking.value = true
    }

    fun pauseTracking() {
        _isTracking.value = false
    }
    
    fun resumeTracking() {
        _isTracking.value = true
    }

    fun stopAndClear() {
        _isTracking.value = false
        _hasStarted.value = false
        _seconds.value = 0
        _totalDistanceMeters.value = 0.0
        _routePoints.value = emptyList()
    }

    // Called by LocationService (Android) or Coroutine (Timer)
    fun incrementTime() {
        if (_isTracking.value) {
            _seconds.value += 1
        }
    }

    fun addLocationPoint(geoPoint: GeoPoint, distanceDelta: Double) {
        if (_isTracking.value) {
            _routePoints.value = _routePoints.value + geoPoint
            _totalDistanceMeters.value += distanceDelta
        }
    }
}
