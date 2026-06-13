# 🌙 SholatYuk - Aplikasi Islam Lengkap

Aplikasi **Islam modern** yang membantu umat Muslim menjalankan ibadah dengan lebih mudah, tepat, dan khusyuk. Dibangun menggunakan **Kotlin Multiplatform (KMP)** & **Compose Multiplatform** untuk pengalaman lintas platform yang optimal.

Aplikasi ini dikembangkan sebagai Tugas Besar (Tubes) mata kuliah **Pengembangan Aplikasi Mobile** - Institut Teknologi Sumatera (ITERA).

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green?logo=android" alt="Platform"/>
  <img src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License"/>
</p>

---

## 👥 Tim Pengembang

| Nama | NIM | Peran Utama |
|------|-----|-------------|
| **Bayu Brigas Novaldi** | 123140072 | Domain Layer, Data Layer, SQLDelight Schemas, Repository Implementations |
| **Gilang Surya Agung** | 123140187 | Presentation Layer, UI/UX Components, Navigation Compose, Theming & Animations |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Institut:** Institut Teknologi Sumatera (ITERA)

---

## ✨ Fitur Utama

- 🕌 **Jadwal Sholat Akurat** - Jadwal sholat 5 waktu otomatis berdasarkan lokasi GPS (Aladhan API) dengan dukungan *offline cache*.
- 🧭 **Arah Kiblat** - Penunjuk arah kiblat otomatis menggunakan sensor kompas internal dan kalkulasi trigonometri koordinat Kakbah.
- 📿 **Dzikir & Doa** - Kumpulan dzikir pagi/petang, setelah sholat, dan doa sehari-hari lengkap dengan fitur *Search* dan *Filter* kategori.
- 🤖 **IslamAI** - Asisten cerdas berbasis Google Gemini API untuk menjawab berbagai pertanyaan seputar Islam langsung di dalam aplikasi.
- 📝 **Catatan Kajian** - Fitur manajemen rangkuman atau catatan pengajian dengan operasi CRUD (Create, Read, Update, Delete) berbasis database lokal.
- 🔔 **Notifikasi Adzan** - Pengingat waktu sholat otomatis dengan penjadwalan latar belakang (*background task*).
- 🌓 **Mode Gelap/Terang** - Adaptasi tema sistem secara otomatis.

---

## 🛠 Tech Stack & Arsitektur

### Arsitektur: Clean Architecture (MVVM)
Aplikasi menerapkan standar industri dengan pemisahan *layer* yang ketat:

- **Presentation Layer**: UI (Compose Multiplatform) dan ViewModel (StateFlow dengan pola Unidirectional Data Flow).
- **Domain Layer**: Business logic murni (Models, Repository Interfaces, UseCases).
- **Data Layer**: Integrasi API (Ktor Client), Lokal Database (SQLDelight), dan DataStore (Preferences).

```text
┌─────────────────────────────────────────┐
│           PRESENTATION LAYER            │
│  ┌──────────────┐    ┌───────────────┐  │
│  │    Screen    │◄──►│   ViewModel   │  │
│  │ (Composable) │State│  (StateFlow)  │  │
│  └──────────────┘    └───────────────┘  │
└─────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────┐
│             DOMAIN LAYER                │
│  ┌──────────────────────────────────┐   │
│  │           Use Cases              │   │
│  │        (Business Logic)          │   │
│  └──────────────────────────────────┘   │
│  ┌──────────────────────────────────┐   │
│  │      Repository Interface        │   │
│  └──────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────┐
│              DATA LAYER                 │
│  ┌──────────┐  ┌──────┐  ┌──────────┐  │
│  │SQLDelight│  │ Ktor │  │DataStore │  │
│  │ (Local)  │  │(API) │  │ (Prefs)  │  │
│  └──────────┘  └──────┘  └──────────┘  │
└─────────────────────────────────────────┘
```

### Komponen Teknologi

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform (KMP) |
| **UI Framework** | Compose Multiplatform (Android & iOS) |
| **Dependency Injection** | Koin 4.0 |
| **Local Database** | SQLDelight 2.2.1 |
| **Network** | Ktor Client |
| **Asynchronous** | Kotlin Coroutines & Flow |
| **Navigation** | Compose Navigation (Type-safe) |
| **Preferences** | DataStore |
| **AI Integration** | Google Gemini API |

---

## 📁 Struktur Project

<details>
<summary>Klik untuk melihat struktur direktori</summary>

```text
composeApp/src/
├── commonMain/kotlin/com/example/sholatyuk/
│   ├── core/                    # Utilitas inti & DI
│   │   ├── di/                  # Koin modules
│   │   ├── network/             # HTTP client config
│   │   ├── location/            # Location service (expect/actual)
│   │   └── util/                # Extensions & helpers
│   ├── data/                    # Data layer
│   │   ├── local/               # SQLDelight database & mappers
│   │   ├── remote/              # Ktor API services & DTOs
│   │   └── repository/          # Repository implementations
│   ├── domain/                  # Domain layer
│   │   ├── model/               # Domain models
│   │   ├── repository/          # Repository interfaces
│   │   └── usecase/             # Business logic use cases
│   ├── presentation/            # UI layer
│   │   ├── navigation/          # Routes & NavHost
│   │   ├── screens/             # Screens & ViewModels
│   │   │   ├── home/            # Home screen
│   │   │   ├── prayer/          # Jadwal sholat screen
│   │   │   └── kajian/          # Catatan kajian screen
│   │   └── theme/               # Material 3 theme
│   └── App.kt                   # Entry point
├── commonMain/sqldelight/       # SQLDelight schemas
│   ├── Doa.sq
│   ├── Dzikir.sq
│   ├── PrayerTime.sq
│   ├── ChatHistory.sq
│   ├── KajianNote.sq
│   └── TasbihCount.sq
├── androidMain/                 # Android-specific implementations
└── iosMain/                     # iOS-specific implementations
```
</details>

---

## 📅 Sprint Progress

| Sprint | Target Utama | Status |
| :--- | :--- | :---: |
| **Sprint 1** | Planning, Clean Architecture Setup, Koin DI, CI/CD | ✅ |
| **Sprint 2** | UI Screens (Home, Prayer, Dzikir), Navigasi, SQLDelight (Local Storage), MVVM Implementation | ✅ |
| **Sprint 3** | Aladhan API (Jadwal Sholat & GPS), Kompas Kiblat, Offline-first Cache, Gemini AI (IslamAI) | ✅ |
| **Sprint 4** | Bug Fixes, UI Polish (Animasi, Tema), Unit Tests & Instrumentation | ✅ |
| **Sprint 5** | Stabilitas Aplikasi, Release APK, Dokumentasi Lengkap, Demo Preparation | ✅ |

---

## 🚀 Setup & Cara Menjalankan

### Prasyarat
- Android Studio Ladybug (2024.2.1) atau lebih baru
- JDK 17
- Git

### Langkah Setup

**1. Clone repository:**
```bash
git clone https://github.com/bayybrigas04/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
git checkout project/123140072-123140187-SholatYuk
```

**2. Setup API Key:**
Buat file `local.properties` di root direktori project:
```bash
cp local.properties.example local.properties
```
Edit `local.properties` dengan text editor pilihan Anda:
```properties
sdk.dir=/path/to/android/sdk
GEMINI_API_KEY=your_gemini_api_key_here
```
> Dapatkan API key gratis di: [Google AI Studio](https://aistudio.google.com/)

**3. Build Project:**
```bash
./gradlew :composeApp:assembleDebug
```

**4. Run di Android:**
Buka di Android Studio → klik tombol **Run** (▶) pada toolbar, atau melalui terminal:
```bash
./gradlew :composeApp:installDebug
```

---

## 📸 Screenshots & Demo

*(Catatan: Silakan sesuaikan tautan gambar dan video di bawah ini dengan sumber asli Anda)*

| Home & Jadwal | Fitur Dzikir & Doa | Kompas Kiblat | Asisten IslamAI |
| :---: | :---: | :---: | :---: |
| <img width="720" height="1604" alt="Screenshot_2026-06-12-23-19-18-64_9163410356b4ba3c803fd7552b7b9a71" src="https://github.com/user-attachments/assets/326586d6-3412-46dd-9604-4344444e89cb" />
 | <img width="720" height="1604" alt="Screenshot_2026-06-12-23-19-22-36_9163410356b4ba3c803fd7552b7b9a71" src="https://github.com/user-attachments/assets/74e0842d-8f17-4028-9bce-f5517a648cb1" />
 | <img width="720" height="1604" alt="Screenshot_2026-06-12-23-19-42-90_9163410356b4ba3c803fd7552b7b9a71" src="https://github.com/user-attachments/assets/2e52e730-9954-49c2-b4cf-5ef4014ccc68" />
 | <img width="720" height="1604" alt="Screenshot_2026-06-12-23-19-29-17_9163410356b4ba3c803fd7552b7b9a71" src="https://github.com/user-attachments/assets/7202bece-1ed9-4f2a-870d-2fe825bc075e" />
 |

---
[![Demo SholatYuk](https://img.shields.io/badge/▶%20Demo%20Video-YouTube-red?style=for-the-badge&logo=youtube)](https://s.itera.id/PAM-VIDEO-DEMO)
---

## 📄 Lisensi

MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.
