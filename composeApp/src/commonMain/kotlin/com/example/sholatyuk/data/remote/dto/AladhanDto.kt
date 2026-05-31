package com.example.sholatyuk.data.remote.dto

import kotlinx.serialization.SerialName // Tambahkan import ini
import kotlinx.serialization.Serializable

@Serializable
data class AladhanResponse(
    val code: Int,
    val status: String,
    val data: AladhanData
)

@Serializable
data class AladhanData(
    val timings: PrayerTimings,
    val date: AladhanDate,
    val meta: AladhanMeta
)

// Peringatan kuning akan hilang dengan menggunakan @SerialName
@Serializable
data class PrayerTimings(
    @SerialName("Imsak") val imsak: String,
    @SerialName("Fajr") val fajr: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String
)

@Serializable
data class AladhanDate(
    val readable: String
)

@Serializable
data class AladhanMeta(
    val timezone: String
)