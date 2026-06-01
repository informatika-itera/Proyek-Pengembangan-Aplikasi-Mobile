package com.mywallet.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Mendapatkan tanggal saat ini dalam format ISO 8601 (YYYY-MM-DD)
 */
fun getCurrentIsoDate(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val year = now.year
    val month = now.monthNumber.toString().padStart(2, '0')
    val day = now.dayOfMonth.toString().padStart(2, '0')
    return "$year-$month-$day"
}

/**
 * Mendapatkan waktu saat ini dalam format HH:mm
 */
fun getCurrentTime(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = now.hour.toString().padStart(2, '0')
    val minute = now.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

/**
 * Konversi milliseconds ke format ISO 8601 (YYYY-MM-DD)
 */
fun formatMillisToIsoDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    
    val year = dateTime.year
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    
    return "$year-$month-$day"
}

/**
 * Konversi format ISO (YYYY-MM-DD) ke tampilan ramah pengguna (misal: 20 Mei 2024)
 */
fun formatIsoDateToDisplay(isoDate: String): String {
    return try {
        if (!isoDate.contains("-")) return isoDate
        
        val parts = isoDate.split("-")
        if (parts.size != 3) return isoDate
        
        val year = parts[0]
        val monthNumber = parts[1].toInt()
        val day = parts[2].toInt()
        
        val monthNames = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        
        "$day ${monthNames[monthNumber - 1]} $year"
    } catch (e: Exception) {
        isoDate
    }
}

/**
 * Alias untuk kompatibilitas
 */
fun formatMillisToDate(millis: Long): String {
    return formatMillisToIsoDate(millis)
}
