package com.example.pantaujompo.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoWeatherResponse(
    val current: CurrentWeather
)

@Serializable
data class CurrentWeather(
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("weather_code") val weatherCode: Int
)

@Serializable
data class OpenMeteoAqiResponse(
    val current: CurrentAqi
)

@Serializable
data class CurrentAqi(
    @SerialName("european_aqi") val aqi: Int? = null
)

// Model gabungan untuk UI
data class WeatherInfo(
    val temperature: Double,
    val weatherCode: Int,
    val aqi: Int
)
