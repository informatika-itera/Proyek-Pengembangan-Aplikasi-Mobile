# 🥗 NutriScan

![CI](https://github.com/Xysaa/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

> Scan. Analyze. Eat Smart.

Aplikasi mobile multiplatform (Android-first) yang memungkinkan pengguna untuk **memindai barcode makanan/minuman kemasan** dan mendapatkan **analisis nutrisi yang dipersonalisasi** berdasarkan profil kesehatan masing-masing pengguna.

---

## 👥 Tim

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| Annisa Al-Qoriah | 123140030 | [@123140030-AnnisaAl-Qoriah](https://github.com/123140030-AnnisaAl-Qoriah) | FE Dev & QA |
| Stevanus Cahya Anggara | 123140038 | [@Xysaa](https://github.com/Xysaa) | Lead & Android Dev |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Dosen:** Pak Habib ([@mh4Scripts](https://github.com/mh4Scripts))  
**Institut:** Institut Teknologi Sumatera (ITERA)

---

## 📱 Deskripsi Aplikasi

NutriScan membantu pengguna membuat keputusan konsumsi yang lebih sehat. Cukup arahkan kamera ke barcode produk makanan/minuman kemasan, dan aplikasi akan:

1. Mengambil data nutrisi dari **OpenFoodFacts API** (database produk global, open-source)
2. Membandingkan kandungan gula, garam, lemak, dan kalori dengan **batas harian yang direkomendasikan** berdasarkan profil pengguna (usia, berat badan, tinggi badan, riwayat penyakit)
3. Memberikan **peringatan atau rekomendasi** apakah produk aman untuk dikonsumsi

---

## ✨ Fitur

### Minimum (Wajib)
- [ ] **Onboarding & Profil Pengguna** — Input data diri: nama, usia, tinggi badan, berat badan, riwayat penyakit (diabetes, hipertensi, obesitas, dll.) — disimpan lokal dengan SQLDelight
- [ ] **Barcode Scanner** — Scan barcode produk kemasan menggunakan kamera perangkat
- [ ] **Detail Nutrisi** — Tampilkan informasi lengkap: kalori, lemak, gula, garam, protein, karbohidrat per sajian
- [ ] **Analisis Personalisasi** — Bandingkan nilai gizi produk dengan kebutuhan harian pengguna; tampilkan status AMAN / PERHATIAN / HINDARI
- [ ] **Riwayat Scan** — Simpan dan lihat kembali produk yang pernah di-scan (local storage)
- [ ] **Navigasi Multi-Screen** — Minimal 5 layar: Onboarding, Home/Scanner, Hasil Scan, Riwayat, Profil
- [ ] **State Management** — MVVM + StateFlow untuk semua UI state
- [ ] **Minimal 10 unit tests** + 3 UI tests, coverage > 50%
- [ ] **Koin DI** — Dependency injection setup

### Bonus (Target)
- [ ] **AI Integration (+10%)** — Gunakan Gemini API untuk memberikan saran konsumsi yang lebih kontekstual dan natural ("Produk ini tinggi gula, cocok dikonsumsi sebelum olahraga tapi hindari sebelum tidur...")
- [ ] **Offline First (+5%)** — Cache hasil scan terakhir di SQLDelight; aplikasi tetap bisa menampilkan riwayat tanpa internet
- [ ] **Dark Mode (+5%)** — Support tema gelap/terang dengan Material 3
- [ ] **Animations (+5%)** — Animasi transisi antar screen, animasi loading saat fetch API, animasi indikator status nutrisi

**Total target bonus: +25%**

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
│   Use Cases ◄──► Repository Interfaces          │
│           (Pure Kotlin, no framework)            │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                DATA LAYER                        │
│   Repository Impl                               │
│   ├── Remote: Ktor + OpenFoodFacts API          │
│   ├── Remote: Ktor + Gemini API (AI)            │
│   └── Local:  SQLDelight (user profile, history)│
└─────────────────────────────────────────────────┘
```

### Struktur Folder

```
composeApp/src/commonMain/kotlin/com/nutriscan/
├── core/                        # Koin modules
│   ├── di
        ├── AppModule.kt
│   ├── network
        ├── ApiConfig.kt
        ├── HttpClientFactory.kt
│   └── util
        ├── DatabaseDriverFactory.kt
        ├── Extension.kt 
├── data/
│   ├── local/
│   │   ├── dao/               # SQLDelight DAOs
│   │   ├── entity/            # DB entities (UserProfile, ScanHistory)
│   │   └── datastore/         # Preferences (theme, onboarding state)
│   ├── remote/
│   │   ├── api/               # OpenFoodFactsService, GeminiService
│   │   └── dto/               # Response DTOs
│   └── repository/            # Repository implementations
├── domain/
│   ├── model/                 # Product, NutritionAnalysis, UserProfile
│   ├── repository/            # Repository interfaces
│   └── usecase/               # AnalyzeNutritionUseCase, GetProductUseCase
└── presentation/
    ├── navigation/             # NavHost, Routes
    ├── theme/                  # Material3 Colors, Typography, Dark Mode
    ├── components/             # Reusable composables (NutrientBar, StatusChip)
    └── screens/
        ├── onboarding/         # Input profil pengguna
        ├── home/               # Scanner screen (kamera + barcode)
        ├── result/             # Hasil scan & analisis nutrisi
        ├── history/            # Riwayat produk yang pernah di-scan
        └── profile/            # Lihat & edit profil pengguna
```

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform, Compose Multiplatform |
| **Architecture** | MVVM, Clean Architecture, Repository Pattern |
| **Async** | Coroutines, Flow, StateFlow |
| **Networking** | Ktor Client + Kotlinx Serialization |
| **Food Database** | [OpenFoodFacts API](https://world.openfoodfacts.org/data) |
| **AI** | Google Gemini API |
| **Local Storage** | SQLDelight (profil user, riwayat scan) |
| **Preferences** | DataStore (tema, onboarding flag) |
| **DI** | Koin |
| **Barcode Scan** | ML Kit (Android expect/actual) |
| **Testing** | kotlin.test, MockK, Turbine, Compose Test |
| **CI/CD** | GitHub Actions |

---

## 🗂️ Sprint Plan

| Sprint | Minggu | Target | PIC |
|--------|--------|--------|-----|
| **Sprint 1** | W11 | Planning, repo setup, CI/CD, README | Berdua |
| **Sprint 2** | W12 | Onboarding screen, profil user (SQLDelight), navigasi 5 screen, Home UI | Anggota 1 |
| **Sprint 2** | W12 | OpenFoodFacts API integration (Ktor), domain models, use cases | Anggota 2 |
| **Sprint 3** | W13 | Barcode scanner (MLKit), Result screen + logika analisis nutrisi | Anggota 1 |
| **Sprint 3** | W13 | Riwayat scan (offline-first cache), Gemini AI integration | Anggota 2 |
| **Sprint 4** | W14 | Dark mode, animasi, UI polish | Anggota 1 |
| **Sprint 4** | W14 | Unit tests & UI tests, bug fixes | Anggota 2 |
| **Sprint 5** | W15 | Final fixes, dokumentasi, demo prep | Berdua |
| **UAS** | W16 | Demo Day 🎉 | Berdua |

---

## 🚀 Setup & Cara Menjalankan

### Prerequisites
- Android Studio Ladybug (2024.2.1) atau lebih baru
- JDK 17+
- Gradle 8.x

### Langkah Setup

1. **Clone repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
   cd YOUR_REPO_NAME
   ```

2. **Setup `local.properties`**
   ```bash
   cp local.properties.example local.properties
   # Edit local.properties dan isi:
   # GEMINI_API_KEY=your_key_here
   ```
   Dapatkan Gemini API key gratis di: https://aistudio.google.com/

3. **Build project**
   ```bash
   ./gradlew build
   # Atau hanya APK debug (lebih cepat):
   ./gradlew :composeApp:assembleDebug
   ```

4. **Run di Android**
   - Pilih run configuration `composeApp` di Android Studio, atau:
   ```bash
   ./gradlew :composeApp:installDebug
   ```

### Menjalankan Tests
```bash
./gradlew allTests
./gradlew :composeApp:testDebugUnitTest
```

---

## 📡 API Reference

### OpenFoodFacts API
- **Base URL:** `https://world.openfoodfacts.org`
- **Endpoint:** `GET /api/v2/product/{barcode}.json`
- **Gratis & tanpa API key** untuk penggunaan wajar
- Dokumentasi: https://openfoodfacts.github.io/openfoodfacts-server/api/

### Gemini API
- **Model:** `gemini-2.0-flash` (gratis tier)
- Digunakan untuk: generate saran konsumsi berbasis profil + data nutrisi

---

## 📝 Logika Analisis Nutrisi

Analisis didasarkan pada **% Angka Kecukupan Gizi (AKG)** harian yang disesuaikan dengan profil pengguna:

| Nutrisi | Ambang PERHATIAN | Ambang HINDARI | Penyesuaian Riwayat Penyakit |
|---------|-----------------|----------------|-------------------------------|
| Gula | > 20% AKG/sajian | > 35% AKG/sajian | Threshold diturunkan 50% untuk diabetes |
| Natrium (Garam) | > 20% AKG/sajian | > 35% AKG/sajian | Threshold diturunkan 50% untuk hipertensi |
| Lemak Jenuh | > 15% AKG/sajian | > 25% AKG/sajian | Threshold diturunkan untuk obesitas |
| Kalori | > 25% AKG/sajian | > 40% AKG/sajian | Disesuaikan dengan BMI |

---

## 📄 Lisensi

MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.