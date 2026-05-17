package com.example.sholatyuk.domain.model

import kotlinx.datetime.LocalDate

data class PrayerTime(
    val id: Long = 0,
    val date: LocalDate,
    val fajr: String,       // Subuh
    val sunrise: String,    // Terbit
    val dhuhr: String,      // Dzuhur
    val asr: String,        // Ashar
    val maghrib: String,    // Maghrib
    val isha: String,       // Isya
    val imsak: String,
    val midnight: String,   // Tengah Malam
    val latitude: Double,
    val longitude: Double,
    val cityName: String
)

enum class PrayerName(val displayName: String) {
    FAJR("Subuh"),
    SUNRISE("Terbit"),
    DHUHR("Dzuhur"),
    ASR("Ashar"),
    MAGHRIB("Maghrib"),
    ISHA("Isya"),
    MIDNIGHT("Tengah Malam")
}

data class NextPrayer(
    val name: PrayerName,
    val time: String,
    val remainingMinutes: Long
)
