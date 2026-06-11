package com.example.foodsaver.core.utility

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Utility extensions for FoodSaver
 */

fun Instant.formatToDisplay(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val year = dateTime.year
    return "$day/$month/$year"
}

fun Double.formatQuantity(unit: String): String {
    val formattedValue = if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        // Simple formatting to avoid long decimals, e.g., 1.50000000001
        ((this * 100).toInt() / 100.0).toString()
    }
    return "$formattedValue $unit"
}
