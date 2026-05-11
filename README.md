# KelazZz: Aplikasi Presensi Mahasiswa ITERA dengan Integrasi AI dan Optimasi Presensi


> Aplikasi mobile multiplatform yang menghadirkan pengalaman presensi dan layanan akademik yang lebih cepat, cerdas, dan mudah digunakan bagi mahasiswa Institut Teknologi Sumatera.

---

## 👥 Tim Pengembang

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| Muhammad Bintang Al Fasya | 123140098 | [@MuhammadBintangAl-Fasya](https://github.com/MuhammadBintangAl-Fasya) | Developer |
| Rifael Eurico Sitorus     | 123140077 | [@eltoruz](https://github.com/eltoruz) | Developer |

**Mata Kuliah:** Pengembangan Aplikasi Mobile (IF25-22017)  
**Program Studi:** Teknik Informatika — Institut Teknologi Sumatera  
**Tahun Akademik:** Genap 2025/2026

---

## 📖 Latar Belakang

Aplikasi Pocket ITERA adalah portal akademik resmi yang digunakan oleh seluruh mahasiswa Institut Teknologi Sumatera. Namun dalam penggunaannya sehari-hari, mahasiswa menghadapi sejumlah kendala yang menghambat produktivitas — terutama pada fitur presensi yang paling sering digunakan.

Proses presensi di Pocket ITERA mengharuskan mahasiswa memindai QR code yang ditampilkan dosen, namun fitur scan di dalam aplikasi kerap gagal bekerja meskipun QR code sudah terbaca oleh kamera biasa. Selain itu, mahasiswa juga kesulitan memantau kondisi kehadiran secara proaktif, tidak memiliki akses cepat ke informasi aturan akademik, dan tidak ada fitur untuk mencatat jadwal dan pengingat kegiatan akademik secara mandiri.

**KelazZz** KelazZz untuk menjawab semua permasalahan ini dengan pendekatan yang lebih fleksibel, andal, dan cerdas.

---

## 📱 Deskripsi Aplikasi

KelazZz adalah aplikasi mobile berbasis Kotlin Multiplatform (KMP) dan Compose Multiplatform yang berfungsi sebagai antarmuka alternatif dan pelengkap untuk layanan akademik Pocket ITERA. Aplikasi ini terhubung langsung ke API resmi Pocket ITERA sehingga seluruh data yang ditampilkan — termasuk rekap kehadiran dan data perkuliahan — adalah data nyata dan real-time.

Fokus utama KelazZz adalah mempersingkat alur presensi dan memberikan fleksibilitas yang tidak tersedia di aplikasi bawaan. Mahasiswa dapat melakukan presensi dengan dua cara: memindai QR code seperti biasa (namun dengan pemrosesan yang lebih andal), atau cukup memasukkan token presensi secara manual ke dalam form — solusi praktis untuk situasi di mana QR scan bawaan tidak bekerja.

KelazZz juga mengintegrasikan kecerdasan buatan berbasis Google Gemini API untuk dua kegunaan: memberikan peringatan dini otomatis ketika kehadiran mahasiswa mendekati batas minimum, serta menjadi asisten akademik yang bisa menjawab pertanyaan seputar aturan dan prosedur kampus. Selain itu, KelazZz menyediakan fitur kalender akademik pribadi yang dapat dikelola sepenuhnya secara offline — mahasiswa dapat membuat, mengedit, dan menghapus jadwal serta pengingat kegiatan akademik yang tersimpan di perangkat menggunakan SQLDelight.

---

## ✨ Fitur

### Fitur Minimum (Wajib)

- **Login Akun ITERA** — Autentikasi menggunakan kredensial Pocket ITERA, session disimpan aman dengan DataStore sehingga tidak perlu login ulang setiap membuka aplikasi
- **Dashboard Akademik** — Ringkasan mata kuliah aktif, status kehadiran terkini, notifikasi peringatan AI, dan agenda terdekat dari kalender dalam satu tampilan
- **Rekap Daftar Presensi** — Lihat detail kehadiran per mata kuliah lengkap dengan persentase hadir dan riwayat per pertemuan
- **Presensi via QR Code Scan** — Scan QR code yang ditampilkan dosen, token otomatis diekstrak dan presensi langsung terkirim ke API
- **Presensi via Input Token Manual** ⭐ — Masukkan token secara manual ke dalam form, solusi utama ketika fitur QR scan bawaan Pocket ITERA gagal bekerja
- **Kalender Akademik Pribadi** — Buat, edit, dan hapus jadwal atau pengingat kegiatan akademik secara offline, data tersimpan lokal di perangkat dengan SQLDelight

### Fitur AI (Bonus +10%)

- **AI Early Warning Kehadiran** — Setiap kali data presensi dimuat, AI secara otomatis menganalisis persentase kehadiran per mata kuliah dan memberikan peringatan dini yang dipersonalisasi jika ada mata kuliah yang berisiko tidak memenuhi syarat minimum kehadiran. Contoh: *"Kehadiran kamu di Kalkulus Lanjut tinggal 68%. Kalau absen 2 kali lagi, kamu tidak memenuhi syarat UAS."*
- **AI Chatbot Asisten Akademik** — Tanya aturan dan prosedur akademik ITERA dalam bahasa sehari-hari dan dijawab langsung oleh AI berbasis Gemini API yang dilengkapi knowledge base informasi akademik ITERA. Contoh pertanyaan: *"Berapa minimal kehadiran untuk ikut UAS?"*, *"Gimana cara ajukan cuti akademik?"*

### Fitur Bonus Lainnya

- **Offline First** (+5%) — Data rekap presensi di-cache dengan SQLDelight, bisa diakses tanpa koneksi internet
- **Dark Mode** (+5%) — Support tema gelap dan terang menggunakan Material Design 3 dynamic color
- **CI/CD** (+5%) — Build dan test otomatis setiap push dan pull request dengan GitHub Actions

---

## 🗄️ Penggunaan SQLDelight

SQLDelight digunakan untuk dua kebutuhan penyimpanan data lokal di KelazZz:

| Tabel | Kegunaan |
|-------|----------|
| `PresensiEntity` | Cache rekap presensi dari API — mendukung fitur Offline First |
| `JadwalEntity` | Menyimpan jadwal dan pengingat akademik yang dibuat user — mendukung fitur Kalender Akademik Pribadi |

Dengan pemisahan ini, fitur presensi tetap bisa diakses saat offline, dan seluruh data kalender pribadi mahasiswa tersimpan permanen di perangkat tanpa bergantung pada koneksi internet maupun server eksternal.

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Framework | Kotlin Multiplatform (KMP) |
| UI | Compose Multiplatform + Material Design 3 |
| Arsitektur | MVVM + Clean Architecture |
| Async | Coroutines + Flow + StateFlow |
| Networking | Ktor Client + Kotlinx Serialization |
| Database Lokal | SQLDelight (cache presensi + kalender) |
| Preferences | DataStore Preferences (session token) |
| Dependency Injection | Koin |
| AI | Google Gemini API |
| Testing | kotlin.test + MockK + Turbine + Compose Test |
| CI/CD | GitHub Actions |

---

## 🏗️ Arsitektur

KelazZz menggunakan **Clean Architecture** dengan tiga lapisan utama yang saling terpisah:

```
Presentation Layer
├── Screens (LoginScreen, HomeScreen, PresensiScreen, QRScanScreen, AIScreen, KalenderScreen)
├── ViewModels (per screen)
└── UI State (Sealed classes)
        ↓
Domain Layer
├── Use Cases (LoginUseCase, GetPresensiUseCase, SubmitPresensiUseCase,
│             AnalyzeAttendanceUseCase, GetJadwalUseCase, SaveJadwalUseCase)
├── Repository Interfaces
└── Domain Models
        ↓
Data Layer
├── Remote  (Ktor — Pocket ITERA API + Gemini API)
├── Local   (SQLDelight — cache presensi + kalender jadwal)
│           (DataStore  — session token login)
└── Repository Implementations
```

**Dependency Rule:** Setiap lapisan hanya bergantung ke lapisan di bawahnya. Domain tidak mengetahui tentang Data atau Presentation.

---

## 📁 Struktur Proyek

```
composeApp/
└── src/
    └── commonMain/
        └── kotlin/com/kelazZz/app/
            ├── App.kt
            ├── di/
            │   ├── AppModule.kt
            │   ├── DataModule.kt
            │   └── ViewModelModule.kt
            ├── data/
            │   ├── local/
            │   │   ├── presensi/       # SQLDelight — cache presensi
            │   │   ├── kalender/       # SQLDelight — jadwal akademik
            │   │   └── datastore/      # DataStore — session token
            │   ├── remote/
            │   │   ├── pocket/         # Ktor — Pocket ITERA API
            │   │   └── gemini/         # Ktor — Gemini API
            │   ├── repository/
            │   └── model/
            ├── domain/
            │   ├── model/
            │   ├── repository/
            │   └── usecase/
            └── presentation/
                ├── navigation/
                ├── theme/
                ├── components/
                └── screens/
                    ├── login/
                    ├── home/
                    ├── presensi/
                    ├── qrscan/
                    ├── ai/
                    └── kalender/
```

---

## 🖥️ Screens (6 Screen)

| # | Screen | Deskripsi |
|---|--------|-----------|
| 1 | Login | Form login dengan kredensial ITERA |
| 2 | Home / Dashboard | Ringkasan akademik + AI early warning + agenda terdekat |
| 3 | Daftar Presensi | Rekap kehadiran per mata kuliah dengan persentase |
| 4 | Presensi | QR scan + input token manual |
| 5 | Kalender Akademik | Buat dan kelola jadwal pribadi secara offline |
| 6 | AI Asisten | Chatbot akademik berbasis Gemini API |

---

## 🚀 Cara Menjalankan

### Prasyarat

- Android Studio Hedgehog atau lebih baru
- JDK 17
- Android SDK minimum API 24
- Kotlin 1.9+

---


## 📅 Sprint Plan

| Sprint | Minggu | Target |
|--------|--------|--------|
| Sprint 1 | W11 | Planning, setup repo, CI/CD, arsitektur |
| Sprint 2 | W12 | Login, navigasi, home screen, DataStore |
| Sprint 3 | W13 | Daftar presensi, QR scan, manual token, SQLDelight cache |
| Sprint 4 | W14 | Kalender akademik, Gemini AI, dark mode, polish |
| Sprint 5 | W15 | Testing, bug fix, persiapan demo |
| **UAS** | W16 | **Final Demo Day** |

---

## 📋 Status Sprint

- [x] Sprint 1 — Planning & Setup
- [ ] Sprint 2 — Auth & Core Navigation
- [ ] Sprint 3 — Fitur Presensi
- [ ] Sprint 4 — Kalender + AI Integration & Polish
- [ ] Sprint 5 — Testing & Final Prep

---

## 📄 Lisensi

Project ini dikembangkan untuk keperluan akademik mata kuliah Pengembangan Aplikasi Mobile, Institut Teknologi Sumatera.
