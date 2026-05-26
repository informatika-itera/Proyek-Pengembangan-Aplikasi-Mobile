package com.example.fitgen.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class MealType(val label: String) {
    BREAKFAST("Sarapan"),
    LUNCH("Makan Siang"),
    DINNER("Makan Malam"),
    SNACK("Camilan")
}

data class MealLog(
    val id: Long = 0,
    val tanggal: Long = Clock.System.now().toEpochMilliseconds(),
    val namaMakanan: String = "",
    val kalori: Int = 0,
    val proteinG: Double = 0.0,
    val karbohidratG: Double = 0.0,
    val lemakG: Double = 0.0,
    val jenisMakan: MealType = MealType.BREAKFAST
) {

    val tanggalFormatted: String
        get() {
            val dt = Instant.fromEpochMilliseconds(tanggal)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            return "${dt.date.dayOfMonth}/${dt.date.monthNumber}/${dt.date.year}"
        }

    val isValid: Boolean
        get() = namaMakanan.isNotBlank() && kalori >= 0
}