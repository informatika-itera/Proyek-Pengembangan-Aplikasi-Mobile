package com.example.inventra.presentation.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Format Instant ke string "dd/MM/yyyy" untuk ditampilkan di UI.
 */
fun Instant.formatDateOnly(): String {
    val dt = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val day = dt.dayOfMonth.toString().padStart(2, '0')
    val month = dt.monthNumber.toString().padStart(2, '0')
    return "$day/$month/${dt.year}"
}