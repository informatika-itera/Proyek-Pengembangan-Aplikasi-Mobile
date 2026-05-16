# 🌌 Roomie - Solusi Satu Pintu Fasilitas Kampus

**Roomie** adalah platform manajemen fasilitas terintegrasi yang didesain untuk menyederhanakan birokrasi kampus. Proyek ini menggabungkan sistem **Pengaduan Fasilitas** dan **Eksplorasi Ruangan** dalam satu ekosistem digital yang modern, transparan, dan efisien.

Aplikasi ini dibangun menggunakan **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform**, menargetkan platform Android dan iOS dari satu codebase tunggal.

---

## 🚀 Fitur Utama

### 1. 🔐 Role-Based Access Control (RBAC)
- **Dual Perspective:** Alur kerja berbeda untuk **Mahasiswa** (Lapor & Cari) dan **Admin** (Kelola & Update).
- **Secure Session:** Manajemen session menggunakan **DataStore** yang persisten (tetap login meskipun aplikasi ditutup).

### 2. 🏢 Interactive Facility Explorer
- **Building Hierarchy:** Penjelajahan fasilitas berbasis gedung (GKU 1, GKU 2, Gedung E, Gedung F).
- **Seat-Map Grid:** Visualisasi ketersediaan ruangan ala "Seat Booking" yang interaktif.
- **Transparency:** Detail ruangan menampilkan informasi peminjam (jika penuh) atau detail kerusakan (jika perbaikan).

### 3. 🔍 Quick Search
- Akses cepat pencarian ruangan langsung dari Dashboard Beranda untuk meningkatkan efisiensi navigasi.

---

## 🛠️ Tech Stack

- **UI Framework:** Compose Multiplatform (Material Design 3 - Professional Theme)
- **Dependency Injection:** Koin (Modular: Data, Domain, ViewModel)
- **Local Database:** SQLDelight (Offline-First with Reactive Flow)
- **Local Storage:** DataStore (Session & Preferences)
- **Networking:** Ktor Client (Ready for Backend Integration)
- **Concurrency:** Kotlin Coroutines & Flow
- **Architecture:** Clean Architecture (Data, Domain, Presentation) + MVVM + UseCases

---

## 📂 Struktur Proyek (Clean Architecture)

Proyek ini mengikuti prinsip Clean Architecture yang sangat modular:

```text
composeApp/src/commonMain/kotlin/com/example/Roomie/
│
├── core/               # Utilitas umum, extensions, dan Database Driver
├── data/               # RepositoryImpl dan SQLDelight Implementation
├── domain/             # Kontrak Repository, UseCases, dan Domain Models
├── di/                 # Koin Modules (Data, Domain, ViewModel)
├── presentation/       # UI Layer (Screens, ViewModels, Themes)
│   ├── auth/           # Login & Auth Logic
│   ├── home/           # Dashboard User
│   ├── admin/          # Dashboard Admin & Management
│   ├── facility/       # Building List, Grid, Search, Detail
│   ├── report/         # Form Laporan Aspirasi
│   └── profile/        # Profil User & History
└── util/               # AppStrings dan konstanta lainnya
```

---

## 🧪 Kualitas Kode & Testing

- **CI/CD:** Terintegrasi dengan **GitHub Actions** untuk build otomatis pada branch `main` dan `develop`.
- **Unit Testing:** Total **11 unit tests** di `commonTest` mencakup validasi Koin DI, logic ViewModel, dan Business Logic UseCases.

---

## 🏁 Cara Menjalankan

1. **Prasyarat:**
   - Android Studio (versi terbaru)
   - JDK 17 (Set `JAVA_HOME` ke lokasi JDK Anda)
2. **Setup Database:**
   - Jalankan `./gradlew generateSqlDelightInterface` untuk generate class database.
3. **Build & Run:**
   - Pilih target `composeApp` untuk Android atau `iosApp` untuk iOS (di Mac).

---

## 📝 Standar Kontribusi (Commit Message)

Kami menggunakan standar pesan commit:
- `feat`: Fitur baru.
- `fix`: Perbaikan bug.
- `refactor`: Perubahan kode (UseCase/Repository).
- `test`: Penambahan Unit Test.
- `chore`: Update build task/library.

---

## 👥 Pengembang

Aplikasi ini dikembangkan oleh:
1. **Mulya Delani** - 123140049
2. **Nahli Saud Ramdani** - 123140049

---

> **"Roomie: Pinjem ruang gampang, lapor fasilitas cepat."**
