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
    SAKIT("Sakit"),
    BELUM_MULAI("Belum Mulai");
    
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
            totalAlpha <= 2 -> RiskLevel.AMAN
            totalAlpha == 3 -> RiskLevel.WARNING
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
    val reminderOffsetMinutes: Long? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)

/**
 * Pilihan reminder notifikasi lokal untuk jadwal.
 *
 * offsetMinutes null berarti tidak ada notifikasi.
 */
enum class ReminderOption(val displayName: String, val offsetMinutes: Long?) {
    NONE("Tidak ada", null),
    ONE_MINUTE("1 menit", 1),
    TEN_MINUTES("10 menit", 10),
    THIRTY_MINUTES("30 menit", 30),
    ONE_HOUR("1 jam", 60),
    ONE_DAY("1 hari", 24 * 60);

    companion object {
        fun fromOffset(offsetMinutes: Long?): ReminderOption {
            return entries.find { it.offsetMinutes == offsetMinutes } ?: NONE
        }
    }
}

/**
 * Jenis jadwal/pengingat akademik
 */
enum class JenisJadwal(val displayName: String) {
    REMINDER("Kelas"),
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

/**
 * Domain model untuk Kelas / Mata Kuliah aktif mahasiswa
 */
data class Kelas(
    val nomorMk: String,
    val kodeMk: String,
    val kodeKelas: String,
    val namaKelas: String,
    val mode: String?,
    val namaMk: String,
    val sksMk: String,
    val namaDosenList: String,
    val jadwalHari: String
)
