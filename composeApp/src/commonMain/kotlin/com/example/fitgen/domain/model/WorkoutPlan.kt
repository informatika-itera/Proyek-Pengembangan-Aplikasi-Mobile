package com.example.fitgen.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Tingkat kesulitan rencana latihan yang direkomendasikan AI.
 */
enum class DifficultyLevel(val displayName: String) {
    PEMULA("Pemula"),
    MENENGAH("Menengah"),
    LANJUTAN("Lanjutan");

    companion object {
        fun fromString(value: String): DifficultyLevel =
            entries.find { it.name == value } ?: PEMULA
    }
}

/**
 * Tujuan utama dari rencana latihan.
 */
enum class WorkoutGoal(val displayName: String) {
    PENURUNAN_BERAT("Penurunan Berat Badan"),
    PEMBENTUKAN_OTOT("Pembentukan Otot"),
    PENINGKATAN_STAMINA("Peningkatan Stamina"),
    FLEKSIBILITAS("Fleksibilitas"),
    KEBUGARAN_UMUM("Kebugaran Umum");

    companion object {
        fun fromString(value: String): WorkoutGoal =
            entries.find { it.name == value } ?: KEBUGARAN_UMUM
    }
}

/**
 * Merepresentasikan rencana latihan yang dibuat oleh AI (Gemini).
 *
 * @property id              Identifier unik rencana (0 = belum tersimpan ke database)
 * @property judul           Judul rencana latihan, misalnya "Program Pemula 4 Minggu"
 * @property deskripsi       Deskripsi singkat yang dihasilkan AI menjelaskan rencana ini
 * @property tujuan          Tujuan utama rencana latihan
 * @property tingkatKesulitan Tingkat kesulitan yang direkomendasikan AI
 * @property durasiMinggu    Durasi total rencana dalam minggu
 * @property sesiPerMinggu   Jumlah sesi latihan yang direkomendasikan per minggu
 * @property daftarGerakan   Daftar gerakan yang dimasukkan AI ke dalam rencana ini
 * @property saran           Saran tambahan dari AI (nutrisi, istirahat, dsb.)
 * @property dibuatPada      Tanggal rencana ini dibuat/di-generate oleh AI
 * @property isAiGenerated   Penanda bahwa rencana ini dihasilkan oleh AI
 */
data class WorkoutPlan(
    val id: Long = 0,
    val judul: String,
    val deskripsi: String = "",
    val tujuan: WorkoutGoal = WorkoutGoal.KEBUGARAN_UMUM,
    val tingkatKesulitan: DifficultyLevel = DifficultyLevel.PEMULA,
    val durasiMinggu: Int = 4,
    val sesiPerMinggu: Int = 3,
    val daftarGerakan: List<Exercise> = emptyList(),
    val saran: String = "",
    val dibuatPada: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val isAiGenerated: Boolean = true
) {
    /** Total gerakan unik dalam rencana ini */
    val jumlahGerakan: Int
        get() = daftarGerakan.size

    /** Estimasi total sesi latihan selama durasi penuh rencana */
    val totalSesi: Int
        get() = durasiMinggu * sesiPerMinggu

    /** True jika rencana memiliki judul dan minimal satu gerakan */
    val isValid: Boolean
        get() = judul.isNotBlank() && daftarGerakan.isNotEmpty()

    /** Ringkasan singkat untuk ditampilkan di daftar/card UI */
    val ringkasan: String
        get() = "${tujuan.displayName} · ${tingkatKesulitan.displayName} · ${durasiMinggu} minggu"
}
