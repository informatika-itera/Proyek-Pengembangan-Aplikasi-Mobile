# KelazZz — Aplikasi Presensi Mahasiswa ITERA

![CI](https://github.com/MuhammadBintangAl-Fasya/KelazZz/actions/workflows/ci.yml/badge.svg)

> Aplikasi mobile multiplatform yang menghadirkan pengalaman presensi dan layanan akademik yang lebih cepat, cerdas, dan mudah digunakan bagi mahasiswa Institut Teknologi Sumatera.

---

## 👥 Tim Pengembang

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| Muhammad Bintang Al Fasya | 123140098 | [@MuhammadBintangAl-Fasya](https://github.com/MuhammadBintangAl-Fasya) | Mobile Developer |
| Rifael Eurico Sitorus | 123140077 | [@eltoruz](https://github.com/eltoruz) | Mobile Developer |

**Mata Kuliah:** Pengembangan Aplikasi Mobile (IF25-22017)  
**Program Studi:** Teknik Informatika — Institut Teknologi Sumatera  
**Tahun Akademik:** Genap 2025/2026

---

## 📖 Latar Belakang

Aplikasi Pocket ITERA adalah portal akademik resmi yang digunakan oleh seluruh mahasiswa Institut Teknologi Sumatera. Namun dalam penggunaannya sehari-hari, mahasiswa menghadapi sejumlah kendala yang menghambat produktivitas — terutama pada fitur presensi yang paling sering digunakan.

Proses presensi di Pocket ITERA mengharuskan mahasiswa memindai QR code yang ditampilkan dosen, namun fitur scan di dalam aplikasi kerap gagal bekerja meskipun QR code sudah terbaca oleh kamera biasa. Selain itu, mahasiswa juga kesulitan memantau kondisi kehadiran secara proaktif, tidak memiliki akses cepat ke informasi aturan akademik, serta tidak ada fitur untuk mencatat jadwal dan pengingat kegiatan akademik secara mandiri.

**KelazZz** hadir untuk menjawab semua permasalahan ini dengan pendekatan yang lebih fleksibel, andal, dan cerdas.

---

## 📱 Deskripsi Aplikasi

KelazZz adalah aplikasi mobile berbasis **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform** yang berfungsi sebagai antarmuka alternatif dan pelengkap untuk layanan akademik Pocket ITERA. Aplikasi ini terhubung langsung ke API resmi Pocket ITERA sehingga seluruh data yang ditampilkan — termasuk rekap kehadiran dan data perkuliahan — adalah data nyata dan real-time.

Fokus utama KelazZz adalah:
- **Mempersingkat dan mengandal-kan alur presensi** — mahasiswa dapat memindai QR code (dengan pemrosesan yang lebih andal menggunakan ML Kit) atau cukup memasukkan token presensi secara manual ke dalam form
- **Smart Attendance Analytics** — dashboard analitik kehadiran per mata kuliah dengan indikator risiko dan prediksi status aman/tidak aman
- **AI-powered features** — peringatan dini otomatis dan asisten akademik berbasis Google Gemini API
- **Offline-first** — data rekap presensi dan kalender akademik pribadi tetap dapat diakses tanpa koneksi internet

> ⚠️ **Disclaimer**
> Aplikasi ini dibuat untuk tujuan akademik dan pembelajaran. KelazZz tetap menggunakan autentikasi resmi milik pengguna dan tidak melakukan bypass keamanan maupun modifikasi sistem kampus.

---

## ✨ Fitur

### 🔐 Autentikasi & Sesi
- Login menggunakan kredensial Pocket ITERA (akun resmi mahasiswa)
- JWT/Bearer token authentication
- Session disimpan aman dengan **DataStore** — tidak perlu login ulang setiap membuka aplikasi
- Auto-login persistence

---

### 📷 Presensi via QR Code Scan *(Fitur Wajib)*
Menggunakan **CameraX + ML Kit Barcode Scanner** untuk pemindaian yang lebih cepat dan andal.

**Alur presensi QR:**
```
Scan QR → ML Kit Detect QR → Extract Token → Validasi Token → Hit API Presensi → Sukses
```

Fitur scanner:
- Fast QR detection dengan continuous autofocus
- Low-light support
- Duplicate scan prevention
- Auto submit attendance

---

### ⌨️ Presensi via Input Token Manual ⭐ *(Fitur Wajib)*
Solusi utama ketika fitur QR scan bawaan Pocket ITERA gagal bekerja.

```
Kamera bawaan → Scan QR → Salin token → Tempel ke form KelazZz → Submit presensi
```

Token divalidasi sebelum dikirim ke API untuk mencegah permintaan yang tidak valid.

---

### 📊 Dashboard & Rekap Presensi *(Fitur Wajib)*
Ringkasan akademik dalam satu tampilan:
- Daftar mata kuliah aktif beserta persentase kehadiran
- Riwayat kehadiran per pertemuan per mata kuliah
- Indikator risiko dan prediksi status aman/tidak aman, contoh:

  ```
  Kehadiran Basis Data: 72% — ⚠️ Warning
  Minimal hadir 3x lagi untuk aman
  ```

- Notifikasi peringatan AI dan agenda terdekat dari kalender

---

### 📅 Kalender Akademik Pribadi *(Fitur Wajib)*
Kelola jadwal dan pengingat kegiatan akademik sepenuhnya secara offline.
- Buat, edit, dan hapus jadwal atau pengingat
- Data tersimpan permanen di perangkat menggunakan **SQLDelight**
- Tidak bergantung pada koneksi internet maupun server eksternal

---

### 🤖 AI Early Warning Kehadiran *(Bonus +10%)*
Setiap kali data presensi dimuat, AI secara otomatis menganalisis persentase kehadiran per mata kuliah dan memberikan peringatan dini yang dipersonalisasi.

Contoh peringatan:
> *"Kehadiran kamu di Kalkulus Lanjut tinggal 68%. Kalau absen 2 kali lagi, kamu tidak memenuhi syarat UAS."*

---

### 💬 AI Chatbot Asisten Akademik *(Bonus +10%)*
Tanya aturan dan prosedur akademik ITERA dalam bahasa sehari-hari, dijawab langsung oleh AI berbasis **Gemini API** yang dilengkapi knowledge base informasi akademik ITERA.

Contoh pertanyaan:
- *"Kelas saya berikutnya apa?"*
- *"Berapa minimal kehadiran untuk ikut UAS?"*
- *"Kalau alpha sekali lagi apakah masih aman?"*
- *"Bagaimana cara mengajukan cuti akademik?"*
- *"Hari paling padat minggu ini apa?"*

---

### 🔔 Smart Notification *(Bonus)*
Push notification pintar berbasis **Firebase Cloud Messaging**:
- **Class Reminder:** *"Kelas Pemrograman Mobile dimulai 15 menit lagi"*
- **Attendance Warning:** *"Presensi Basis Data tinggal 5% dari batas minimum"*

---

### 🌙 Dark Mode *(Bonus +5%)*
Support tema gelap dan terang menggunakan **Material Design 3 dynamic color**.

---

### 📶 Offline First *(Bonus +5%)*
Data rekap presensi di-cache dengan **SQLDelight**, dapat diakses tanpa koneksi internet. Sinkronisasi otomatis saat kembali online.

---

### ⚙️ CI/CD *(Bonus +5%)*
Build dan test otomatis setiap push dan pull request menggunakan **GitHub Actions**.

---

## 🗄️ Penggunaan SQLDelight

SQLDelight digunakan untuk dua kebutuhan penyimpanan data lokal:

| Tabel | Kegunaan |
|-------|----------|
| `PresensiEntity` | Cache rekap presensi dari API — mendukung fitur Offline First |
| `JadwalEntity` | Menyimpan jadwal dan pengingat akademik yang dibuat user — mendukung Kalender Akademik Pribadi |

Dengan pemisahan ini, fitur presensi tetap dapat diakses saat offline, dan seluruh data kalender pribadi mahasiswa tersimpan permanen di perangkat tanpa bergantung pada koneksi internet maupun server eksternal.

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Framework | Kotlin Multiplatform (KMP) |
| UI | Compose Multiplatform + Material Design 3 |
| Arsitektur | Clean Architecture + MVVM |
| Async | Coroutines + Flow + StateFlow |
| Networking | Ktor Client + Kotlinx Serialization |
| QR Scanner | CameraX + ML Kit Barcode Scanner |
| Database Lokal | SQLDelight (cache presensi + kalender) |
| Preferences | DataStore Preferences (session token) |
| Dependency Injection | Koin |
| AI | Google Gemini API |
| Notifications | Firebase Cloud Messaging |
| Testing | kotlin.test + MockK + Turbine + Compose Test |
| CI/CD | GitHub Actions |

---

## 🏗️ Arsitektur

KelazZz menggunakan **Clean Architecture** dengan tiga lapisan utama yang saling terpisah:

```
┌──────────────────────────────────────────────────┐
│              PRESENTATION LAYER                  │
│  Screens · ViewModels · UI State (Sealed)        │
└─────────────────────┬────────────────────────────┘
                      │
┌─────────────────────▼────────────────────────────┐
│                DOMAIN LAYER                      │
│  Use Cases · Repository Interfaces · Models      │
└─────────────────────┬────────────────────────────┘
                      │
┌─────────────────────▼────────────────────────────┐
│                 DATA LAYER                       │
│  Remote (Ktor) · Local (SQLDelight · DataStore)  │
│  Repository Implementations                      │
└──────────────────────────────────────────────────┘
```

**Use Cases yang tersedia:**
`LoginUseCase`, `GetPresensiUseCase`, `SubmitPresensiUseCase`, `AnalyzeAttendanceUseCase`, `GetJadwalUseCase`, `SaveJadwalUseCase`

**Dependency Rule:** Setiap lapisan hanya bergantung ke lapisan di bawahnya. Domain tidak mengetahui tentang Data atau Presentation.

---

## 🖥️ Screens (7 Screen)

| # | Screen | Deskripsi |
|---|--------|-----------|
| 1 | **Login** | Form login dengan kredensial ITERA |
| 2 | **Home / Dashboard** | Ringkasan akademik + AI early warning + agenda terdekat |
| 3 | **Daftar Presensi** | Rekap kehadiran per mata kuliah + analytics (persentase, heatmap, risiko) |
| 4 | **Presensi** | QR scan (ML Kit) + input token manual |
| 5 | **Kalender Akademik** | Buat dan kelola jadwal pribadi secara offline |
| 6 | **AI Asisten** | Chatbot akademik berbasis Gemini API |
| 7 | **Notifikasi** | Pusat notifikasi class reminder dan attendance warning |

---

## 📁 Struktur Proyek

```
composeApp/
└── src/
    ├── commonMain/
    │   └── kotlin/com/kelazZz/app/
    │       ├── App.kt
    │       ├── di/
    │       │   ├── AppModule.kt
    │       │   ├── DataModule.kt
    │       │   └── ViewModelModule.kt
    │       ├── data/
    │       │   ├── local/
    │       │   │   ├── presensi/       # SQLDelight — cache presensi
    │       │   │   ├── kalender/       # SQLDelight — jadwal akademik
    │       │   │   └── datastore/      # DataStore — session token
    │       │   ├── remote/
    │       │   │   ├── pocket/         # Ktor — Pocket ITERA API
    │       │   │   └── gemini/         # Ktor — Gemini API
    │       │   ├── repository/
    │       │   └── model/
    │       ├── domain/
    │       │   ├── model/
    │       │   ├── repository/
    │       │   └── usecase/
    │       └── presentation/
    │           ├── navigation/
    │           ├── theme/
    │           ├── components/
    │           └── screens/
    │               ├── login/
    │               ├── home/
    │               ├── presensi/
    │               ├── qrscan/
    │               ├── ai/
    │               ├── kalender/
    │               └── notification/
    ├── androidMain/
    │   ├── scanner/
    │   ├── notification/       # FCM
    │   └── biometrics/
    └── commonMain/sqldelight/
```

---

## 🛡️ Keamanan

Aplikasi menerapkan:
- Secure token handling dengan DataStore Preferences
- Session persistence yang aman
- Request interceptor dan token validation
- Duplicate request prevention pada scanner

KelazZz **tidak**:
- Mem-bypass autentikasi kampus
- Memodifikasi sistem kampus
- Mengakses akun pengguna lain
- Melakukan abuse terhadap endpoint API

---

## 🚀 Cara Menjalankan

### Prasyarat

- Android Studio Ladybug atau lebih baru
- JDK 17+
- Android SDK minimum API 24 (target API 34)
- Kotlin 1.9+

### Clone Repository

```bash
git clone https://github.com/MuhammadBintangAl-Fasya/KelazZz.git
cd KelazZz
```

### Setup `local.properties`

```properties
GEMINI_API_KEY=your_api_key_here
```

### Build Project

```bash
./gradlew build
```

### Run Android

```bash
./gradlew :composeApp:installDebug
```

Atau langsung melalui **Run > Run 'composeApp'** di Android Studio.

---

## 📅 Sprint Plan

| Sprint | Minggu | Target |
|--------|--------|--------|
| Sprint 1 | W11 | Planning, setup repo, CI/CD, arsitektur |
| Sprint 2 | W12 | Login, navigasi, home screen, DataStore |
| Sprint 3 | W13 | Daftar presensi, QR scan + ML Kit, manual token, SQLDelight cache |
| Sprint 4 | W14 | Kalender akademik, Gemini AI, FCM, dark mode, polish |
| Sprint 5 | W15 | Testing, bug fix, persiapan demo |
| **UAS** | W16 | **Final Demo Day** |

### Task Assignment per Sprint

#### Sprint 1 — Planning & Setup
| Task | Assignee |
|------|----------|
| Buat repository, push initial project | Rifael |
| Setup Clean Architecture & Koin DI | Rifael |
| Setup CI/CD (GitHub Actions) | Bintang |
| Tulis README & dokumentasi project | Bintang |

#### Sprint 2 — Auth & Core Navigation
| Task | Assignee |
|------|----------|
| Login screen + JWT auth via Pocket ITERA API | Bintang |
| Session persistence dengan DataStore | Bintang |
| Home/Dashboard screen + Bottom Navigation | Rifael |
| Navigation setup (NavHost, Routes, arguments) | Rifael |

#### Sprint 3 — Fitur Presensi
| Task | Assignee |
|------|----------|
| QR Code scanner (CameraX + ML Kit) | Rifael |
| Input token manual + validasi | Rifael |
| Rekap presensi screen + API integration | Bintang |
| SQLDelight cache untuk offline presensi | Bintang |

#### Sprint 4 — Kalender + AI & Polish
| Task | Assignee |
|------|----------|
| Kalender akademik pribadi (CRUD offline) | Bintang |
| AI Early Warning kehadiran (Gemini API) | Rifael |
| AI Chatbot asisten akademik | Rifael |
| Dark mode + UI polish | Bintang |

#### Sprint 5 — Testing & Final Prep
| Task | Assignee |
|------|----------|
| Unit tests (domain & data layer) | Bintang |
| UI tests (Compose Test) | Rifael |
| Bug fixing & performance tuning | Bintang & Rifael |
| Demo preparation & final documentation | Bintang & Rifael |

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
