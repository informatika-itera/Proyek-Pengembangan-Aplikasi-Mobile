package com.example.foodsaver.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Extension untuk memformat Instant menjadi string tanggal yang mudah dibaca.
 */
fun Instant.formatToDisplay(): String {
    val dateTime \= this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
}
