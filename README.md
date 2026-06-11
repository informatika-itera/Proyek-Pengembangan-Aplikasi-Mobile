<img width="90" align="left" style="margin-right: 20px; margin-bottom: 10px;" src="https://github.com/user-attachments/assets/59abbfe7-2157-4460-9e5f-b55f5d52ae5c" />


# Pantau Jompo
### Aplikasi Pelacakan Olahraga dan Nutrisi Harian

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-000000?style=for-the-badge&logo=android&logoColor=white)
![CI](https://github.com/EL-graha26/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg?branch=projeck/123140063-123140200-PantauJompo)

---

**Pantau Jompo** adalah aplikasi Android berbasis Kotlin Multiplatform yang dirancang untuk membantu pengguna melacak aktivitas olahraga dan mengatur asupan nutrisi. Aplikasi ini dilengkapi dengan integrasi GPS untuk pencatatan rute serta fitur *scanner* makanan berbasis Gemini AI untuk menghitung estimasi kalori secara otomatis.

---

## 👥 Tim Pengembang

Proyek ini dikerjakan oleh mahasiswa Institut Teknologi Sumatera:

| Foto | Nama | NIM | Peran |
| :---: | :--- | :---: | :--- |
| 🧑‍💻 | **Pradana Figo Ariasya** | `123140063` | Android Developer |
| 🧑‍💻 | **Muhammad Piela Nugraha** | `123140200` | Android Developer |

---

## ✨ Fitur Utama

- 📍 **Pelacakan Olahraga (GPS)** — Mencatat rute lari, jalan kaki, atau bersepeda menggunakan Google Maps API. Menghitung jarak tempuh, durasi, dan kalori yang terbakar.
- 📸 **Scanner Nutrisi AI** — Mengidentifikasi makanan dari foto kamera dan menghitung estimasi kalori beserta kandungan gizinya melalui integrasi Gemini AI.
- 📝 **Pencatatan Riwayat Kesehatan** — Sistem CRUD (*Create, Read, Update, Delete*) untuk mencatat riwayat olahraga, asupan makanan harian, dan kondisi kesehatan.
- 📰 **Portal Berita Kesehatan** — Menampilkan artikel kesehatan terkini dengan fitur ringkasan otomatis (*AI Summarizer*) agar lebih mudah dibaca.
- 👤 **Kalkulator BMI** — Menghitung Indeks Massa Tubuh dan memberikan rekomendasi target asupan kalori harian.
- 🌗 **Tema Gelap (Dark Mode)** — Tampilan antarmuka modern dengan dukungan *dark mode* penuh untuk kenyamanan mata.

---

## 📱 Tampilan Aplikasi

| Beranda (Dashboard) | Tracking Aktivitas | Scanner Nutrisi | Baca Artikel |
| :---: | :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/320bc1d2-0f43-46e9-ac9c-9996c9f21733" width="200" /> | <img src="https://github.com/user-attachments/assets/e3d1b9d3-3fe2-42ef-9eb3-c974fcbdd5c2" width="200" /> | <img src="https://github.com/user-attachments/assets/6756c232-9863-4ed7-82b4-b8dd5738fb65" width="200" /> | <img src="https://github.com/user-attachments/assets/449f53f5-f335-401b-896a-9d598aeef83e" width="200" /> |
| Ringkasan kalori, dan akses cepat ke semua fitur utama. | Peta rute olahraga *real-time* beserta statistik durasi dan jarak. | Kamera untuk memindai makanan dan melihat rincian gizi. | Tampilan artikel kesehatan dengan ringkasan poin-poin penting. |

---

## 🎬 Video Presentasi Aplikasi

Klik gambar di bawah untuk menonton demonstrasi lengkap aplikasi Pantau Jompo di YouTube:

[![Video Presentasi Pantau Jompo](https://img.youtube.com/vi/uyYoP1yeyZM/maxresdefault.jpg)](https://youtu.be/uyYoP1yeyZM)

---

## ⚙️ Tech Stack

<details>
<summary><b>Lihat daftar teknologi lengkap</b></summary>
<br>

| Komponen | Teknologi yang Digunakan |
|----------|--------------------------|
| **Bahasa Utama** | Kotlin |
| **UI Framework** | Compose Multiplatform (Material Design 3) |
| **Pola Arsitektur** | MVVM (Model-View-ViewModel) + Repository |
| **Database Lokal** | Room (SQLite) / SQLDelight |
| **Asynchronous** | Kotlin Coroutines & Flow |
| **Network Client** | Ktor Client |
| **Image Loader** | Coil |
| **Integrasi AI** | Gemini API |
| **Lokasi & Maps** | Google Maps SDK & FusedLocationProvider |
| **Injection (DI)** | Koin |

</details>

---

## 🧬 Arsitektur & Struktur Direktori

Proyek ini disusun menggunakan prinsip **Clean Architecture** berlapis untuk memudahkan pengembangan, perbaikan *bug*, dan keterbacaan kode.

### 1. Pola Arsitektur

Proyek ini mengadopsi pola **MVVM (Model-View-ViewModel)** yang dikombinasikan dengan **Repository Pattern** untuk memisahkan antara tampilan (UI), logika bisnis, dan mekanisme pengambilan data.

- **UI (View)** — Hanya bertugas merender tampilan dan menangkap input dari pengguna.
- **ViewModel** — Menyimpan *state* sementara dan menjadi jembatan antara UI dengan logika aplikasi.
- **Repository** — Menentukan sumber data, apakah dari penyimpanan lokal atau dari Remote API.

### 2. Struktur Folder Utama

```text
composeApp/src/
├── androidMain/               # 🤖 Kode khusus ekosistem Android
│   ├── MainActivity.kt        # Entry Point aplikasi
│   └── permissions/           # Penanganan izin sistem (Lokasi, Kamera)
│
├── commonMain/.../pantaujompo/# 🧬 Kode utama lintas platform
│   ├── core/                  # Konfigurasi pondasi (Koin DI, Ktor, Tema UI)
│   ├── data/                  # Remote API & Database Lokal (Room)
│   ├── domain/                # Model data murni & Use Cases
│   ├── presentation/          # Komponen UI, Layar Utama, dan ViewModels
│   └── utils/                 # Fungsi pembantu (Format waktu, teks, dll)
│
└── iosMain/                   # 🍏 Persiapan struktur dasar untuk iOS
```

---

## 🚀 Cara Menjalankan Proyek

### 1. Prasyarat Sistem

- **Android Studio** (Disarankan versi Hedgehog 2023.1.1 atau terbaru)
- **JDK 17**
- **Android SDK** API 26+

### 2. Instalasi

*Clone* repositori ini, lalu buka foldernya di Android Studio:

```bash
git clone https://github.com/EL-graha26/Proyek-Pengembangan-Aplikasi-Mobile.git
```

Tunggu hingga proses *Gradle Sync* selesai.

### 3. Konfigurasi API Key ⚠️

Aplikasi ini membutuhkan akses **Gemini API** agar fitur *scanner* makanan dan ringkasan berita bisa berjalan.

1. Buat file baru bernama `local.properties` di folder paling luar proyek (sejajar dengan `build.gradle.kts`).
2. Tambahkan baris berikut:

```properties
# Dapatkan key dari https://aistudio.google.com/
GEMINI_API_KEY=masukkan_api_key_gemini_anda_di_sini
```

### 4. Build dan Jalankan

Pilih emulator atau *device* Android fisik yang terhubung, lalu klik tombol **▶ Run** di Android Studio.

---

## 🎯 Status Timeline Pengembangan (Sprints)

### 🏁 SPRINT 1 — Planning & Setup ✅

Fase inisialisasi arsitektur dan penyusunan dokumen manajemen proyek.

- [x] Form kelompok, tentukan role (lead, dev, QA)
- [x] Pilih project idea, approval dosen
- [x] Define requirements (fitur minimum dan bonus)
- [x] Design architecture (diagram & struktur folder)
- [x] Create & setup repository (GitHub + collaborators)
- [x] Setup CI/CD (GitHub Actions workflow)
- [x] Create project doc (README dengan requirements dan timeline)

---

### 🧱 SPRINT 2 — Core UI & Data Layer ✅

Fokus pada struktur antarmuka utama, navigasi, dan fungsi simpan-baca data lokal.

- [x] Minimal 3 working screens (Home, Detail, Add/Edit)
- [x] Navigation dengan argument passing antar layar
- [x] Data layer dengan Repository pattern
- [x] Local storage (Room / SQLDelight / DataStore)
- [x] Basic CRUD operations berjalan penuh
- [x] UI States (Loading, Success, Error) tertangani
- [x] Semua fitur accessible dari app

> **🎥 Demo Sprint 1 & 2:**

https://github.com/user-attachments/assets/7df9fc3a-b7a5-49a0-a2ce-75d2969fba29

---

### 🚀 SPRINT 3 — Advanced Features & API ✅

Fokus pada integrasi Gemini API, mode *offline*, dan polesan estetika *Glassmorphism*.

- [x] **P0** — Search/Filter: Pencarian fungsional dalam app
- [x] **P0** — API Integration: Gemini REST API (Scanner Nutrisi & AI Chat)
- [x] **P1** — Offline Support: App tetap usable tanpa internet (Cached DB)
- [x] **P1** — Additional Screen: Settings/Profile fungsional (BMI Setup, ganti bahasa)
- [x] **P2** — UI Polish: Modern Futuristic Glassmorphism styling
- [x] **P2** — Bonus Features: Dark Mode, Animasi, Multi-language

> **🎥 Demo Sprint 3:**

https://github.com/user-attachments/assets/dfc5d28c-9206-48f2-a532-eb81cb95ffdc

---

### 🧪 SPRINT 4 — Polish, Testing & Bug Fixing ✅

Fokus pada kestabilan aplikasi, Unit Test, UI Test, dan resolusi semua kendala.

- [x] **P0** — Fix All Known Bugs: No crash, no broken features
- [x] **P0** — UI Polish: Consistent spacing, typography, colors
- [x] **P0** — Unit Tests: Repository + ViewModel tests (10+ total)
- [x] **P1** — UI Tests: Critical user journeys (3+)
- [x] **P1** — Edge Cases: Empty states, errors, loading tertangani
- [x] **P2** — Performance: No visible lag or jank

> **🎥 Demo Sprint 4:**

https://github.com/user-attachments/assets/d7a326fa-a017-42d5-87df-d0e6f33a7960

---

## 🔍 Panduan Pengujian Otomatis

### 1. ⚙️ Unit Test

Mengecek kebenaran logika fungsi komputasi di dalam layer `ViewModel` dan `Repository`.

```bash
./gradlew :composeApp:testDebugUnitTest
```

Atau buka file `*Test.kt` di direktori `androidUnitTest` di Android Studio, lalu klik **▶** di margin kiri.

### 2. 📱 UI Test

Menjalankan simulasi interaksi pengguna. Membutuhkan Emulator Android yang aktif.

```bash
./gradlew :composeApp:connectedDebugAndroidTest
```

Atau buka skrip uji di `androidInstrumentedTest` dan klik **▶** untuk menjalankan robot di Emulator.

---

## 🐛 Riwayat Bug & Perbaikan

### 🚨 Issue #1 — *Crash* saat Eksekusi UI Test

| | Detail |
|---|---|
| **Deskripsi** | Test Runner langsung berhenti dengan `RuntimeException` pada emulator Android 11+. Logcat: `Intent in process com.example.pantaujompo resolved to different process...` |
| **Root Cause** | Modul `ui-test-manifest` terdeklar di *scope* yang keliru pada Gradle, menyebabkan *Process Mismatch* di OS Android. |
| **Solusi ✅** | Memindahkan deklarasi `androidx.compose.ui:ui-test-manifest` ke scope `debugImplementation` pada *build script*, sehingga lapisan testing terintegrasi dengan benar. |

---

<div align="center">
  <b>Tugas Besar Pengembangan Aplikasi Mobile — RB</b><br>
  Institut Teknologi Sumatera · 2026
</div>
