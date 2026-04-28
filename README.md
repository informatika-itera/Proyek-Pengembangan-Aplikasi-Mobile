# 📱 NoteAI - KMP Project Template

Template project **Kotlin Multiplatform** untuk mata kuliah **Pengembangan Aplikasi Mobile** di ITERA.

Aplikasi Notes dengan fitur AI untuk membantu mahasiswa memahami arsitektur dan pattern yang digunakan dalam pengembangan aplikasi mobile modern.

> **📚 Dokumentasi Lengkap**
> 
> | Dokumen | Deskripsi |
> |---------|-----------|
> | [🚀 Cara Menjalankan](./docs/CARA_MENJALANKAN.md) | **BACA INI DULU!** Panduan setup dan running aplikasi |
> | [📋 Panduan Project](./docs/PANDUAN_PROJECT.md) | Informasi lengkap tentang project, timeline, dan penilaian |
> | [🌿 Git Workflow](./docs/GIT_WORKFLOW.md) | Cara menggunakan Git dan branching strategy |
> | [📜 Aturan Modifikasi](./docs/ATURAN_MODIFIKASI.md) | Apa yang boleh dan tidak boleh dimodifikasi |
> | [🏗️ Struktur Kode](./docs/STRUKTUR_KODE.md) | Penjelasan arsitektur dan struktur folder |
> | [🔧 Troubleshooting](./docs/TROUBLESHOOTING.md) | Solusi untuk masalah umum |

## ✨ Fitur Aplikasi

- 📝 **CRUD Notes** - Tambah, edit, hapus, dan lihat catatan
- 🔍 **Search & Filter** - Cari dan filter notes berdasarkan kategori
- 🤖 **AI Assistant** - Summarize, generate ideas, improve writing
- 🌙 **Dark Mode** - Tema gelap/terang
- 📱 **Cross-Platform** - Android & iOS dari satu codebase

## 🏗️ Arsitektur & Teknologi

### Clean Architecture + MVVM

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌───────────────┐        ┌───────────────┐                 │
│  │    Screen     │◄──────►│   ViewModel   │                 │
│  │  (Composable) │ State  │  (StateFlow)  │                 │
│  └───────────────┘        └───────┬───────┘                 │
└───────────────────────────────────┼─────────────────────────┘
                                    │
┌───────────────────────────────────┼─────────────────────────┐
│                      DOMAIN LAYER │                          │
│                    ┌──────────────▼──────────────┐          │
│                    │         Use Cases           │          │
│                    │    (Business Logic)         │          │
│                    └──────────────┬──────────────┘          │
│                    ┌──────────────▼──────────────┐          │
│                    │    Repository Interface     │          │
│                    └──────────────┬──────────────┘          │
└───────────────────────────────────┼─────────────────────────┘
                                    │
┌───────────────────────────────────┼─────────────────────────┐
│                       DATA LAYER  │                          │
│                    ┌──────────────▼──────────────┐          │
│                    │   Repository Implementation │          │
│                    └──────────────┬──────────────┘          │
│              ┌────────────────────┼────────────────────┐    │
│              │                    │                    │    │
│        ┌─────▼─────┐        ┌─────▼─────┐       ┌─────▼────┐│
│        │  SQLDelight│        │   Ktor   │       │ DataStore││
│        │  (Local)  │        │ (Remote) │       │  (Prefs) ││
│        └───────────┘        └──────────┘       └──────────┘│
└─────────────────────────────────────────────────────────────┘
```

### Tech Stack

| Layer | Technology |
|-------|------------|
| **UI** | Compose Multiplatform, Material 3 |
| **State** | StateFlow, ViewModel |
| **Navigation** | Compose Navigation (Type-safe) |
| **Networking** | Ktor Client |
| **Local DB** | SQLDelight |
| **Preferences** | DataStore |
| **DI** | Koin |
| **AI** | Google Gemini API |
| **Testing** | Kotlin Test, Turbine |

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
