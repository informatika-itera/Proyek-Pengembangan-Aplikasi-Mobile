package com.kelazzz.app.testutil

import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.domain.model.Kelas
import com.kelazzz.app.domain.model.User
import kotlinx.datetime.Clock

fun sampleUser(
    nama: String = "Rifael",
    nim: String = "123140077"
) = User(
    userId = "u-1",
    nama = nama,
    nim = nim,
    email = "$nim@student.itera.ac.id",
    unit = "Teknik Informatika",
    level = "mahasiswa",
    photoUrl = "",
    token = "token"
)

fun sampleJadwal(
    id: Long = 1L,
    judul: String = "Pemrograman Mobile",
    tanggal: String = "2026-06-10",
    waktu: String = "08:00-09:40",
    jenis: JenisJadwal = JenisJadwal.REMINDER
) = Jadwal(
    id = id,
    judul = judul,
    deskripsi = "Ruang GD 401",
    tanggal = tanggal,
    waktu = waktu,
    jenis = jenis,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

fun sampleSummary(
    mataKuliahId: String = "IF001",
    mataKuliahNama: String = "Basis Data",
    totalPertemuan: Int = 10,
    totalHadir: Int = 8,
    totalAlpha: Int = 2
) = AttendanceSummary(
    mataKuliahId = mataKuliahId,
    mataKuliahNama = mataKuliahNama,
    totalPertemuan = totalPertemuan,
    totalHadir = totalHadir,
    totalAlpha = totalAlpha,
    totalIzin = 0,
    totalSakit = 0
)

fun sampleKelas(
    kodeKelas: String = "KLS-1",
    namaMk: String = "Basis Data"
) = Kelas(
    nomorMk = "1",
    kodeMk = "IF1234",
    kodeKelas = kodeKelas,
    namaKelas = "RB",
    mode = "offline",
    namaMk = namaMk,
    sksMk = "3",
    namaDosenList = "Dosen Pengampu",
    jadwalHari = "Senin, 08:00"
)
