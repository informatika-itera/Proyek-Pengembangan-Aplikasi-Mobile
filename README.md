# 🌌 Roomie - Solusi Satu Pintu Fasilitas Kampus

**Roomie** adalah platform manajemen fasilitas terintegrasi yang didesain untuk menyederhanakan birokrasi kampus. Proyek ini menggabungkan sistem **Pengaduan Fasilitas** dan **Peminjaman Ruangan** dalam satu ekosistem digital yang modern, transparan, dan efisien.

Aplikasi ini dibangun menggunakan **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform**, menargetkan platform Android dan iOS dari satu codebase tunggal.

---

## 🚀 Fitur Utama

### 1. 📢 Smart Reporting System
- **Real-time Tracking:** Pantau laporan dari status "Menunggu", "Diproses", hingga "Selesai".
- **Evidence-Based:** Unggah foto bukti kerusakan langsung dari perangkat.
- **Transparansi:** Laporan dikelola secara terbuka untuk memastikan akuntabilitas.

### 2. 🗺️ Interactive Campus Map
- **Mapping Lokasi:** Visualisasi lokasi gedung, laboratorium, dan fasilitas lainnya di ITERA.
- **Integrasi Detail:** Akses langsung ke informasi detail fasilitas melalui titik koordinat di peta.

### 3. 📅 Centralized Facility Booking (Coming Soon)
- **Live Calendar:** Cek ketersediaan ruangan secara *real-time*.
- **E-Permit:** Generate surat izin peminjaman digital setelah mendapatkan persetujuan.

---

## 🛠️ Tech Stack

- **UI Framework:** Compose Multiplatform (Material Design 3)
- **Dependency Injection:** Koin
- **Local Database:** SQLDelight
- **Local Storage:** DataStore (Preferences)
- **Networking:** Ktor (JSON Serialization)
- **Concurrency:** Kotlin Coroutines & Flow
- **Navigation:** Compose Navigation
- **Architecture:** Clean Architecture (Data, Domain, Presentation) + MVVM

---

## 📂 Struktur Proyek (Clean Architecture)

Proyek ini mengikuti prinsip Clean Architecture untuk memastikan kode modular, mudah diuji, dan skalabel:

```text
composeApp/src/commonMain/kotlin/com/example/Roomie/
│
├── core/               # Utilitas umum, ekstensi, dan basis navigasi
├── data/               # Implementasi Repository, API Service, dan Data Sources
├── domain/             # Kontrak Repository, UseCases, dan Domain Models
├── di/                 # Konfigurasi Koin (DataModule, DomainModule, ViewModelModule)
├── presentation/       # UI Layer (Screens, ViewModels, Components, Themes)
│   ├── home/
│   ├── map/
│   ├── facility/
│   ├── report/
│   └── profile/
└── util/               # AppStrings dan konstanta lainnya
```

---

## 🧪 Kualitas Kode & Testing

- **CI/CD:** Terintegrasi dengan **GitHub Actions** untuk build otomatis dan testing pada setiap *push* atau *pull request*.
- **Unit Testing:** Mendukung pengujian logic bisnis di layer Domain dan ViewModel.
- **Koin Validation:** Menggunakan `checkModules()` untuk memastikan integritas konfigurasi Dependency Injection.

---

## 🏁 Cara Menjalankan

1. **Prasyarat:**
   - Android Studio (versi terbaru)
   - JDK 17
2. **Kloning Repositori:**
   ```bash
   git clone https://github.com/username/Roomie.git
   ```
3. **Build & Run:**
   - Pilih target `composeApp` di Android Studio.
   - Jalankan pada Emulator Android atau Simulator iOS.

---

## 📝 Standar Kontribusi (Commit Message)

Kami menggunakan standar pesan commit berikut:
- `feat`: Fitur baru.
- `fix`: Perbaikan bug.
- `refactor`: Perubahan kode yang tidak mengubah fungsi.
- `chore`: Update build task, library, dll.
- `docs`: Dokumentasi.

---

> **"Roomie: Pinjem ruang gampang, lapor fasilitas cepat."**
