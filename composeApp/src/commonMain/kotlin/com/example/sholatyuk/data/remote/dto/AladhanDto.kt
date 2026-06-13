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
    val timings: PrayerTimings,
    val date: AladhanDate,
    val meta: AladhanMeta
)

@Serializable
data class PrayerTimings(
    @SerialName("Imsak") val imsak: String,
    @SerialName("Fajr") val fajr: String,
    @SerialName("Sunrise") val sunrise: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String
)

@Serializable
data class AladhanDate(
    val readable: String,
    val hijri: HijriDate
)

@Serializable
data class HijriDate(
    val date: String,
    val format: String,
    val day: String,
    val weekday: HijriWeekday,
    val month: HijriMonth,
    val year: String,
    val designation: Designation
)

@Serializable
data class HijriWeekday(
    val en: String,
    val ar: String? = null
)

@Serializable
data class HijriMonth(
    val number: Int,
    val en: String,
    val ar: String? = null
)

@Serializable
data class Designation(
    val abbreviated: String,
    val expanded: String
)

@Serializable
data class AladhanMeta(
    val timezone: String
)