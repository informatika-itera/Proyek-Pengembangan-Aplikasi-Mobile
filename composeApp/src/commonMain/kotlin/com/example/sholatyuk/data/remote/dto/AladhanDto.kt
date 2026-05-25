package com.example.sholatyuk.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AladhanResponse(
    val code: Int,
    val status: String,
    val data: AladhanData
)

@Serializable
data class AladhanData(
    val timings: AladhanTimings,
    val date: AladhanDate
)

@Serializable
data class AladhanTimings(
    @SerialName("Fajr") val fajr: String,
    @SerialName("Sunrise") val sunrise: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String,
    @SerialName("Imsak") val imsak: String,
    @SerialName("Midnight") val midnight: String
)

@Serializable
data class AladhanDate(
    val readable: String,
    val timestamp: String,
    val hijri: AladhanHijri
)

@Serializable
data class AladhanHijri(
    val date: String,
    val month: AladhanHijriMonth,
    val year: String
)

@Serializable
data class AladhanHijriMonth(
    val number: Int,
    val en: String,
    val ar: String
)