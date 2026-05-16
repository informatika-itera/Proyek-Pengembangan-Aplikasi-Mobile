# BridgeBit
[![CI](https://github.com/arrauf02/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg?branch=project%2F123140002-123140032-BridgeBit)](https://github.com/arrauf02/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml)

## Team
- Ar'rauf Setiawan Muhammad Jabar (Domain + Data layer, Database, API) - @arrauf02
- Muhammad Daffa Hakim Matondang (Presentation layer, UI/UX, Testing) - @dakim777

## Description
BridgeBit adalah aplikasi penerjemah cerdas berbasis AI yang dirancang untuk memberikan terjemahan kontekstual dan mendalam. Menggunakan Kotlin Multiplatform, aplikasi ini bertujuan membantu pengguna memahami nuansa bahasa, istilah teknis, dan menyediakan asisten belajar pribadi melalui asisten AI yang terintegrasi secara cerdas.

## Features
- [ ] **Contextual Translation**: Menerjemahkan teks dengan mempertimbangkan nuansa formal atau santai menggunakan Gemini API.
- [ ] **Phrase Vault**: Menyimpan hasil terjemahan penting ke database lokal untuk akses luring.
- [ ] **Categorization**: Mengelompokkan kata atau frasa tersimpan ke dalam kategori khusus (misal: IT, Medis, Kuliah).
- [ ] **History Dashboard**: Menampilkan riwayat terjemahan terakhir yang dilakukan pengguna.
- [ ] **AI Context Assistant**: Fitur chat interaktif untuk menanyakan detail tata bahasa atau alasan pemilihan kata oleh AI.
- [ ] **Learning Insights**: Statistik harian tentang perkembangan kosakata yang dipelajari.
- [ ] **AI-Generated Quiz**: Kuis otomatis yang dibuat berdasarkan kata-kata yang paling sering dicari atau disimpan oleh pengguna.

## 📁 Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/example/bridgebit/
│   ├── core/                      # Core utilities
│   │   ├── di/                    # Koin modules (AppModule.kt)
│   │   ├── network/               # API Config & HttpClient (Ktor)
│   │   └── util/                  # Extensions & Platform Drivers
│   │
│   ├── data/                      # Data layer
│   │   ├── local/
│   │   │   ├── datastore/         # DataStore preferences (UserPreferences)
│   │   │   └── entity/            # Database mappers (TranslationMapper)
│   │   ├── remote/
│   │   │   ├── api/               # API services (GeminiService)
│   │   │   └── dto/               # Data Transfer Objects (GeminiDto)
│   │   └── repository/            # Repository implementations (Translation, AI)
│   │
│   ├── domain/                    # Domain layer (pure Kotlin)
│   │   ├── model/                 # Domain models (Translation)
│   │   ├── repository/            # Repository interfaces
│   │   └── usecase/               # Business logic (GetAllHistory, SaveTranslation, dll)
│   │
│   ├── presentation/              # Presentation layer
│   │   ├── navigation/            # Navigation setup (AppNavHost, Routes)
│   │   ├── screens/               # Screen composables + ViewModels
│   │   │   ├── dashboard/         # Screen 1: Riwayat & Beranda
│   │   │   ├── workspace/         # Screen 2: Input & Edit Terjemahan
│   │   │   ├── detail/            # Screen 3: Detail Terjemahan
│   │   │   └── ai/                # Screen 4: AI Contextual Assistant
│   │   ├── components/            # Reusable UI components (TranslationCard)
│   │   └── theme/                 # Material 3 theme configurations
│   │
│   └── App.kt                     # Main Compose entry point & Bottom Navigation
│
├── commonMain/sqldelight/         # SQLDelight schema (BridgeBit.sq)
│
├── androidMain/kotlin/            # Android-specific implementations (expect/actual)
└── iosMain/kotlin/                # iOS-specific implementations (expect/actual)
```
## Tech Stack
KMP, Compose Multiplatform, Ktor, SQLDelight, Koin, Gemini API

## Architecture
Aplikasi ini mengadopsi **Clean Architecture** yang dipadukan dengan pola **MVVM** (Model-View-ViewModel) untuk memastikan pemisahan tanggung jawab yang jelas antara logika bisnis, data, dan antarmuka pengguna.

- **Presentation Layer**: UI menggunakan Compose Multiplatform dan State management menggunakan StateFlow di dalam ViewModel.
- **Domain Layer**: Berisi logika bisnis murni, Use Cases, dan interface Repository.
- **Data Layer**: Implementasi Repository yang mengelola sumber data lokal (SQLDelight) dan remote (Ktor untuk Gemini API).

## Setup
1. **Clone repo**
   ```bash
   git clone [https://github.com/arrauf02/Proyek-Pengembangan-Aplikasi-Mobile.git](https://github.com/arrauf02/Proyek-Pengembangan-Aplikasi-Mobile.git)
   cd Proyek-Pengembangan-Aplikasi-Mobile
