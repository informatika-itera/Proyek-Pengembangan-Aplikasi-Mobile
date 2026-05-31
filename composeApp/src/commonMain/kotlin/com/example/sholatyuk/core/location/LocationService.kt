package com.example.sholatyuk.core.location

// 1. Ini adalah cetakan datanya
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val city: String? = null
)

// 2. Ini adalah cetak biru (blueprint) mesin GPS-nya
expect class LocationService {
    suspend fun getCurrentLocation(): LocationData?
    fun hasLocationPermission(): Boolean

    fun openLocationSettings()
}