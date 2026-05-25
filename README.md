# 🌙 SholatYuk - Aplikasi Islam Lengkap

Aplikasi **Islam modern** yang membantu umat Muslim menjalankan ibadah dengan lebih mudah, tepat, dan khusyuk. Dibangun menggunakan **Kotlin Multiplatform (KMP)** & **Compose Multiplatform**.

Aplikasi ini dikembangkan sebagai Tugas Besar (Tubes) mata kuliah **Pengembangan Aplikasi Mobile** - Institut Teknologi Sumatera (ITERA).

---

## 👥 Identitas Mahasiswa

| Item | Detail |
|------|--------|
| **Nama** | Bayu Brigas Novaldi |
| **NIM** | 123140072 |
| **Nama** | Gilang Surya Agung |
| **NIM** | 123140187 |
| **Program Studi** | Teknik Informatika |
| **Mata Kuliah** | Pengembangan Aplikasi Mobile |
| **Institusi** | Institut Teknologi Sumatera (ITERA) |

---

## ✨ Fitur Utama

- 🕌 **Jadwal Sholat Akurat** - Jadwal sholat 5 waktu berdasarkan lokasi GPS
- 🧭 **Arah Kiblat** - Penunjuk arah kiblat otomatis menggunakan GPS
- 📿 **Dzikir & Doa** - Kumpulan dzikir pagi/petang, setelah sholat, dan doa sehari-hari
- 🤖 **IslamAI** - Asisten cerdas untuk menjawab pertanyaan seputar Islam
- 📖 **Al-Quran** - Baca Al-Quran digital (Coming Soon)
- 🔔 **Notifikasi Adzan** - Pengingat waktu sholat dengan suara adzan (Coming Soon)

---

## 🛠 Tech Stack & Arsitektur

### Arsitektur: MVVM + Clean Architecture

Aplikasi mengikuti pola arsitektur MVVM dengan pemisahan layer yang jelas:

- **Presentation Layer**: UI (Compose Multiplatform) dan ViewModel (StateFlow)
- **Domain Layer**: Business logic murni (Models, Repository Interfaces, UseCases)
- **Data Layer**: Implementasi repository, SQLDelight (Local), dan Ktor (Remote)

```
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
| **AI** | Google Gemini API |

---

## 📁 Struktur Project

```
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
│   │   │   └── kajian/          # Dzikir & Doa screen
│   │   └── theme/               # Material 3 theme
│   └── App.kt                   # Entry point
├── commonMain/sqldelight/       # SQLDelight schemas
│   ├── Doa.sq
│   ├── Dzikir.sq
│   ├── PrayerTime.sq
│   ├── ChatHistory.sq
│   └── TasbihCount.sq
├── androidMain/                 # Android-specific implementations
└── iosMain/                     # iOS-specific implementations
```

---

## 📅 Sprint Progress

### ✅ Sprint 1: Foundation (Minggu 11)
- [x] Clone dan setup project
- [x] Pahami struktur folder Clean Architecture
- [x] Setup Koin DI
- [x] Setup CI/CD Pipeline (GitHub Actions)
- [x] Modifikasi tema/warna sesuai SholatYuk

### ✅ Sprint 2: Core Features (Minggu 12)
- [x] Minimal 3 screens (Home, Prayer, Dzikir & Doa)
- [x] Navigation antar screen dengan Bottom Navigation
- [x] Data layer dengan Repository pattern
- [x] Local storage menggunakan SQLDelight
- [x] SQLDelight schema (Doa, Dzikir, PrayerTime, ChatHistory, TasbihCount)
- [x] Mapper layer (DoaMapper, DzikirMapper, PrayerTimeMapper, ChatMessageMapper)
- [x] Domain models (Doa, Dzikir, PrayerTime, ChatMessage, QiblaInfo)
- [x] Repository interfaces & implementations
- [x] UI States (Loading, Success, Error) implemented
- [x] Material 3 theming konsisten

### ⬜ Sprint 3: Advanced Features (Minggu 13)
- [ ] Integrasi GPS untuk jadwal sholat otomatis (Aladhan API)
- [ ] Arah kiblat dengan kompas GPS
- [ ] Notifikasi adzan tepat waktu
- [ ] Search & filter doa/dzikir
- [ ] Offline support (cache-first)
- [ ] IslamAI chat screen lengkap

### ⬜ Sprint 4: Polish & Testing (Minggu 14)
- [ ] Bug fixes & UI polish
- [ ] Animasi dan transisi halus
- [ ] Unit tests (minimal 10)
- [ ] UI tests (minimal 3)
- [ ] Tasbih digital dengan counter
- [ ] Widget layar utama

### ⬜ Sprint 5: Final (Minggu 15)
- [ ] Aplikasi stabil tanpa crash
- [ ] Release APK
- [ ] README lengkap & dokumentasi
- [ ] Slide presentasi
- [ ] Video demo backup

---

## 🚀 Cara Menjalankan

### Prasyarat
- Android Studio Ladybug (2024.2.1) atau lebih baru
- JDK 17
- Git

### Setup

1. **Clone repository:**
```bash
git clone https://github.com/bayybrigas04/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
git checkout project/123140072-123140187-SholatYuk
```

2. **Setup API Key:**
```bash
cp local.properties.example local.properties
```
Edit `local.properties`:
```properties
sdk.dir=/path/to/android/sdk
GEMINI_API_KEY=your_gemini_api_key_here
```
Dapatkan API key gratis di: https://aistudio.google.com/

3. **Build:**
```bash
./gradlew :composeApp:assembleDebug
```

4. **Run:**
   Buka di Android Studio → klik **Run** (▶) atau:
```bash
./gradlew :composeApp:installDebug
```

---

## 📱 Screenshots

> Coming soon - akan ditambahkan setelah Sprint 3

---

## 🔑 Fitur Mendatang

| Fitur | Sprint |
|-------|--------|
| GPS auto-detect lokasi | Sprint 3 |
| Integrasi Aladhan API | Sprint 3 |
| Arah kiblat dengan kompas | Sprint 3 |
| Notifikasi adzan | Sprint 3 |
| IslamAI chat lengkap | Sprint 3 |
| Tasbih digital | Sprint 4 |
| Al-Quran reader | Sprint 4 |
| Widget home screen | Sprint 4 |

---

## 👨‍💻 Pengembang

| Nama | NIM | Kontribusi |
|------|-----|------------|
| Bayu Brigas Novaldi | 123140072 | Domain Layer, Data Layer, SQLDelight, Repository |
| Gilang Surya Agung | 123140187 | Presentation Layer, UI/UX, Navigation, Theming |

---

*Proyek ini adalah bagian dari Tugas Besar mata kuliah Pengembangan Aplikasi Mobile - ITERA*