# 📚  LearnCore - Aplikasi untuk fokus pembelajaran
![CI](https://github.com/123140003-MuhammadFadhilahAkbar/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

## 📱 Deskripsi Aplikasi

**LearnCore** adalah aplikasi mobile produktivitas cerdas yang dirancang khusus untuk membantu pelajar dan profesional mengoptimalkan sesi belajar serta mengelola tugas secara efisien. Dengan menggabungkan teknik manajemen waktu yang teruji dan kecerdasan buatan, LearnCore hadir sebagai asisten pribadi yang mengatasi kebiasaan menunda pekerjaan dan menjaga fokus pengguna agar tetap berada pada performa puncak.

---

LearnCore membantu pengguna memprioritaskan pekerjaan dan belajar secara lebih efektif. Dengan visualisasi Matriks Eisenhower interaktif, pengguna dapat melihat secara langsung tugas mana yang harus dikerjakan duluan. Dikombinasikan dengan Pomodoro Timer dan AI Assistant berbasis Gemini, LearnCore hadir sebagai teman belajar yang cerdas dan personal.

> Focus. Prioritize. Learn Smarter.

Aplikasi mobile multiplatform (Android-first) yang membantu mahasiswa dan pelajar untuk **mengelola tugas secara cerdas** menggunakan **Matriks Eisenhower**, dilengkapi dengan **Pomodoro Timer**, **AI Learning Assistant**, dan analisis produktivitas berbasis data lokal.

---
## 👥 Tim
| Nama | NIM | Role |
|------|-----|------|
| Muhammad Fadhilah Akbar | 123140003 | Lead & Android Dev |
| Sigit Kurnia Hartawan | 123140033 | FE Dev & QA |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile

**Dosen:** Pak Habib ([@mh4Scripts](https://github.com/mh4Scripts))

**Institut:** Institut Teknologi Sumatera (ITERA)

---

## ✨ Fitur

### Minimum (Wajib)

- [ ] **Dashboard & Eisenhower Matrix** — Visualisasi prioritas tugas aktif dalam grid 2×2 interaktif; klik kuadran untuk langsung filter daftar tugas
- [ ] **Task List & CRUD** — Tambah, lihat, edit, dan hapus tugas dengan kategori prioritas Eisenhower; navigasi ke halaman detail menggunakan argument passing
- [ ] **Pomodoro Mode** — Timer hitung mundur dengan Circular Progress Indicator; pilih tugas aktif yang sedang dikerjakan; kontrol Play/Pause/Reset
- [ ] **Profile & Settings** — Input nama & status pengguna; kustomisasi durasi Pomodoro via Slider; toggle notifikasi deadline; toggle Dark/Light Mode
- [ ] **AI Learning Assistant** — Chat interaktif dengan Gemini API; prompt cepat (chip suggestion) untuk analisis produktivitas; kirim data statistik Room ke AI untuk evaluasi
- [ ] **Navigasi Multi-Screen** — 5 layar utama: Dashboard, Task List/Detail, Pomodoro, Profile/Settings, AI Chat
- [ ] **State Management** — MVVM + StateFlow untuk semua UI state
- [ ] **Minimal 10 unit tests** + 3 UI tests, coverage > 50%
- [ ] **Koin DI** — Dependency injection setup

### Bonus (Target)

- [ ] **AI Integration (+10%)** — Integrasi Gemini API untuk saran belajar kontekstual dan analisis produktivitas personal
- [ ] **Offline First (+5%)** — Cache data tugas dan riwayat di Room Database; aplikasi tetap berfungsi tanpa internet
- [ ] **Dark Mode (+5%)** — Support tema gelap/terang dengan Material 3
- [ ] **Animations (+5%)** — Animasi transisi antar screen, animasi timer Pomodoro, animasi indikator kuadran Eisenhower
- [ ] **CI/CD (+5%)** — Automated build dan test dengan GitHub Actions

**Total target bonus: +30%**

---

## 🖥️ Rancangan 5 Screen Utama

### 1. Dashboard / Home
Layar utama dengan **Eisenhower Matrix Card** interaktif — grid 2×2 berisi titik-titik berwarna mewakili tugas aktif per kuadran. Klik kuadran menavigasi ke Task List yang sudah terfilter. Tersedia kartu statistik (tugas selesai, waktu fokus) dan tombol FAB untuk langsung ke Pomodoro.

### 2. Task List, CRUD & Detail Page
Daftar tugas dengan `ScrollableTabRow` untuk filter 4 kuadran secara manual. Setiap item memiliki checkbox dan indikator warna prioritas. FAB untuk tambah tugas via BottomSheet. Halaman Detail menampilkan deskripsi lengkap, tenggat, opsi ubah kuadran, dan tombol hapus tugas.

### 3. Pomodoro Mode
UI bersih minim gangguan. Lingkaran besar (`Circular Progress Indicator`) sebagai timer utama dengan warna dinamis (merah/oranye = kerja, hijau/biru = istirahat). Dropdown pemilih tugas aktif dari database. Tombol Play/Pause dan Reset. State tracker menampilkan progres siklus (misal: "Sesi 1 dari 4").

### 4. Profile & Settings
Header profil dengan placeholder icon Material 3. Slider kustomisasi durasi Pomodoro. Toggle notifikasi deadline lokal. Switch Dark/Light Mode.

### 5. AI Learning Assistant
UI chat interaktif (`LazyColumn`) mirip aplikasi messaging. Chip suggestion untuk prompt cepat: "Pecah tugasku hari ini", "Beri ringkasan materi", "Analisa produktivitasku". Saat analisis diminta, data statistik dari Room dikirim ke Gemini untuk evaluasi dan saran peningkatan.

---

## 🏗️ Arsitektur

Menggunakan **Clean Architecture + MVVM** sesuai panduan mata kuliah.

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                 │
│   Screens (Composable) ◄──► ViewModel           │
│             (StateFlow / UDF Pattern)           │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│               DOMAIN LAYER                      │
│   Use Cases ◄──► Repository Interfaces          │
│           (Pure Kotlin, no framework)           │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                DATA LAYER                       │
│   Repository Impl                               │
│   ├── Remote: Ktor + Gemini API (AI)            │
│   └── Local:  Room Database (tasks, stats)      │
└─────────────────────────────────────────────────┘
```
## 📁 Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/example/noteai/
│   ├── core/                      # Core utilities
│   │   ├── di/                    # Koin modules
│   │   ├── network/               # Network config, error handling
│   │   └── util/                  # Extensions, helpers
│   │
│   ├── data/                      # Data layer
│   │   ├── local/
│   │   │   ├── dao/               # SQLDelight DAOs
│   │   │   ├── entity/            # Database entities
│   │   │   └── datastore/         # DataStore preferences
│   │   ├── remote/
│   │   │   ├── api/               # API services (Ktor)
│   │   │   └── dto/               # Data Transfer Objects
│   │   └── repository/            # Repository implementations
│   │
│   ├── domain/                    # Domain layer (pure Kotlin)
│   │   ├── model/                 # Domain models
│   │   ├── repository/            # Repository interfaces
│   │   └── usecase/               # Business logic
│   │
│   └── presentation/              # Presentation layer
│       ├── navigation/            # Navigation setup
│       ├── screens/               # Screen composables + ViewModels
│       │   ├── home/
│       │   ├── addnote/
│       │   ├── detail/
│       │   └── ai/
│       ├── components/            # Reusable UI components
│       └── theme/                 # Material theme
│
├── commonMain/sqldelight/         # SQLDelight schema
│
├── androidMain/kotlin/            # Android-specific (expect/actual)
└── iosMain/kotlin/                # iOS-specific (expect/actual)
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) atau lebih baru
- Xcode 15+ (untuk iOS)
- JDK 17+

### 👥 Ketentuan Kelompok

| Ketentuan | Detail |
|-----------|--------|
| Jumlah Anggota | **1 - 3 mahasiswa** per kelompok |
| Format Branch | `project/[NIM-NIM-...]-[NamaAplikasi]` |

**Contoh Branch:**
- Individu: `project/121140001-TodoMaster`
- 2 orang: `project/121140003-121140004-FitnessApp`
- 3 orang: `project/121140007-121140008-121140009-StudyPlanner`

### Setup

1. **Fork & Clone repository**
   ```bash
   # 1 orang fork, lalu invite anggota lain sebagai collaborator
   # Semua anggota clone dari repo yang di-fork
   git clone https://github.com/USERNAME_FORK/Pryk-PAM.git
   cd Pryk-PAM

   # Buat branch project kelompok
   git checkout -b project/121140003-121140004-FitnessApp
   ```

2. **Setup `local.properties`**

   Salin template, lalu isi API key:
   ```bash
   cp local.properties.example local.properties
   # edit local.properties dan isi GEMINI_API_KEY=...
   ```

   Dapatkan API key gratis di: https://aistudio.google.com/

3. **Sync & Build**
   ```bash
   ./gradlew build              # build semua target
   ./gradlew :composeApp:assembleDebug   # build APK debug saja (lebih cepat)
   ```

4. **Run**
   - **Android**: pilih run configuration `composeApp` di Android Studio, atau
     `./gradlew :composeApp:installDebug` ke emulator/device aktif.
   - **iOS** (opsional): folder `iosApp/` belum disertakan di template ini —
     lihat panduan di [`docs/CARA_MENJALANKAN.md`](./docs/CARA_MENJALANKAN.md#8-menjalankan-ios-lanjutan-opsional).

## 📚 Materi yang Dicakup

| Pertemuan | Topik | File/Folder Reference |
|-----------|-------|----------------------|
| 1 | Setup Environment | Root project setup |
| 2 | Kotlin Lanjutan | `core/util/`, coroutines, Flow |
| 3 | Compose Basics | `presentation/components/` |
| 4 | MVVM & State | `presentation/screens/*/ViewModel.kt` |
| 5 | Navigation | `presentation/navigation/` |
| 6 | Networking | `data/remote/`, Ktor setup |
| 7 | Local Storage | `data/local/`, SQLDelight |
| 8 | Platform Code | `androidMain/`, `iosMain/`, expect/actual |
| 9 | AI Integration | `data/remote/api/GeminiService.kt` |
| 10 | Testing | `commonTest/` |

## 🧪 Testing

```bash
# Run all tests
./gradlew allTests

# Run common tests only
./gradlew :composeApp:testDebugUnitTest
```

## 📝 Tugas Mahasiswa

### Sprint 1: Foundation
- [ ] Clone dan setup project
- [ ] Pahami struktur folder
- [ ] Modifikasi tema/warna

### Sprint 2: Core Features
- [ ] Tambahkan field baru di Note (misal: priority, dueDate)
- [ ] Implementasi fitur kategori/tags
- [ ] Tambahkan validasi input

### Sprint 3: Advanced Features
- [ ] Implementasi search dengan debounce
- [ ] Tambahkan filter dan sort
- [ ] Implementasi offline-first

### Sprint 4: AI & Polish
- [ ] Integrasikan fitur AI baru
- [ ] UI polish dan animasi
- [ ] Tambahkan unit tests

### Sprint 5: Final
- [ ] Bug fixes
- [ ] Dokumentasi
- [ ] Prepare demo

## 🤝 Contributing

1. Fork repository
2. Buat branch fitur (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buat Pull Request

## 📄 License

MIT License - silakan gunakan untuk pembelajaran.

## 👨‍🏫 Dosen Pengampu
### Pak Habib
[GitHub: mh4Scripts](https://github.com/mh4Scripts)

**Program Studi Teknik Informatika**  
Institut Teknologi Sumatera (ITERA)

---

*Template ini dibuat untuk mendukung pembelajaran Pengembangan Aplikasi Mobile dengan Kotlin Multiplatform.*
