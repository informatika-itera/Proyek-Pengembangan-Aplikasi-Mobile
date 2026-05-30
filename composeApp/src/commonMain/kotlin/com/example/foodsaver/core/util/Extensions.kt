package com.example.foodsaver.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

/**
 * Extension untuk memformat Instant menjadi string tanggal yang mudah dibaca.
 */
fun Instant.formatToDisplay(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
}

/**
 * Extension untuk memformat jumlah stok makanan.
 * Menghilangkan .0 jika nilainya bulat (misal 12.0 -> 12).
 * Menambahkan satuan di belakangnya.
 */
fun Double.formatQuantity(unit: String): String {
    val formattedValue = if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
    return "$formattedValue $unit"
}
