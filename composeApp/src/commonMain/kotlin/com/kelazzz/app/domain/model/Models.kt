package com.kelazzz.app.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Domain model untuk data presensi/kehadiran mahasiswa
 */
data class Presensi(
    val id: Long = 0,
    val mataKuliahId: String,
    val mataKuliahNama: String,
    val pertemuan: Int,
    val status: StatusPresensi,
    val tanggal: String,
    val lastSync: Instant = Clock.System.now()
)

/**
 * Status kehadiran mahasiswa
 */
enum class StatusPresensi(val displayName: String) {
    HADIR("Hadir"),
    ALPHA("Alpha"),
    IZIN("Izin"),
    SAKIT("Sakit");
    
    companion object {
        fun fromString(value: String): StatusPresensi {
            return entries.find { it.name == value } ?: ALPHA
        }
    }
}

/**
 * Ringkasan kehadiran per mata kuliah
 */
data class AttendanceSummary(
    val mataKuliahId: String,
    val mataKuliahNama: String,
    val totalPertemuan: Int,
    val totalHadir: Int,
    val totalAlpha: Int,
    val totalIzin: Int,
    val totalSakit: Int
) {
    val persentaseKehadiran: Float
        get() = if (totalPertemuan > 0) (totalHadir.toFloat() / totalPertemuan) * 100f else 0f
    
    val riskLevel: RiskLevel
        get() = when {
            persentaseKehadiran >= 80f -> RiskLevel.AMAN
            persentaseKehadiran >= 70f -> RiskLevel.WARNING
            else -> RiskLevel.BAHAYA
        }
}

/**
 * Level risiko kehadiran
 */
enum class RiskLevel(val displayName: String, val emoji: String) {
    AMAN("Aman", "✅"),
    WARNING("Warning", "⚠️"),
    BAHAYA("Bahaya", "🚨")
}

/**
 * Domain model untuk jadwal akademik pribadi
 */
data class Jadwal(
    val id: Long = 0,
    val judul: String,
    val deskripsi: String = "",
    val tanggal: String,
    val waktu: String = "",
    val jenis: JenisJadwal = JenisJadwal.REMINDER,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)

/**
 * Jenis jadwal/pengingat akademik
 */
enum class JenisJadwal(val displayName: String) {
    REMINDER("Pengingat"),
    TUGAS("Tugas"),
    UJIAN("Ujian"),
    KUIS("Kuis"),
    PRESENTASI("Presentasi"),
    LAINNYA("Lainnya");
    
    companion object {
        fun fromString(value: String): JenisJadwal {
            return entries.find { it.name == value } ?: REMINDER
        }
    }
}
