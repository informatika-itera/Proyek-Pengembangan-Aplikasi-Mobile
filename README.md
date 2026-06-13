# ✈️ TripMate

<p align="center">
  <img src="composeApp/src/commonMain/composeResources/drawable/ic_tripmate.png" alt="TripMate Logo" width="120"/>
</p>

<p align="center">
  <strong>Rencanakan. Jalani. Kenang.</strong><br/>
  Aplikasi mobile multiplatform (Android-first) untuk merencanakan perjalanan,<br/>melacak budget, dan mendapatkan itinerary dari AI.
</p>

<p align="center">
  <img src="https://github.com/Ramaaaadevs/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg?branch=project/123140116-123140135-TripMate" alt="CI"/>
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" alt="Platform"/>
  <img src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin" alt="Kotlin"/>
</p>

---

## 👥 Tim

| Nama | NIM |
|------|-----|
| Diwan Ramadhani Dwi Putra | 123140116 |
| M. Gymnastiar Syahputra | 123140135 |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Dosen:** Muhammad Habib Algifari, S.Kom., M.TI. ([@mh4Scripts](https://github.com/mh4Scripts))  
**Institut:** Institut Teknologi Sumatera (ITERA)

---

## 📱 Deskripsi Aplikasi

TripMate adalah aplikasi mobile travel planning & tracking yang dirancang untuk mendampingi pengguna di setiap tahap perjalanan. Pengguna bisa merencanakan trip, membuat packing list, memantau budget, dan mendapatkan itinerary otomatis dari AI — semuanya dalam satu aplikasi.

Berbeda dari aplikasi travel lainnya yang berfokus pada rekomendasi otomatis, TripMate menempatkan pengguna sebagai pencatat aktif perjalanannya sendiri. AI hadir sebagai asisten — bukan menggantikan pengalaman personal.

---

## ✨ Fitur

### ✅ Minimum (Wajib)
- [x] **CRUD Trip** — Tambah, lihat, edit, dan hapus rencana perjalanan dengan date picker & budget formatter
- [x] **Search & Filter** — Cari destinasi dengan debounce 300ms, filter real-time
- [x] **Navigasi Multi-Screen** — 6 layar: Splash, Beranda, Tambah/Edit, Detail, AI, Statistik, Profil
- [x] **State Management** — MVVM + StateFlow untuk semua UI state
- [x] **Local Storage** — SQLDelight untuk data trip dan packing list
- [x] **Minimal 10 unit tests** + 3 UI tests, coverage 53%
- [x] **Koin DI** — Dependency injection setup

### 🎯 Bonus (Tercapai)
- [x] **AI Integration (+10%)** — Gemini 2.5 Flash untuk generate itinerary otomatis berdasarkan destinasi, durasi, budget, dan minat
- [x] **Offline First (+5%)** — SQLDelight offline-first + tombol retry saat error jaringan
- [x] **Dark Mode (+5%)** — Toggle tampilan gelap/terang via Settings screen
- [x] **Packing Checklist** — Daftar barang bawaan per trip dengan ceklis, counter, dan dua section (belum/sudah dibawa)
- [x] **Trip Statistics** — Ringkasan total trip, budget, rata-rata, trip termahal & terhemat
- [x] **Settings Screen** — Pengaturan Dark Mode dan Notifikasi terpisah dari Profil
- [x] **App Icon & Splash Screen** — Icon kustom TripMate dengan animasi fade in/out

---

## 🏗️ Arsitektur

Menggunakan **Clean Architecture + MVVM** sesuai panduan mata kuliah.

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                  │
│   Screens (Composable) ◄──► ViewModel           │
│             (StateFlow / UDF Pattern)            │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│               DOMAIN LAYER                       │
│   Models ◄──► Repository Interfaces             │
│           (Pure Kotlin, no framework)            │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                DATA LAYER                        │
│   Repository Impl                               │
│   ├── Remote: Ktor + Gemini API                 │
│   └── Local:  SQLDelight (trip, packing list)   │
└─────────────────────────────────────────────────┘
```

### 📁 Struktur Folder

<details>
<summary>Klik untuk expand</summary>

```
composeApp/src/
├── androidMain/
│   ├── kotlin/com/example/tripmate/
│   │   ├── MainActivity.kt
│   │   ├── TripMateApplication.kt
│   │   ├── core/di/AndroidModule.kt
│   │   ├── core/network/ApiConfig.android.kt
│   │   ├── core/util/DatabaseDriverFactory.android.kt
│   │   └── data/local/datastore/DataStoreFactory.android.kt
│   └── res/
│       ├── mipmap-*/ic_launcher.png
│       ├── mipmap-anydpi-v26/
│       └── values/colors.xml, strings.xml, themes.xml
├── commonMain/
│   ├── composeResources/drawable/ic_tripmate.png
│   └── kotlin/com/example/tripmate/
│       ├── App.kt
│       ├── core/
│       │   ├── di/AppModule.kt
│       │   ├── network/ApiConfig.kt + HttpClientFactory.kt
│       │   └── util/DatabaseDriverFactory.kt + Extensions.kt
│       ├── data/
│       │   ├── local/datastore/DataStoreFactory.kt + UserPreferences.kt
│       │   ├── remote/api/GeminiService.kt
│       │   ├── remote/dto/GeminiDto.kt
│       │   └── repository/TripRepositoryImpl.kt + AIRepositoryImpl.kt + PackingRepositoryImpl.kt
│       ├── domain/
│       │   ├── model/Trip.kt + PackingItem.kt
│       │   └── repository/TripRepository.kt + AIRepository.kt + PackingRepository.kt
│       └── presentation/
│           ├── navigation/Screen.kt + AppNavHost.kt + BottomNavItem.kt
│           ├── theme/Theme.kt
│           └── screens/
│               ├── splash/SplashScreen.kt
│               ├── home/HomeScreen.kt + TripViewModel.kt + HomeUiState.kt
│               ├── addedit/AddEditTripScreen.kt
│               ├── detail/TripDetailScreen.kt + PackingViewModel.kt
│               ├── ai/AIScreen.kt + AIViewModel.kt
│               ├── statistics/TripStatisticsScreen.kt
│               └── profile/ProfileScreen.kt
├── commonTest/
│   └── kotlin/com/example/tripmate/
│       ├── data/repository/
│       │   ├── NoteRepositoryTest.kt
│       │   └── PackingRepositoryTest.kt
│       └── presentation/
│           ├── HomeViewModelTest.kt
│           ├── PackingViewModelTest.kt
│           └── AIViewModelTest.kt
└── androidTest/
    └── kotlin/com/example/tripmate/
        └── presentation/HomeScreenTest.kt
```

</details>

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform, Compose Multiplatform |
| **Architecture** | MVVM, Clean Architecture, Repository Pattern |
| **Async** | Coroutines, Flow, StateFlow |
| **Networking** | Ktor Client + Kotlinx Serialization |
| **AI** | Google Gemini API (gemini-2.5-flash) |
| **Local Storage** | SQLDelight (trip, packing list) |
| **Preferences** | DataStore (dark mode) |
| **DI** | Koin |
| **Testing** | kotlin.test + Turbine + kotlinx-coroutines-test |
| **Coverage** | Kover |
| **CI/CD** | GitHub Actions |

---

## 🗂️ Sprint Plan

| Sprint | Minggu | Target | PIC |
|--------|--------|--------|-----|
| **Sprint 1** | W11 | Setup repo, CI/CD, Clean Architecture, SQLDelight, Koin DI | Rama |
| **Sprint 2** | W12 | UI Screens, navigasi, CRUD, local storage | Gymnas |
| **Sprint 3** | W13 | Search, sort, AI Integration, Splash, BottomNav, Dark Mode, Packing, Statistics | Keduanya |
| **Sprint 4** | W14 | Bug fixes, UI polish, 23 unit tests, 53% coverage | Keduanya |
| **Sprint 5** | W15 | Final fixes, dokumentasi, demo prep | Keduanya |
| **UAS** | W16 | Demo Day 🎉 | Keduanya |

---

## 🚀 Setup & Cara Menjalankan

### Prerequisites
- Android Studio Hedgehog atau lebih baru
- JDK 17+
- Android SDK (API 26+)
- Gemini API Key ([dapatkan di sini](https://aistudio.google.com/app/apikey))

### Langkah Setup

**1. Clone repository**
```bash
git clone https://github.com/Ramaaaadevs/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
git checkout project/123140116-123140135-TripMate
```

**2. Setup `local.properties`**
```properties
# Windows
sdk.dir=C\:\\Users\\[username]\\AppData\\Local\\Android\\Sdk

# Linux/macOS
# sdk.dir=/home/[username]/Android/Sdk

GEMINI_API_KEY=api_key_kamu
```
> Dapatkan Gemini API key gratis di: https://aistudio.google.com/

**3. Build project**
```bash
# Windows
.\gradlew :composeApp:assembleDebug

# Linux/macOS
./gradlew :composeApp:assembleDebug
```

**4. Run di Android**
```bash
# Windows
.\gradlew :composeApp:installDebug

# Linux/macOS
./gradlew :composeApp:installDebug
```

> ⚠️ Jika app crash dengan error `no such table` setelah update schema — uninstall app dari HP dulu, lalu install ulang.

---

## 🧪 Testing

### Menjalankan Unit Tests
```bash
.\gradlew :composeApp:testDebugUnitTest
```

### Menjalankan UI Tests (butuh emulator/device aktif)
```bash
.\gradlew :composeApp:connectedDebugAndroidTest
```

### Coverage Report (Kover)
```bash
.\gradlew :composeApp:koverHtmlReportDebug
```
Report tersedia di: `composeApp/build/reports/kover/html/index.html`

### Ringkasan Tests Sprint 4

| Test Class | Jumlah | Cakupan |
|---|---|---|
| TripRepositoryTest | 6 | Repository CRUD |
| TripViewModelTest | 6 | ViewModel state & actions |
| PackingRepositoryTest | 3 | Packing CRUD |
| PackingViewModelTest | 3 | Packing ViewModel |
| AIViewModelTest | 2 | AI state & validasi |
| HomeScreenTest (UI) | 3 | Critical UI flows |
| **Total** | **23** | **53% class coverage** |

---

## 📡 API Reference

### Gemini API
- **Model:** `gemini-2.5-flash`
- **Endpoint:** `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent`
- **Auth:** API Key via query parameter
- **Digunakan untuk:** Generate itinerary perjalanan berdasarkan destinasi, durasi, budget, dan minat pengguna
- **Docs:** https://ai.google.dev/docs

---

## 🎥 Demo Aplikasi

https://youtu.be/38DMuYmpc9g?si=o_s0Xp0J3FbCrvbY

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan akademik di Institut Teknologi Sumatera.  
Template dasar menggunakan **NoteAI** oleh mh4Scripts.
