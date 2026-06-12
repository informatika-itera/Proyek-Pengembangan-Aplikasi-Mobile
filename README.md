# 🎬 Rewind — Film & Series Personal Tracker

![CI](https://github.com/choirunnisasy/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

---

## 👥 Team

| Nama | GitHub                                             | NIM | Role |
|------|----------------------------------------------------|-----|------|
| Refi Ikhsanti | [@7refisa](https://github.com/7refisa)             | 123140126 | Domain Layer — Models, Repository Interfaces, Use Cases |
| Choirunnisa Syawaldina | [@choirunnisasy](https://github.com/choirunnisasy) | 123140136 | Data Layer — SQLDelight, Repository Impl, DI Modules |
| Keira Lakeisha Fachra Fuady | [@keiralakeisha](https://github.com/keiralakeisha) | 123140142 | Presentation Layer — Screens, ViewModels, Animations |

> 📚 **Mata Kuliah:** Pengembangan Aplikasi Mobile (IF25-22017) — Kelas RB  
> 🏫 **Institusi:** Program Studi Teknik Informatika, Institut Teknologi Sumatera (ITERA)  
> 👨‍🏫 **Dosen Pengampu:** Muhammad Habib Algifari, S.Kom., M.TI. ([@mh4Scripts](https://github.com/mh4Scripts))

---

## 📖 Description

**Rewind** adalah aplikasi mobile personal tracker untuk film dan series berbasis **Kotlin Multiplatform**. Pengguna bisa mencari film atau series dari TMDB, menambahkannya ke koleksi pribadi, mencatat status tontonan, memberi rating, dan menulis review. Dilengkapi **AI Assistant berbasis Google Gemini** yang membantu menulis review, merekomendasikan tontonan, dan fitur **notifikasi harian** untuk mengingatkan film yang sedang ditonton.

---

## 🌟 Features

### 📦 Core Features
- [x] **Search Film & Series** — Pencarian real-time terintegrasi TMDB API (search, trending, detail)
- [x] **Koleksi Pribadi (CRUD)** — Simpan tontonan dengan status *Watching, Completed, Plan to Watch, On Hold, Dropped*
- [x] **Rating & Review** — Berikan rating dan simpan catatan kesan secara offline
- [x] **Multi-screen Navigation** — Navigasi type-safe antar halaman (Home, Detail, Add/Edit, Search, AI, Profile, Settings)
- [x] **State Management** — UI State menggunakan `Sealed Interface` + `StateFlow` untuk pembaruan reaktif
- [x] **Profile & Statistics** — Statistik tontonan, genre favorit, achievements, dan streak

### 🎁 Bonus Features
- [x] **AI Integration (+10%)** — Google Gemini API sebagai asisten pintar: review writer, chat, summarizer, idea generator, translator
- [x] **Offline First (+5%)** — Aplikasi berfungsi penuh secara offline dengan SQLDelight local database
- [x] **Dark Mode (+5%)** — Tema gelap *twilight palette* yang bisa di-toggle dari Settings
- [x] **Animations (+5%)** — Transisi halaman dan animasi mikro pada komponen UI
- [x] **Notification Daily Watch Reminder** — Notifikasi harian pengingat film yang sedang ditonton via WorkManager

---

## 🎬 Video Demo

### 📹 Final Video Demo
> 🔗 [**FINAL VIDEO DEMO**]( https://youtu.be/6ElHQAbLAns)

### Sprint Demos

| Sprint | Durasi | Fokus | Link |
|--------|--------|-------|------|
| **Sprint 2** | ~1 menit | Navigate all screens, CRUD operations | https://github.com/user-attachments/assets/f2e51409-5ccb-4ddc-a641-064c27d1e5c3 |
| **Sprint 3** | ~1-2 menit | Search, offline mode, bonus features (AI, Dark Mode) | https://github.com/user-attachments/assets/1207e725-c0d3-4f95-8303-b1aa204dc24a |
| **Sprint 4** | ~2 menit | Polished UI, run tests, coverage report | https://github.com/user-attachments/assets/07a0acd1-b2a4-4492-9075-09447b0dc128 |

---

## 📊 Coverage Report

<img width="600" height="338" alt="Coverage Report" src="https://github.com/user-attachments/assets/52f5b318-3207-4e22-8509-1ca7ae53e2ca" />

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Framework** | Kotlin Multiplatform (KMP) + Compose Multiplatform |
| **Architecture** | Clean Architecture (Domain → Data → Presentation) + MVVM |
| **Dependency Injection** | Koin 4.0 |
| **Local Database** | SQLDelight 2.0 |
| **Preferences** | DataStore Preferences |
| **Networking** | Ktor Client 3.0 + Kotlinx Serialization |
| **AI** | Google Gemini API |
| **Movie Data** | TMDB API v3 |
| **Image Loading** | Coil 3 |
| **Async** | Kotlin Coroutines + Flow (StateFlow) |
| **Notifications** | WorkManager + NotificationCompat |
| **Navigation** | Jetbrains Navigation Compose |
| **Testing** | kotlin.test, Turbine, Compose UI Test |
| **Coverage** | Kover |
| **CI/CD** | GitHub Actions |

---

## 📐 Architecture

Aplikasi ini menerapkan **Clean Architecture** dengan pemisahan 3 layer + **MVVM** pattern:

```mermaid
graph TD
    subgraph Presentation["🎨 Presentation Layer"]
        UI["Composables / Screens"]
        VM["ViewModels"]
        US["UiState — Sealed Interface"]
    end

    subgraph Domain["💎 Domain Layer — Pure Kotlin"]
        UC["Use Cases"]
        RI["Repository Interfaces"]
        DM["Domain Models"]
    end

    subgraph Data["💾 Data Layer"]
        RI2["Repository Implementations"]
        subgraph Local["Local Storage"]
            SQL["SQLDelight"]
            DS["DataStore"]
        end
        subgraph Remote["Remote APIs"]
            TMDB["TMDB API — Ktor"]
            GEMINI["Gemini API"]
        end
    end

    subgraph Android["📱 Android Platform"]
        NOTIF["NotificationHelper"]
        WORKER["WatchReminderWorker"]
        WM["WorkManager"]
    end

    UI -->|observes| US
    US -->|driven by| VM
    VM -->|calls| UC
    UC -->|uses| RI
    UC -->|returns| DM
    RI -->|implemented by| RI2
    RI2 --> SQL
    RI2 --> DS
    RI2 --> TMDB
    RI2 --> GEMINI
    WM -->|schedules| WORKER
    WORKER -->|queries| SQL
    WORKER -->|sends| NOTIF
```

### 📁 Project Structure
```
composeApp/src/
├── commonMain/          # Shared code (Domain + Data + Presentation)
│   ├── domain/          # Models, Repository interfaces, Use Cases
│   ├── data/            # Repository impls, SQLDelight, Ktor services, DataStore
│   └── presentation/    # Compose screens, ViewModels, Theme, Navigation
├── androidMain/         # Android-specific (MainActivity, Notifications, DI)
├── iosMain/             # iOS-specific (MainViewController, DI)
├── commonTest/          # Shared unit tests (16+ test classes)
└── androidInstrumentedTest/  # Android UI tests
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Ladybug atau terbaru)
- **JDK 17+**
- **Android SDK** (API 24 - 35)

### Installation

1. **Clone repository:**
   ```bash
   git clone https://github.com/choirunnisasy/Proyek-Pengembangan-Aplikasi-Mobile.git
   cd Proyek-Pengembangan-Aplikasi-Mobile
   ```

2. **Setup API Keys:**
   ```bash
   cp local.properties.example local.properties
   ```
   Edit `local.properties` dan masukkan API key:
   ```properties
   TMDB_API_KEY=your_tmdb_api_key_here
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **Open in Android Studio:**
    - Pilih **Open** → arahkan ke folder project
    - Tunggu **Gradle Sync** selesai

4. **Run on device/emulator:**
    - Pilih konfigurasi `composeApp`
    - Klik **Run** ▶️

### 🧪 Running Tests

```bash
# Unit tests (commonTest)
./gradlew composeApp:allTests

# Android instrumented tests
./gradlew composeApp:connectedDebugAndroidTest

# Coverage report (Kover)
./gradlew composeApp:koverHtmlReport
# Report output: composeApp/build/reports/kover/html/index.html
```

---

## 📸 Screenshots

### 🔔 Splash Screen & Notifikasi Harian
<img width="200" height="444" alt="WhatsApp Image 2026-06-12 at 09 30 02" src="https://github.com/user-attachments/assets/bcff2d5f-7999-4379-b147-7112bd316cae" />
  <img width="200" height="444" alt="WhatsApp Image 2026-06-12 at 09 30 02 (1)" src="https://github.com/user-attachments/assets/c7684f1c-e750-4db1-8fe7-331f6a455d78" />

### 🏠 Home Screen
| 🌑 Dark Mode | ☀️ Light Mode |
|:------------:|:-------------:|
| <img width="200" height="444" src="https://github.com/user-attachments/assets/08c97fe0-827c-4c30-b278-005ff51ea502" /> | <img width="200" height="444" src="https://github.com/user-attachments/assets/287bd82e-a00b-4050-a2b5-c32e96b4ad23" /> |

### 🤖 AI Assistant
| 🌑 Dark Mode | ☀️ Light Mode |
|:------------:|:-------------:|
| <img width="200" height="444" src="https://github.com/user-attachments/assets/0ab4c205-8ea5-4e77-8980-448e6f84bc19" /> | <img width="200" height="444" src="https://github.com/user-attachments/assets/26713c7d-98ef-4508-9c5a-ddb53cceff9f" /> |

### ⚙️ Settings
| 🌑 Dark Mode | ☀️ Light Mode |
|:------------:|:-------------:|
| <img width="200" height="444" src="https://github.com/user-attachments/assets/e705eb68-0821-4787-9bd4-34970617f4ac" /> | <img width="200" height="444" src="https://github.com/user-attachments/assets/8e27d6d0-0c32-4c46-a490-68c2d49d15d5" /> |

### 👤 Profile
| 🌑 Dark Mode | ☀️ Light Mode |
|:------------:|:-------------:|
| <img width="200" height="444" src="https://github.com/user-attachments/assets/1f512471-4f96-4ed9-ba3b-3482e0d5d460" /> | <img width="200" height="444" src="https://github.com/user-attachments/assets/88b25c71-e892-48a2-8a8f-93e62e57257f" /> |

### ✏️ Detail & Edit Screen
| 🌑 Dark Mode | ☀️ Light Mode |
|:------------:|:-------------:|
| <img width="200" height="444" src="https://github.com/user-attachments/assets/a8619daa-2500-4816-ac5f-8abaad0abcad" /> | <img width="200" height="444" src="https://github.com/user-attachments/assets/ec12f661-e56c-4f6d-8858-7caf5e21188f" /> |

---

<p align="center">
  Made with ❤️ by <strong>Team Rewind</strong> — ITERA 2025/2026
</p>
