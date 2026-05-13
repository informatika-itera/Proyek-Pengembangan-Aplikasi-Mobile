package com.example.fitgen.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Merepresentasikan satu sesi latihan (workout log) yang dilakukan oleh pengguna.
 *
 * @property id          Identifier unik log (0 = belum tersimpan ke database)
 * @property tanggal     Tanggal sesi latihan dalam format [LocalDate]
 * @property gerakan     Daftar gerakan/latihan yang dilakukan dalam sesi ini
 * @property catatan     Catatan opsional untuk sesi latihan
 */
data class WorkoutLog(
    val id: Long = 0,
    val tanggal: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val gerakan: List<Exercise> = emptyList(),
    val catatan: String = ""
) {
    /** Total set keseluruhan dari semua gerakan dalam sesi ini */
    val totalSets: Int
        get() = gerakan.sumOf { it.sets }

    /** Total volume latihan (sets × reps × beban) dalam kg */
    val totalVolume: Double
        get() = gerakan.sumOf { it.sets * it.reps * it.beban }

    /** Jumlah gerakan unik dalam sesi ini */
    val jumlahGerakan: Int
        get() = gerakan.size

    /** True jika log memiliki minimal satu gerakan */
    val isValid: Boolean
        get() = gerakan.isNotEmpty()
}
