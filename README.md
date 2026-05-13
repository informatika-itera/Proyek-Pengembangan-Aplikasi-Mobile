# 💰 TabungIn
> Aplikasi manajemen target tabungan berbasis mobile multiplatform

![CI](https://github.com/[team-name]/tabungin/actions/workflows/ci.yml/badge.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green)

---

## 👥 Tim

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| [Falih Faiq Fadhlurrahman] | [123140129] | [@ScienceCom](https://github.com/ScienceCom) | Lead & Data Engineer |
| [Khairul Rijal Syauqi] | [123140143] | [@32gz](https://github.com/32gz) | UI/UX & QA Developer |

---

## 📱 Deskripsi Aplikasi

**TabungIn** adalah aplikasi manajemen target tabungan berbasis mobile multiplatform yang dikembangkan menggunakan framework **Kotlin Multiplatform** dan **Compose Multiplatform** dengan menerapkan pola **Clean Architecture** serta **MVVM**.

Aplikasi ini memfasilitasi pengguna untuk mencatat dan memantau progres celengan fisik secara digital melalui fitur CRUD yang didukung oleh **SQLDelight** untuk penyimpanan lokal, serta visualisasi data reaktif menggunakan **StateFlow**.

Dengan sistem navigasi antarlayar yang mendukung argument passing, TabungIn juga mengintegrasikan **Gemini API** untuk asisten finansial cerdas serta fitur **Dark Mode** guna memberikan pengalaman pengguna yang modern, responsif, dan fungsional.

---

## ✨ Fitur Aplikasi

### ✅ Fitur Minimum
- [ ] **Home Screen** — Tampilan total tabungan dan daftar target aktif dengan progress bar
- [ ] **Target Detail Screen** — Detail target tabungan, riwayat setoran, dan sisa target
- [ ] **Add/Edit Target Screen** — Form CRUD untuk membuat dan mengedit target tabungan
- [ ] **Riwayat Screen** — Daftar semua transaksi setoran tabungan
- [ ] **Settings Screen** — Pengaturan preferensi pengguna
- [ ] **CRUD Target Tabungan** — Create, Read, Update, Delete target tabungan
- [ ] **CRUD Setoran** — Catat setoran ke target tertentu
- [ ] **Local Storage** — Semua data tersimpan offline menggunakan SQLDelight
- [ ] **State Management** — StateFlow + MVVM pattern
- [ ] **Navigation** — Multi-screen navigation dengan argument passing
- [ ] **Unit Tests** — Minimal 10 unit tests, coverage > 50%
- [ ] **UI Tests** — Minimal 3 UI tests
- [ ] **Dependency Injection** — Koin DI setup

### 🎁 Fitur Bonus (Target)
- [ ] **Dark Mode** (+5%) — Support light/dark theme
- [ ] **AI Integration** (+10%) — Asisten finansial menggunakan Gemini API
- [ ] **Offline First** (+5%) — App berfungsi penuh tanpa koneksi internet
- [ ] **Animations** (+5%) — Animasi progress bar dan transisi antar layar
- [ ] **CI/CD** (+5%) — Automated build & test dengan GitHub Actions
- [ ] **iOS Support** (+10%) — App berjalan di iOS simulator

---

## 🛠️ Tech Stack

| Kategori | Teknologi |
|----------|-----------|
| Framework | Kotlin Multiplatform, Compose Multiplatform |
| Architecture | MVVM, Clean Architecture, Repository Pattern |
| Async | Coroutines, Flow, StateFlow |
| Storage | SQLDelight (local DB), DataStore (preferences) |
| Networking | Ktor Client (untuk Gemini API) |
| DI | Koin |
| AI | Google Gemini API |
| Testing | kotlin.test, MockK, Turbine, Compose Test |
| CI/CD | GitHub Actions |

---

## 🏗️ Arsitektur

TabungIn menggunakan **Clean Architecture** dengan 3 layer utama:

```
┌─────────────────────────────────────┐
│        PRESENTATION LAYER           │
│   UI (Composables) + ViewModels     │
│          + UI State                 │
├─────────────────────────────────────┤
│           DOMAIN LAYER              │
│   Use Cases + Domain Models +       │
│      Repository Interfaces          │
├─────────────────────────────────────┤
│            DATA LAYER               │
│  Repository Impl + Local (SQLDelight│
│  + DataStore) + Remote (Ktor/Gemini)│
└─────────────────────────────────────┘
```

### Struktur Folder

```
composeApp/
└── src/commonMain/kotlin/com/tabungin/app/
    ├── App.kt                    # Entry point & NavHost
    ├── di/                       # Koin modules
    │   ├── AppModule.kt
    │   ├── DataModule.kt
    │   └── ViewModelModule.kt
    ├── data/
    │   ├── local/                # SQLDelight, DataStore
    │   ├── remote/               # Ktor, Gemini API
    │   ├── repository/           # Repository implementations
    │   └── model/                # DTOs, Entities
    ├── domain/
    │   ├── model/                # Domain models (Target, Setoran)
    │   ├── repository/           # Repository interfaces
    │   └── usecase/              # Use cases
    └── presentation/
        ├── navigation/           # NavHost, Routes
        ├── theme/                # Colors, Typography, Dark Mode
        ├── components/           # Reusable composables
        └── screens/
            ├── home/             # HomeScreen + HomeViewModel
            ├── detail/           # TargetDetailScreen + ViewModel
            ├── add_edit/         # AddEditScreen + ViewModel
            ├── riwayat/          # RiwayatScreen + ViewModel
            └── settings/         # SettingsScreen + ViewModel
```

### Domain Models

```kotlin
data class Target(
    val id: Long,
    val nama: String,
    val targetNominal: Double,
    val terkumpul: Double,
    val deadline: Long?,
    val createdAt: Long
)

data class Setoran(
    val id: Long,
    val targetId: Long,
    val nominal: Double,
    val catatan: String?,
    val tanggal: Long
)
```

---

## 🚀 Setup & Menjalankan Aplikasi

### Prasyarat
- Android Studio Hedgehog atau yang lebih baru
- JDK 17
- Android SDK (min API 24)
- *(Opsional untuk iOS)* Xcode 15+, macOS

### Langkah-langkah

```bash
# 1. Clone repository
git clone git clone https://github.com/ScienceCom/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile

# 2. Buka di Android Studio
# File > Open > pilih folder tabungin

# 3. Sync Gradle
# Klik "Sync Now" jika muncul notifikasi

# 4. Jalankan di emulator / device Android
# Run > Run 'composeApp'
```

### Konfigurasi API Key (untuk fitur AI)

Buat file `local.properties` di root project dan tambahkan:
```properties
GEMINI_API_KEY=your_api_key_here
```

---

## 🌿 Git Branching Strategy

```
main          ← Production-ready, protected branch
  └── develop ← Integration branch
        └── feature/[nama-fitur]  ← Feature branches
```

**Commit Convention:**
```
feat: add home screen with savings target list
fix: resolve crash when target nominal is zero
test: add unit tests for TargetRepository
docs: update README with setup guide
```

---

## 📊 Sprint Plan

| Sprint | Minggu | Target |
|--------|--------|--------|
| Sprint 1 | W11 | Planning, Setup, Arsitektur, CI/CD |
| Sprint 2 | W12 | Core Features: CRUD Target & Setoran, Navigation |
| Sprint 3 | W13 | Advanced: Gemini AI, Dark Mode, Statistics |
| Sprint 4 | W14 | Polish, Bug Fixes, Testing |
| Sprint 5 | W15 | Final Preparation & Demo Rehearsal |
| UAS | W16 | Final Demo Day |

Detail breakdown per sprint tersedia di [PROJECT_PLAN.md](./PROJECT_PLAN.md).

---

## 📋 Deliverables Progress

### Sprint 1 ✅
- [x] GitHub repository setup dengan collaborators
- [x] KMP project structure dengan Clean Architecture
- [x] GitHub Actions CI (build + test passing)
- [x] README.md komprehensif
- [x] Project plan document

---

## 📄 Lisensi

Project ini dibuat untuk keperluan tugas akademik mata kuliah **Pengembangan Aplikasi Mobile** di Institut Teknologi Sumatera (ITERA).
