package com.example.fitgen.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Merepresentasikan satu catatan metrik tubuh pengguna pada waktu tertentu.
 *
 * @property id              Identifier unik (0 = belum tersimpan)
 * @property tanggal         Timestamp Unix (milli) saat pengukuran dilakukan
 * @property beratBadan      Berat badan dalam kilogram
 * @property persentaseLemak Persentase lemak tubuh (0-100)
 * @property catatan         Catatan opsional terkait pengukuran
 */
data class BodyMetric(
    val id: Long = 0,
    val tanggal: Long = Clock.System.now().toEpochMilliseconds(),
    val beratBadan: Double = 0.0,
    val persentaseLemak: Double = 0.0,
    val catatan: String = ""
) {
    val tanggalFormatted: String
        get() {
            val dt = Instant.fromEpochMilliseconds(tanggal)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            return "${dt.date.dayOfMonth}/${dt.date.monthNumber}/${dt.date.year}"
        }

    val isValid: Boolean
        get() = beratBadan > 0.0
}