package com.example.sholatyuk.domain.model

data class QiblaInfo(
    val direction: Double,      // Arah kiblat dalam derajat (0-360)
    val latitude: Double,       // Latitude user
    val longitude: Double,      // Longitude user
    val cityName: String
)
