![CI](https://github.com/3-206-jefri/Tubes_PAM/actions/workflows/ci.yml/badge.svg)

# PocketGuard

PocketGuard adalah aplikasi mobile cerdas berbasis Android yang dirancang khusus untuk membantu pengguna mengelola keuangan personal mereka secara bijak. Aplikasi ini dibangun menggunakan teknologi mutakhir dengan pendekatan *offline-first*, dilengkapi dengan pelacakan anggaran bulanan yang fleksibel, serta asisten keuangan berbasis Kecerdasan Buatan (AI).
<p align = "center">
<img src="composeApp/src/commonMain/composeResources/drawable/Logo_PocketGuard.png" width="300">
</p>


---

## 👥 Tim Pengembang
* **Jefri Wahyu Fernando Sembiring** (NIM: 123140026) - Lead & Developer - [@3-206-jefri](https://github.com/3-206-jefri)
* **Arta Eka Yuly Rajagukguk** (NIM: 123140209) - Developer - [@artaeka](https://github.com/artaeka)

---

## 🚀 Fitur Aplikasi

### 1. Minimum Requirements (100% Implemented)
* **Multi-Screen UI**: Memiliki 5 layar utama fungsional yang saling terintegrasi yaitu Beranda (Home), Tambah Transaksi, Detail Transaksi, AI Assistant, dan Pengaturan (Settings).
* **Responsive Layout**: Desain antarmuka modern, bersih, dan responsif sepenuhnya menggunakan Material Design 3 bawaan Jetpack Compose.
* **Arsitektur Standar Industri**: Pemisahan komponen kode yang tegas, bersih, dan independen menggunakan pola **MVVM** dan **Clean Architecture** (Presentation Layer, Domain Layer, dan Data Layer).
* **Manajemen Data Offline-First**: Operasi CRUD (Create, Read, Update, Delete) penuh yang cepat dan andal menggunakan database lokal.
* **State Management**: Pengelolaan aliran data UI secara reaktif, aman, dan asinkron menggunakan kombinasi `StateFlow` dan `SharedFlow`.
* **Type-Safe Navigation**: Sistem perpindahan antar-layar yang aman dari error tipe data (*compile-time safe*) beserta pengiriman argumen antar komponen.
* **Dependency Injection**: Manajemen dependensi terpusat, ringan, dan rapi menggunakan Koin DI Framework.
* **Testing**: Dilengkapi dengan unit testing (>10 pengujian terotomatisasi) pada komponen penting di layer Repository, Use Case, dan ViewModel.

### 2. Fitur Unggulan & Bonus
* **AI Integration**: Fitur asisten finansial pintar yang terintegrasi langsung dengan **Gemini API** untuk menganalisis tren kesehatan keuangan pengguna serta memberikan rekomendasi penghematan secara otomatis.
* **Dynamic Monthly Budgeting**: Pengaturan batas maksimal pengeluaran (anggaran belanja) yang bersifat independen/berbeda untuk setiap bulan. Dilengkapi dengan komponen `BudgetProgressBar` visual yang dapat berubah warna secara psikologis berdasarkan persentase konsumsi anggaran (Hijau = Aman, Oranye = Peringatan >70%, Merah = Bahaya/Overbudget >90%).
* **Smart Currency Auto-Formatter**: Kotak dialog pengisian nominal anggaran belanja yang dilengkapi dengan pemisah ribuan otomatis secara *real-time* saat diketik (contoh: input `80000000` otomatis terformat menjadi `80.000.000`) serta dilengkapi dengan prefiks mata uang "Rp" permanen untuk mencegah kesalahan pengetikan pengguna.
* **Premium Native Splash Screen**: Menggunakan **Android 12+ Splash Screen API** (`androidx.core:core-splashscreen`) untuk menangani proses inisialisasi awal database dan objek di latar belakang, menghilangkan masalah *white flash*, serta dilengkapi dengan mekanisme penahanan layar selama 1.5 detik demi konsistensi visual *branding* yang premium.
* **Dark Mode Support**: Mendukung perubahan tema gelap dan terang secara dinamis dan reaktif berbasis penyimpanan preferensi lokal.

---

## 🛠️ Tech Stack
* **Framework**: Kotlin Multiplatform (KMP) & Compose Multiplatform
* **Architecture**: Clean Architecture & MVVM Pattern
* **Concurrency**: Kotlin Coroutines & Flow / StateFlow
* **Database Lokal**: SQLDelight (Offline-First)
* **Penyimpanan Preferensi**: DataStore Preferences (Okio)
* **Dependency Injection**: Koin DI
* **HTTP Client**: Ktor Client & Kotlinx Serialization
* **Testing Library**: kotlin.test, Turbine (Flow Testing), & MockK
* **Image Loading**: Coil Multiplatform

---

## 📐 Arsitektur Proyek
Aplikasi ini menerapkan prinsip **Clean Architecture** dengan aturan dependensi yang mengarah ke dalam (Domain Layer bersifat murni dan tidak mengetahui tentang Data Layer maupun Presentation Layer):

```text
composeApp/
└── src/
    └── commonMain/kotlin/com/example/pocketguard/
        ├── core/
        │   ├── di/          # Setup Koin Modules (AppModule, NetworkModule, dll)
        │   └── util/        # Database driver factory & utilitas global
        ├── data/
        │   ├── local/       # SQLDelight database & DataStore (Preferences)
        │   ├── remote/      # Ktor HTTP Client, DTOs, & Gemini AI Service
        │   └── repository/  # Implementasi dari Repository Interface (Data Layer)
        ├── domain/
        │   ├── model/       # Objek data bisnis (Transaction, Category, dll)
        │   ├── repository/  # Kontrak/Interface data (Domain Layer)
        │   └── usecase/     # Logika bisnis spesifik (SaveTransaction, dll)
        └── presentation/
            ├── components/  # Komponen UI reusable (Card, BudgetProgressBar, dll)
            ├── navigation/  # NavHost, NavController, & definisi rute layar
            ├── screens/     # Layar fitur (Home, Add, Detail, AI, Settings)
            └── theme/       # Pengaturan warna, tipografi, dan tema aplikasi