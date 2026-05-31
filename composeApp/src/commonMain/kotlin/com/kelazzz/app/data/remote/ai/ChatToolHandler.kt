package com.kelazzz.app.data.remote.ai

import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.domain.repository.PresensiRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.toLocalDateTime


/**
 * ChatToolHandler — Inti Tool Calling untuk AI Chatbot KelazZz
 *
 * Handler ini bertanggung jawab:
 * 1. Mendeteksi intent pengguna berdasarkan keyword dalam pesan
 * 2. Mengambil data yang relevan dari repository yang sesuai
 * 3. Menyusun data sebagai konteks tambahan untuk dikirim ke AI API
 *
 * Pendekatan: Prompt-based tool routing (keyword detection → data enrichment)
 * 
 * Prinsip keamanan:
 * - AI TIDAK membaca database secara langsung
 * - AI meminta data melalui tool (repository)
 * - Hanya data milik pengguna yang sedang login yang diakses
 */
class ChatToolHandler(
    private val presensiRepository: PresensiRepository,
    private val jadwalRepository: JadwalRepository,
    private val userPreferences: UserPreferences
) {
    /**
     * Deteksi intent dari pesan pengguna dan ambil data yang relevan.
     * 
     * @param message Pesan dari pengguna
     * @return Context string berisi data yang relevan, atau kosong jika tidak ada tool yang cocok
     */
    suspend fun detectAndFetchContext(message: String): String {
        val lowerMessage = message.lowercase().trim()
        val contextParts = mutableListOf<String>()

        // Tool: get_student_profile
        if (matchesProfileIntent(lowerMessage)) {
            fetchStudentProfile()?.let { contextParts.add(it) }
        }

        // Tool: get_attendance_summary
        if (matchesAttendanceIntent(lowerMessage)) {
            fetchAttendanceSummary()?.let { contextParts.add(it) }
        }

        // Tool: get_course_with_most_absences
        if (matchesMostAbsencesIntent(lowerMessage)) {
            fetchCourseWithMostAbsences()?.let { contextParts.add(it) }
        }

        // Tool: get_nearest_schedule
        if (matchesNearestScheduleIntent(lowerMessage)) {
            fetchNearestSchedule()?.let { contextParts.add(it) }
        }

        // Tool: get_schedule_today / get_schedule_by_date
        if (matchesScheduleIntent(lowerMessage)) {
            fetchScheduleData()?.let { contextParts.add(it) }
        }

        // Tool: get_kelas_list (daftar mata kuliah)
        if (matchesKelasListIntent(lowerMessage)) {
            fetchKelasList()?.let { contextParts.add(it) }
        }

        return contextParts.joinToString("\n\n")
    }

    // ==================== INTENT MATCHERS ====================

    private fun matchesProfileIntent(message: String): Boolean {
        val keywords = listOf(
            "profil", "nama saya", "nim saya", "siapa saya",
            "data saya", "identitas", "info saya", "email saya"
        )
        return keywords.any { message.contains(it) }
    }

    private fun matchesAttendanceIntent(message: String): Boolean {
        val keywords = listOf(
            "rekap", "kehadiran", "presensi", "absensi", "hadir",
            "alpha", "tidak hadir", "persentase kehadiran", "absen",
            "izin", "sakit", "status kehadiran"
        )
        return keywords.any { message.contains(it) }
    }

    private fun matchesMostAbsencesIntent(message: String): Boolean {
        val keywords = listOf(
            "alpha terbanyak", "paling sering absen", "paling banyak alpha",
            "absen terbanyak", "paling sering tidak hadir",
            "mata kuliah paling banyak absen", "matkul alpha"
        )
        return keywords.any { message.contains(it) }
    }

    private fun matchesNearestScheduleIntent(message: String): Boolean {
        val keywords = listOf(
            "jadwal terdekat", "kelas berikutnya", "kelas selanjutnya",
            "agenda terdekat", "jadwal selanjutnya", "kelas mendatang"
        )
        return keywords.any { message.contains(it) }
    }

    private fun matchesScheduleIntent(message: String): Boolean {
        val keywords = listOf(
            "jadwal", "kelas hari ini", "jadwal hari ini", "jadwal kuliah",
            "jadwal minggu ini", "jadwal besok", "hari paling padat",
            "mata kuliah hari"
        )
        return keywords.any { message.contains(it) }
    }

    private fun matchesKelasListIntent(message: String): Boolean {
        val keywords = listOf(
            "daftar mata kuliah", "daftar matkul", "mata kuliah saya",
            "matkul apa saja", "kelas apa saja", "berapa sks",
            "dosen", "nama dosen"
        )
        return keywords.any { message.contains(it) }
    }

    // ==================== DATA FETCHERS ====================

    private suspend fun fetchStudentProfile(): String? {
        return try {
            val name = userPreferences.userName.first()
            val nim = userPreferences.userNim.first()
            val email = userPreferences.userEmail.first()

            if (name == null && nim == null) return null

            buildString {
                appendLine("[PROFIL MAHASISWA]")
                name?.let { appendLine("Nama: $it") }
                nim?.let { appendLine("NIM: $it") }
                email?.let { appendLine("Email: $it") }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchAttendanceSummary(): String? {
        return try {
            val summaryList = presensiRepository.getAttendanceSummary().first()
            if (summaryList.isEmpty()) return "[REKAP KEHADIRAN]\nData presensi belum tersedia. Mahasiswa perlu melakukan sinkronisasi data di halaman Rekap."

            buildString {
                appendLine("[REKAP KEHADIRAN]")
                summaryList.forEach { summary ->
                    val persen = ((summary.persentaseKehadiran * 10).toInt() / 10f)
                    appendLine("- ${summary.mataKuliahNama}: " +
                            "Hadir ${summary.totalHadir}/${summary.totalPertemuan} " +
                            "(${persen}%), " +
                            "Alpha: ${summary.totalAlpha}, " +
                            "Izin: ${summary.totalIzin}, " +
                            "Sakit: ${summary.totalSakit}, " +
                            "Status: ${summary.riskLevel.emoji} ${summary.riskLevel.displayName}")
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchCourseWithMostAbsences(): String? {
        return try {
            val summaryList = presensiRepository.getAttendanceSummary().first()
            if (summaryList.isEmpty()) return "[ALPHA TERBANYAK]\nData presensi belum tersedia."

            val worst = summaryList.maxByOrNull { it.totalAlpha }
            if (worst == null || worst.totalAlpha == 0) {
                return "[ALPHA TERBANYAK]\nTidak ada alpha di semua mata kuliah. Bagus!"
            }

            buildString {
                appendLine("[MATA KULIAH DENGAN ALPHA TERBANYAK]")
                appendLine("Mata Kuliah: ${worst.mataKuliahNama}")
                appendLine("Jumlah Alpha: ${worst.totalAlpha}")
                val persen = ((worst.persentaseKehadiran * 10).toInt() / 10f)
                appendLine("Persentase Kehadiran: ${persen}%")
                appendLine("Status Risiko: ${worst.riskLevel.emoji} ${worst.riskLevel.displayName}")
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchNearestSchedule(): String? {
        return try {
            val today = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date.toString()

            val jadwalList = jadwalRepository.getUpcomingJadwal(today, 3).first()
            if (jadwalList.isEmpty()) return "[JADWAL TERDEKAT]\nTidak ada jadwal/agenda terdekat yang ditemukan."

            buildString {
                appendLine("[JADWAL TERDEKAT]")
                jadwalList.forEach { jadwal ->
                    appendLine("- ${jadwal.judul} | ${jadwal.tanggal} ${jadwal.waktu} | Jenis: ${jadwal.jenis.displayName}")
                    if (jadwal.deskripsi.isNotBlank()) {
                        appendLine("  Deskripsi: ${jadwal.deskripsi}")
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchScheduleData(): String? {
        return try {
            val kelasList = presensiRepository.getKelasList().first()
            val today = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date.toString()
            val jadwalList = jadwalRepository.getUpcomingJadwal(today, 10).first()

            buildString {
                if (kelasList.isNotEmpty()) {
                    appendLine("[JADWAL KULIAH MAHASISWA]")
                    kelasList.forEach { kelas ->
                        appendLine("- ${kelas.namaMk} (${kelas.kodeMk}) | Kelas: ${kelas.namaKelas} | Jadwal: ${kelas.jadwalHari} | Dosen: ${kelas.namaDosenList} | SKS: ${kelas.sksMk}")
                    }
                }
                if (jadwalList.isNotEmpty()) {
                    appendLine()
                    appendLine("[AGENDA PRIBADI MENDATANG]")
                    jadwalList.forEach { jadwal ->
                        appendLine("- ${jadwal.judul} | ${jadwal.tanggal} ${jadwal.waktu} | Jenis: ${jadwal.jenis.displayName}")
                    }
                }
                if (kelasList.isEmpty() && jadwalList.isEmpty()) {
                    appendLine("[JADWAL]\nBelum ada data jadwal kuliah atau agenda pribadi.")
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchKelasList(): String? {
        return try {
            val kelasList = presensiRepository.getKelasList().first()
            if (kelasList.isEmpty()) return "[DAFTAR MATA KULIAH]\nData mata kuliah belum tersedia. Mahasiswa perlu melakukan sinkronisasi data."

            buildString {
                appendLine("[DAFTAR MATA KULIAH AKTIF]")
                kelasList.forEach { kelas ->
                    appendLine("- ${kelas.namaMk} (${kelas.kodeMk})")
                    appendLine("  Kelas: ${kelas.namaKelas} | SKS: ${kelas.sksMk}")
                    appendLine("  Dosen: ${kelas.namaDosenList}")
                    appendLine("  Jadwal: ${kelas.jadwalHari}")
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
