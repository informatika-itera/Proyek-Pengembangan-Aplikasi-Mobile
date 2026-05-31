package com.example.sholatyuk.core.location

// Ini adalah dummy (palsu) khusus untuk iOS agar compiler KMP tidak protes.
actual class LocationService {

    actual suspend fun getCurrentLocation(): LocationData? {
        // Nanti jika ingin rilis di iOS, kode GPS Apple (CoreLocation) ditaruh di sini
        return null
    }

    actual fun hasLocationPermission(): Boolean {
        return false
    }

    actual fun openLocationSettings() {
        // Kosongkan saja untuk sekarang
    }
}