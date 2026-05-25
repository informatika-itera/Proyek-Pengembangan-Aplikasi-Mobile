package com.example.sholatyuk.core.location

actual class LocationService {
    actual suspend fun getCurrentLocation(): LocationData? = null
    actual fun hasLocationPermission(): Boolean = false
}