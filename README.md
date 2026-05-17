# PocketGuard
![CI](https://github.com/3-206-jefri/Tubes_PAM/actions/workflows/ci.yml/badge.svg)

PocketGuard adalah aplikasi mobile multiplatform (Android & iOS) yang dirancang untuk membantu pengguna mengelola keuangan personal mereka dengan cerdas. Aplikasi ini mengimplementasikan pencatatan pemasukan dan pengeluaran secara offline-first, pelacakan kategori, serta dilengkapi dengan fitur analisis finansial berbasis kecerdasan buatan (AI).

---

## 👥 Tim Pengembang
* **Anggota 1 (Lead & Developer)**: Jefri Wahyu Fernando Sembiring (NIM: 123140026) - @3-206-jefri
* **Anggota 2 (Developer)**: Arta Eka Yuly Rajagukguk  (NIM: 123140209) - @artaeka


---

## 🚀 Fitur Aplikasi

### 1. Minimum Requirements (100% Implemented)
* **Multi-Screen UI**: Memiliki 5 layar fungsional yaitu Beranda (Home), Tambah Transaksi, Detail Transaksi, AI Assistant, dan Pengaturan (Settings).
* **Responsive Layout**: Desain antarmuka modern yang responsif menggunakan Material Design 3.
* **Arsitektur**: Pemisahan komponen kode yang tegas menggunakan pola MVVM dan Clean Architecture (Presentation, Domain, dan Data Layer).
* **Manajemen Data**: Operasi CRUD (Create, Read, Update, Delete) penuh menggunakan database lokal.
* **State Management**: Pengelolaan data UI secara reaktif dan asinkron menggunakan StateFlow dan SharedFlow.
* **Navigation**: Perpindahan antar-layar yang aman menggunakan Type-Safe Navigation dan pengiriman argumen (argument passing).
* **Dependency Injection**: Manajemen dependensi terpusat menggunakan Koin DI.
* **Testing**: Dilengkapi dengan unit testing (>10 tests) pada layer Repository, Use Case, dan ViewModel.

### 2. Bonus Features
* **AI Integration**: Analisis kesehatan keuangan otomatis menggunakan OpenRouter API (menghubungkan model AI gratis seperti Gemini/Mistral).
* **Dark Mode**: Mendukung perubahan tema gelap dan terang secara reaktif berbasis penyimpanan lokal (DataStore Preferences).

---

## 🛠️ Tech Stack
* **Framework**: Kotlin Multiplatform (KMP) & Compose Multiplatform
* **Architecture**: Clean Architecture & MVVM Pattern
* **Concurrency**: Kotlin Coroutines & Flow / StateFlow
* **Database Lokal**: SQLDelight (Offline-First)
* **Penyimpanan Preferensi**: DataStore Preferences
* **Dependency Injection**: Koin DI
* **HTTP Client**: Ktor Client & Kotlinx Serialization
* **Testing Library**: kotlin.test, Turbine (Flow Testing), & MockK

---

## 📐 Arsitektur Proyek
Aplikasi ini menerapkan prinsip **Clean Architecture** dengan aturan dependensi yang mengarah ke dalam (Domain Layer tidak mengetahui tentang Data Layer maupun Presentation Layer):

```text
composeApp/
└── src/
    └── commonMain/kotlin/com/example/pocketguard/
        ├── core/
        │   ├── di/          # Setup Koin Modules (AppModule, NetworkModule, dll)
        │   └── util/        # Database driver factory & utilitas global
        ├── data/
        │   ├── local/       # SQLDelight database & DataStore (Preferences)
        │   ├── remote/      # Ktor HTTP Client, DTOs, & OpenRouter API Service
        │   └── repository/  # Implementasi dari Repository Interface (Data Layer)
        ├── domain/
        │   ├── model/       # Objek data bisnis (Transaction, Category, dll)
        │   ├── repository/  # Kontrak/Interface data (Domain Layer)
        │   └── usecase/     # Logika bisnis spesifik (SaveTransaction, dll)
        └── presentation/
            ├── components/  # Komponen UI reusable (Card, LoadingIndicator)
            ├── navigation/  # NavHost, NavController, & definisi rute layar
            ├── screens/     # Layar fitur (Home, Add, Detail, AI, Settings)
            └── theme/       # Pengaturan warna, tipografi, dan tema aplikasi