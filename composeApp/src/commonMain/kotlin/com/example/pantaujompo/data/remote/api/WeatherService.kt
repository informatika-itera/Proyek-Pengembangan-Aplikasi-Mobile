package com.example.pantaujompo.data.remote.api

import com.example.pantaujompo.data.remote.model.OpenMeteoAqiResponse
import com.example.pantaujompo.data.remote.model.OpenMeteoWeatherResponse
import com.example.pantaujompo.data.remote.model.WeatherInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class WeatherService(private val client: HttpClient) {

    suspend fun getWeatherAndAqi(lat: Double, lon: Double): WeatherInfo? {
        return try {
            coroutineScope {
                val weatherDeferred = async {
                    client.get("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code")
                        .body<OpenMeteoWeatherResponse>()
                }
                
                val aqiDeferred = async {
                    client.get("https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$lat&longitude=$lon&current=european_aqi")
                        .body<OpenMeteoAqiResponse>()
                }
                
                val weather = weatherDeferred.await()
                val aqi = aqiDeferred.await()
                
                WeatherInfo(
                    temperature = weather.current.temperature,
                    weatherCode = weather.current.weatherCode,
                    aqi = aqi.current.aqi ?: 20 // Default to good if null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
