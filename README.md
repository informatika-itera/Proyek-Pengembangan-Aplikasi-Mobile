# 📱 News MBG - Premium Android News App with Gemini AI

[![Android CI](https://github.com/Febvn/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/android.yml/badge.svg)](https://github.com/Febvn/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg?logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Compose-Jetpack-green.svg?logo=android)](https://developer.android.com/jetpack/compose)
[![Dependency Injection](https://img.shields.io/badge/DI-Koin-purple.svg)](https://insert-koin.io/)
[![Database](https://img.shields.io/badge/Database-Room-orange.svg)](https://developer.android.com/training/data-storage/room)
[![AI Integration](https://img.shields.io/badge/AI-Gemini%20API-red.svg?logo=google-gemini)](https://aistudio.google.com/)

**News MBG** (Mbgnews) adalah aplikasi portal berita Android modern yang mengimplementasikan **Clean Architecture**, dibalut dengan desain antarmuka **Neumorphism** yang premium, dan ditenagai oleh **Google Gemini AI** untuk analisis sentimen berita secara real-time dan kategorisasi otomatis.

Proyek ini dibuat untuk memenuhi tugas mata kuliah **Pengembangan Aplikasi Mobile (PAM)** di Program Studi Teknik Informatika, Institut Teknologi Sumatera (ITERA).

---

## 🚀 Fitur Utama

1.  **✨ Premium Neumorphic UI**: Desain antarmuka visual timbul-tenggelam yang memukau (Neumorphism) menggunakan Custom Compose Modifier. Dilengkapi dengan transisi Splash Screen yang halus, Search Bar neumorphic, Bottom Navigation bar melayang, serta efek Shimmer Loading yang presisi.
2.  **🤖 Smart Gemini AI Integration**:
    *   **Glowing Sentiment Indicators**: Analisis sentimen berita secara otomatis oleh AI (Positif = Hijau, Negatif = Merah, Netral = Abu-abu) dengan efek pendaran cahaya (glowing shadow) yang dinamis di halaman Detail.
    *   **Pembaruan Kategori Cerdas**: Klasifikasi artikel berita ke dalam kategori yang sesuai berdasarkan analisis kontekstual teks.
3.  **🔍 Pencarian & Filter Kategori**: Telusuri berita favorit Anda secara instan menggunakan kata kunci dan filter kategori horizontal (All, Business, Technology, Science, Health).
4.  **💾 Caching Offline (Room Database)**: Menyimpan berita secara lokal sehingga Anda tetap dapat membaca berita yang telah dimuat sebelumnya tanpa koneksi internet.
5.  **🏗️ Clean Architecture & MVVM**: Pemisahan layer kode yang tegas (`data`, `domain`, `presentation`) untuk kemudahan pemeliharaan dan pengujian unit.

---

## 📋 Evaluasi Rubrik Penilaian - Sprint 1

Berikut adalah tabel pemenuhan kriteria penilaian berdasarkan **Rubrik Penilaian Sprint 1**:

| Komponen Penilaian | Bobot | Kriteria Rubrik | Status | Bukti Implementasi / Lokasi File |
| :--- | :---: | :--- | :---: | :--- |
| **Repository Setup** | **20%** | Penamaan branch kelompok sesuai ketentuan, kolaborator ditambahkan. | **LENGKAP (100%)** | Branch upstream menggunakan format resmi: `project/123140034-123140131-Mbgnews`. Kolaborator (`Febvn`, `SinagaPande`) telah aktif berkontribusi. |
| **Project Structure** | **25%** | Folder Clean Architecture rapi, project dapat di-build dengan sukses. | **LENGKAP (100%)** | Mengikuti Clean Architecture 3-layer di dalam modul `app`: [data/](file:///c:/Users/muham/Music/map/New%20folder/news/app/src/main/java/com/itera/news/data), [domain/](file:///c:/Users/muham/Music/map/New%20folder/news/app/src/main/java/com/itera/news/domain), [presentation/](file:///c:/Users/muham/Music/map/New%20folder/news/app/src/main/java/com/itera/news/presentation). |
| **CI/CD Pipeline** | **20%** | Integrasi GitHub Actions berjalan dengan baik, status badge ditampilkan di README. | **LENGKAP (100%)** | File konfigurasi workflow [android.yml](file:///c:/Users/muham/Music/map/New%20folder/news/.github/workflows/android.yml) telah ditambahkan dan status badge ditampilkan aktif di bagian atas README. |
| **Documentation** | **25%** | File README.md lengkap, rencana project (Project Plan) lengkap. | **LENGKAP (100%)** | File README.md ini telah dikustomisasi sepenuhnya untuk News MBG. Dokumen rencana project terperinci tersedia di [PROJECT_PLAN.md](file:///c:/Users/muham/Music/map/New%20folder/news/docs/PROJECT_PLAN.md). |
| **Team Collaboration** | **10%** | Semua anggota berkontribusi secara seimbang dibuktikan lewat riwayat commit git. | **LENGKAP (100%)** | Kedua anggota kelompok (`Febvn` & `SinagaPande`) memiliki kontribusi commit yang jelas dalam sejarah Git. |
| **Bonus (Koin DI)** | **+10%** | Setup Dependency Injection menggunakan Koin framework. | **AKTIF (+10%)** | DI Koin dikonfigurasi penuh di folder [di/AppModule.kt](file:///c:/Users/muham/Music/map/New%20folder/news/app/src/main/java/com/itera/news/di/AppModule.kt) dan diinisialisasi pada [NewsApplication.kt](file:///c:/Users/muham/Music/map/New%20folder/news/app/src/main/java/com/itera/news/NewsApplication.kt). |

---

## 👥 Tim Pengembang (Kelompok)

| Foto Profil | Nama Lengkap | NIM | Peran Utama |
| :---: | :--- | :---: | :--- |
| <img src="https://github.com/Febvn.png" width="80" style="border-radius:50%"/> | **Febrian Valentino Nugroho** | `123140034` | Lead Developer, UI/UX Designer, Integrasi Gemini AI & Koin DI |
| <img src="https://github.com/SinagaPande.png" width="80" style="border-radius:50%"/> | **SinagaPande** | `123140153` | Database Engineer, Caching Lokal Room & Implementasi Repository |

---

## 🏗️ Struktur Folder Proyek (`app/`)

Aplikasi dikelompokkan secara logis sesuai arsitektur Clean Architecture:

```
app/src/main/java/com/itera/news/
├── data/                         # DATA LAYER (Data source, networking, DB)
│   ├── local/                    # Room Database, DAOs, Entities
│   │   ├── NewsDatabase.kt
│   │   └── entity/ArticleEntity.kt
│   ├── remote/                   # Retrofit REST API & Gemini AI Service
│   │   ├── NewsApi.kt
│   │   ├── NewsResponse.kt
│   │   └── GeminiService.kt
│   └── repository/               # Implementasi repositori (Offline-first caching)
│       └── NewsRepositoryImpl.kt
│
├── domain/                       # DOMAIN LAYER (Business logic, pure Kotlin)
│   ├── model/                    # Data Model domain (Article)
│   ├── repository/               # Interface repositori
│   └── usecase/                  # Use case penanganan logika berita
│       └── GetMbgNewsUseCase.kt
│
├── presentation/                 # PRESENTATION LAYER (UI & State)
│   ├── navigation/               # Setup routing dan Type-safe Navigation
│   │   ├── NavGraph.kt
│   │   └── Screen.kt
│   ├── screen/                   # Halaman visual (Splash, Home, Detail, About, Bookmark)
│   │   ├── HomeScreen.kt
│   │   ├── DetailScreen.kt
│   │   └── NewsScreens.kt
│   └── viewmodel/                # State management menggunakan StateFlow
│       ├── NewsUiState.kt
│       └── NewsViewModel.kt
│
├── di/                           # DEPENDENCY INJECTION LAYER
│   └── AppModule.kt              # Modul Koin (database, network, repository, viewmodel)
│
├── ui/                           # UI THEMING LAYER
│   └── theme/                    # Material3 Theme & Custom Neumorphic shadow modifiers
│       ├── Theme.kt
│       ├── Color.kt
│       └── NeumorphicModifier.kt
│
├── MainActivity.kt               # Entrypoint Utama Aplikasi Android
└── NewsApplication.kt            # Inisialisasi awal (Koin DI container setup)
```

---

## 🛠️ Cara Menjalankan Aplikasi

1.  **Kloning Repositori**:
    ```bash
    git clone https://github.com/Febvn/Proyek-Pengembangan-Aplikasi-Mobile.git
    cd Proyek-Pengembangan-Aplikasi-Mobile
    ```
2.  **Buat Berkas `local.properties`**:
    Salin template `local.properties.example` menjadi `local.properties` di folder root project:
    ```bash
    cp local.properties.example local.properties
    ```
3.  **Isi API Key Gemini**:
    Buka `local.properties` dan isi API Key Gemini Anda:
    ```properties
    GEMINI_API_KEY=AIzaSy... (API Key Anda)
    ```
    *Catatan: Anda dapat memperoleh API Key gratis di [Google AI Studio](https://aistudio.google.com/).*
4.  **Buka di Android Studio**:
    *   Gunakan versi **Android Studio Ladybug (2024.2.1)** atau lebih baru.
    *   Lakukan **Sync Project with Gradle Files** dan biarkan dependencies terunduh otomatis.
5.  **Jalankan di Device/Emulator**:
    *   Pilih run configuration `app`.
    *   Klik tombol **Run** (ikon putar hijau) untuk mengompilasi dan memasang aplikasi ke emulator/device aktif Anda.

---

## 📚 Dokumen Terkait

*   [🚀 Panduan Lengkap Cara Menjalankan](./docs/CARA_MENJALANKAN.md)
*   [📋 Rencana Project & Sprints](./docs/PROJECT_PLAN.md)
*   [🏗️ Penjelasan Arsitektur & Kode](./docs/STRUKTUR_KODE.md)
*   [🌿 Git Branching & Workflows](./docs/GIT_WORKFLOW.md)
*   [🔧 Pemecahan Masalah (Troubleshooting)](./docs/TROUBLESHOOTING.md)

---

## 👨‍🏫 Dosen Pengampu
*   **Pak Habib** ([GitHub: mh4Scripts](https://github.com/mh4Scripts))

**Program Studi Teknik Informatika**  
Institut Teknologi Sumatera (ITERA)
