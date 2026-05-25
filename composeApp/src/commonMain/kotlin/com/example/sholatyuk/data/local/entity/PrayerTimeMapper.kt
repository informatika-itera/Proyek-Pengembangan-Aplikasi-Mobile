package com.example.sholatyuk.data.local.entity

import com.example.sholatyuk.data.local.PrayerTimeEntity
import com.example.sholatyuk.domain.model.PrayerTime
import kotlinx.datetime.LocalDate

// ── PrayerTimeEntity → Domain ─────────────────────────────────────

fun PrayerTimeEntity.toDomain(): PrayerTime = PrayerTime(
    id        = id,
    date      = LocalDate.parse(date),   // "YYYY-MM-DD" → LocalDate
    fajr      = fajr,
    sunrise   = sunrise,
    dhuhr     = dhuhr,
    asr       = asr,
    maghrib   = maghrib,
    isha      = isha,
    imsak     = imsak,
    midnight  = midnight,
    latitude  = latitude,
    longitude = longitude,
    cityName  = city_name
)

fun List<PrayerTimeEntity>.toDomainList(): List<PrayerTime> = map { it.toDomain() }

// ── Domain → Insert params ────────────────────────────────────────

/**
 * Format LocalDate ke string "YYYY-MM-DD" untuk disimpan ke database.
 * Menggunakan toString() dari kotlinx.datetime yang sudah ISO-8601.
 */
fun PrayerTime.dateKey(): String = date.toString()
