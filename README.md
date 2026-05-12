# 🧠 NeuroDeck — AI-Powered Adaptive Flashcard Learning

![CI](https://github.com/fajrifirdaus/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

> Aplikasi flashcard generasi baru yang menggabungkan **algoritma spaced repetition SM-2** dengan **AI Gemini** untuk auto-generate kartu pembelajaran dari materi kuliah mahasiswa.

**Tugas Besar — IF25-22017 Pengembangan Aplikasi Mobile**
Program Studi Teknik Informatika, Institut Teknologi Sumatera (ITERA)
Tahun Akademik Genap 2025/2026

---

## 👥 Tim Pengembang

| Role | Nama | NIM | GitHub |
|------|------|-----|--------|
| Lead Developer & AI Integration | Muhammad Fajri Firdaus | `123140050` | [@fajrifirdaus](https://github.com/fajrifirdaus) |
| UI/UX Developer & QA | Nadya Shafwah Yusuf | `123140167` | [@nadshafy](https://github.com/nadshafy) |

**Dosen Pengampu:** Pak Habib — [@mh4Scripts](https://github.com/mh4Scripts)


---

## 📖 Deskripsi Project

### Masalah yang Diselesaikan

Mahasiswa sering kesulitan mempersiapkan ujian dengan metode belajar yang efektif. Membuat flashcard manual dari materi kuliah memakan waktu, dan aplikasi flashcard yang ada (seperti Anki, Quizlet) tetap memerlukan input kartu secara manual. Akibatnya, banyak yang menyerah sebelum mulai belajar dengan sistem spaced repetition yang terbukti efektif.

### Solusi NeuroDeck

NeuroDeck menghilangkan friksi terbesar dalam belajar dengan flashcard. Mahasiswa cukup:

1. **Paste materi kuliah** (slide, catatan, ringkasan) ke dalam aplikasi
2. **AI Gemini otomatis generate** 5–15 flashcard berkualitas dari materi tersebut
3. **Sistem SM-2 menjadwalkan review** berdasarkan tingkat kesulitan yang dilaporkan user
4. **Statistik adaptif** menunjukkan retention rate dan kartu yang perlu lebih banyak repetisi
5. **AI Tutor Mode** menjelaskan konsep sulit dengan analogi personal saat user stuck

### Target Pengguna

Mahasiswa dan pelajar yang ingin belajar dengan metode spaced repetition tanpa harus repot membuat flashcard manual.

---

## ✨ Fitur

### 🟢 Core Features (Sprint 1–2, Wajib)

- [x] **Deck Management** — Buat, edit, hapus, dan kategorisasi deck flashcard
- [x] **AI-Powered Card Generation** — Auto-generate flashcards dari teks materi via Gemini API
- [x] **Spaced Repetition (SM-2)** — Algoritma penjadwalan adaptif berbasis SuperMemo SM-2
- [x] **Study Session** — Mode belajar dengan card flip animation dan rating (Again/Hard/Good/Easy)
- [x] **Statistics Dashboard** — Retention rate, streak harian, total cards due
- [x] **Offline-First** — Semua data tersimpan lokal via SQLDelight, AI generation membutuhkan koneksi
- [x] **Dark Mode** — Tema light/dark dengan toggle manual
- [x] **CI/CD Pipeline** — GitHub Actions untuk automated build pada setiap push/PR

### 🟡 Pro Upgrades (Sprint 3, Target Bintang 5)

- [ ] **AI Tutor Mode** — Tombol "Explain More" pada kartu sulit → AI menjelaskan konsep dengan analogi yang dipersonalisasi
- [ ] **Advanced Statistics** — Visualisasi forgetting curve dan predicted retention per kartu
- [ ] **Animations Polish** — Transisi halus antar screen, micro-interactions

### 🔵 Stretch Goals (Opsional, jika waktu memungkinkan)

- [ ] **Mini Knowledge Graph** — Visualisasi sederhana relasi antar kartu dalam satu deck
- [ ] **Export to CSV** — Export deck untuk backup eksternal
- [ ] **iOS Support** — Adaptasi untuk platform iOS (saat ini fokus Android)

### 🎁 Estimasi Bonus Nilai (+30%)

| Bonus | Bobot | Status |
|-------|-------|--------|
| AI Integration (Gemini) | +10% | ✅ Inti aplikasi |
| Offline First | +5% | ✅ By design |
| Dark Mode | +5% | 🟡 Sprint 3 |
| Animations | +5% | 🟡 Sprint 3 |
| CI/CD | +5% | ✅ Sudah berjalan |

---

## 🛠️ Tech Stack

| Kategori | Teknologi | Versi |
|----------|-----------|-------|
| **Framework** | Kotlin Multiplatform + Compose Multiplatform | 2.0.21 / 1.7.0 |
| **Architecture** | Clean Architecture + MVVM | — |
| **State Management** | StateFlow + Sealed Class UI State | — |
| **Navigation** | Compose Navigation (Type-safe) | 2.8.0-alpha10 |
| **Async** | Coroutines + Flow | 1.9.0 |
| **Networking** | Ktor Client | 3.0.1 |
| **Local Storage** | SQLDelight | 2.0.2 |
| **Preferences** | DataStore | 1.1.1 |
| **Dependency Injection** | Koin | 4.0.0 |
| **AI Provider** | Google Gemini API (2.5 Flash-Lite) | Free Tier |
| **Serialization** | Kotlinx Serialization JSON | 1.7.3 |
| **Testing** | Kotlin Test + Turbine | 1.2.0 |
| **CI/CD** | GitHub Actions | — |
| **Build Tool** | Gradle (Kotlin DSL) | 8.5.2 |

---

## 🏗️ Arsitektur

NeuroDeck mengadopsi **Clean Architecture** dengan tiga layer terpisah, mengikuti prinsip Dependency Rule (dependencies point inward).

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│   Composables (Screens) ◄──► ViewModels (StateFlow)          │
│   - DeckLibraryScreen    - DeckLibraryViewModel              │
│   - ImportGenerateScreen - ImportViewModel                   │
│   - StudySessionScreen   - StudyViewModel                    │
│   - StatisticsScreen     - StatsViewModel                    │
│   - SettingsScreen       - SettingsViewModel                 │
└───────────────────────────┬─────────────────────────────────┘
                            │ depends on ▼
┌───────────────────────────┴─────────────────────────────────┐
│                      DOMAIN LAYER (Pure Kotlin)              │
│   - Models: Deck, Flashcard, ReviewRecord, StudySession      │
│   - Use Cases:                                               │
│     · GenerateFlashcardsFromTextUseCase                      │
│     · CalculateNextReviewUseCase (SM-2 algorithm)            │
│     · GetDueCardsUseCase                                     │
│     · GetStatisticsUseCase                                   │
│     · ExplainConceptUseCase (Pro upgrade)                    │
│   - Repository Interfaces: DeckRepository, CardRepository,   │
│     AIRepository                                             │
└───────────────────────────┬─────────────────────────────────┘
                            │ implemented by ▼
┌───────────────────────────┴─────────────────────────────────┐
│                       DATA LAYER                             │
│   - Repository Implementations                               │
│   - Local Data Source: SQLDelight (decks, cards, reviews)    │
│   - Remote Data Source: Ktor + Gemini API                    │
│   - DTOs: GeminiRequestDto, GeminiResponseDto, FlashcardDto  │
│   - Mappers: DTO ↔ Domain Model                              │
└─────────────────────────────────────────────────────────────┘
```

### Mengapa Clean Architecture?

- **Testable** — Domain layer pure Kotlin, mudah di-unit test tanpa Android dependency
- **Maintainable** — Setiap perubahan terisolasi di layer yang tepat
- **Scalable** — Mudah menambah fitur baru tanpa memengaruhi yang sudah ada
- **Separation of Concerns** — UI tidak tahu detail database, database tidak tahu detail UI

---

## 📁 Struktur Folder

```
composeApp/src/
├── commonMain/kotlin/com/example/neurodeck/
│   ├── core/                      # Core utilities (cross-cutting)
│   │   ├── di/                    # Koin modules (AppModule, DataModule, ...)
│   │   ├── network/               # Ktor config, error handling
│   │   └── util/                  # Extensions, helpers
│   │
│   ├── data/                      # 📦 DATA LAYER
│   │   ├── local/
│   │   │   ├── dao/               # SQLDelight DAOs (Deck, Card, Review)
│   │   │   ├── entity/            # Database entities
│   │   │   └── datastore/         # DataStore preferences (theme, settings)
│   │   ├── remote/
│   │   │   ├── api/               # GeminiService (Ktor client)
│   │   │   └── dto/               # Request/Response DTOs
│   │   └── repository/            # Repository implementations
│   │
│   ├── domain/                    # 🧠 DOMAIN LAYER (pure Kotlin)
│   │   ├── model/                 # Deck, Flashcard, ReviewRecord, ...
│   │   ├── repository/            # Repository interfaces
│   │   └── usecase/               # Business logic, termasuk SM-2 algorithm
│   │
│   └── presentation/              # 🎨 PRESENTATION LAYER
│       ├── navigation/            # NavHost, Routes (type-safe)
│       ├── theme/                 # Material 3 theme (light/dark)
│       ├── components/            # Reusable composables (FlashcardView, ...)
│       └── screens/               # Feature screens + ViewModels
│           ├── library/           # Deck Library (home)
│           ├── import/            # Import & AI Generate
│           ├── study/             # Study Session
│           ├── statistics/        # Statistics Dashboard
│           └── settings/          # Settings
│
├── commonMain/sqldelight/         # SQLDelight schema (.sq files)
├── androidMain/kotlin/            # Android-specific (expect/actual)
└── iosMain/kotlin/                # iOS-specific (saat ini placeholder)
```

---

## 🗓️ Project Plan & Sprint Breakdown

Project ini dikerjakan dalam **3 sprint efektif** (sesuai keputusan tim dengan strategi *Lite-first → Pro Upgrade*). Mapping ke timeline modul (Sprint 1–5) di-condense karena tim hanya 2 orang.

### 📊 Sprint Summary

| Sprint | Minggu | Fokus | Status |
|--------|--------|-------|--------|
| **Sprint 1** | W11 | Foundation: Setup, Architecture, CI/CD, Deck CRUD | 🟢 In Progress |
| **Sprint 2** | W12–W13 | Core Features: SM-2, AI Generation, Study Session | ⚪ Planned |
| **Sprint 3** | W14–W15 | Pro Upgrades + Polish + Testing | ⚪ Planned |
| **UAS** | W16 | Final Demo Day | ⚪ Planned |

### 📌 Sprint 1: Foundation (Minggu 11)

**Goal:** Aplikasi memiliki struktur Clean Architecture yang solid, bisa CRUD deck, dan CI/CD berjalan.

| Task | PIC | Status |
|------|-----|--------|
| Repository setup di GitHub (private), invite kolaborator | NIM 123140050 | ✅ Done |
| KMP project structure dengan Clean Architecture folders | NIM 123140050 | ✅ Done |
| Konfigurasi Gradle, dependencies, Koin DI setup | NIM 123140050 | ✅ Done |
| GitHub Actions CI workflow (build + APK artifact) | NIM 123140167 | ✅ Done |
| SQLDelight schema awal: `decks`, `cards`, `review_records` | NIM 123140050 | 🟡 In Progress |
| Domain models: `Deck`, `Flashcard`, `ReviewRecord` | NIM 123140050 | 🟡 In Progress |
| Navigation skeleton untuk 5 screens | NIM 123140167 | 🟡 In Progress |
| Deck Library Screen + ViewModel (CRUD deck) | NIM 123140167 | ⚪ Todo |
| README lengkap (dokumen ini) | NIM 123140050 | 🟡 In Progress |

**Deliverables Sprint 1:**
- ✅ GitHub repository dengan semua collaborator
- ✅ KMP project structure dengan Clean Architecture folders
- ✅ GitHub Actions CI passing (badge `passing` di atas)
- 🟡 README lengkap dengan team info, deskripsi, fitur, tech stack, arsitektur
- 🟡 Project plan dengan task assignment (dokumen ini bagian "Project Plan")

### 📌 Sprint 2: Core Features (Minggu 12–13)

**Goal:** End-to-end flow jalan: paste materi → AI generate → study session → SM-2 schedule.

| Task | PIC | Estimasi |
|------|-----|----------|
| Implementasi **algoritma SM-2** di `CalculateNextReviewUseCase` | NIM 123140050 | 6 jam |
| Unit tests untuk SM-2 (minimal 10 test cases) | NIM 123140050 | 4 jam |
| Ktor client untuk Gemini API + JSON schema parsing | NIM 123140050 | 8 jam |
| `GenerateFlashcardsFromTextUseCase` dengan prompt engineering | NIM 123140050 | 4 jam |
| Import & Generate Screen UI + ViewModel | NIM 123140167 | 6 jam |
| Study Session Screen UI dengan card flip animation | NIM 123140167 | 8 jam |
| Integrasi SM-2 dengan Study Session (rating → schedule) | NIM 123140167 | 4 jam |
| Statistics Screen UI dasar (retention, streak, due today) | NIM 123140167 | 6 jam |
| Integration testing | Bersama | 4 jam |

**Deliverables Sprint 2:**
- AI flashcard generation berjalan dengan output JSON structured
- SM-2 algorithm fully tested
- Study session bisa dijalankan end-to-end
- Statistics dasar tampil

### 📌 Sprint 3: Pro Upgrades + Polish (Minggu 14–15)

**Goal:** Tambah fitur upgrade untuk target bintang 5, polish UI, comprehensive testing.

| Task | PIC | Estimasi |
|------|-----|----------|
| **AI Tutor Mode** — tombol "Explain More" + Gemini prompt khusus | NIM 123140050 | 6 jam |
| **Advanced Statistics** — forgetting curve chart (Compose Canvas) | NIM 123140050 | 8 jam |
| Dark Mode implementation (theme switcher) | NIM 123140167 | 4 jam |
| Animations polish (transitions, micro-interactions) | NIM 123140167 | 6 jam |
| Unit tests tambahan untuk Repository & ViewModel (target 10+ total) | Bersama | 6 jam |
| UI tests untuk 3 screen utama | NIM 123140167 | 6 jam |
| Settings Screen (dark mode toggle, about) | NIM 123140167 | 3 jam |
| README finalization, screenshots, demo script | Bersama | 4 jam |
| Demo rehearsal + signed APK | Bersama | 3 jam |

**Deliverables Sprint 3:**
- 2 Pro upgrade features selesai
- Coverage > 50%
- Signed APK siap demo
- Demo script untuk presentasi UAS

### 🛡️ Risk Mitigation

| Risiko | Probabilitas | Mitigasi |
|--------|--------------|----------|
| Gemini API rate limit kena saat development | Medium | Pakai 2 API key (tiap anggota), cache response, mock saat dev |
| Algoritma SM-2 bug | Low | Test-driven, ada referensi resmi SuperMemo |
| Sprint 2 telat → Sprint 3 ketat | Medium | Drop Advanced Statistics dulu, prioritaskan AI Tutor Mode |
| Knowledge graph terlalu kompleks | High | Sudah di-defer ke Stretch Goal, bukan target utama |
| Anggota tim sakit/sibuk | Medium | Pair programming saat fitur kritis, dokumentasi commit yang jelas |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) atau lebih baru
- **JDK 17+** (Temurin recommended)
- **Android SDK** dengan API level 24+ (untuk minimum SDK)
- **Gemini API Key** — gratis di [Google AI Studio](https://aistudio.google.com/)

### Installation

1. **Clone repository**

   ```bash
   git clone https://github.com/fajrifirdaus/Proyek-Pengembangan-Aplikasi-Mobile.git
   cd Proyek-Pengembangan-Aplikasi-Mobile
   ```

2. **Setup `local.properties`**

   ```bash
   cp local.properties.example local.properties
   ```

   Lalu edit `local.properties` dan isi:

   ```properties
   GEMINI_API_KEY=your_api_key_here
   sdk.dir=/path/to/Android/Sdk
   ```

3. **Sync & Build**

   ```bash
   ./gradlew build
   # atau lebih cepat:
   ./gradlew :composeApp:assembleDebug
   ```

4. **Run di Android**

   - Buka project di Android Studio
   - Pilih run configuration `composeApp`
   - Pilih emulator/device → Run
   - Atau via CLI:
     ```bash
     ./gradlew :composeApp:installDebug
     ```

### Cara Mendapatkan Gemini API Key (Gratis)

1. Buka [Google AI Studio](https://aistudio.google.com/)
2. Sign in dengan Google account
3. Klik **"Get API key"** → **"Create API key"**
4. Salin key dan paste ke `local.properties`
5. **Free tier:** 1.000 request/hari untuk Gemini 2.5 Flash-Lite (lebih dari cukup untuk development)

> ⚠️ **Jangan commit `local.properties` ke Git!** File ini sudah ada di `.gitignore`.

---

## 🧪 Testing

```bash
# Run semua test (common + android)
./gradlew allTests

# Run unit test Android saja (lebih cepat)
./gradlew :composeApp:testDebugUnitTest

# Run dengan coverage report
./gradlew :composeApp:testDebugUnitTestCoverage
```

**Target coverage Sprint 3:** > 50%

### Test Strategy

| Layer | Apa yang Ditest | Tools |
|-------|----------------|-------|
| Domain | SM-2 algorithm, Use Cases | kotlin.test |
| Data | Repository, Mappers, API parsing | kotlin.test + MockK |
| Presentation | ViewModel state changes | Turbine (Flow testing) |
| UI | Critical user flows | Compose UI Test |

---

## 🌿 Git Workflow

NeuroDeck menggunakan **Git Flow yang disederhanakan**:

```
main          ← Production-ready, protected, requires PR + CI passing
  └─ develop  ← Integration branch
       └─ feature/<nama-fitur>   ← Individual features
       └─ fix/<nama-bug>          ← Bug fixes
```

### Commit Convention

Mengikuti [Conventional Commits](https://www.conventionalcommits.org/):

| Type | Contoh |
|------|--------|
| `feat:` | `feat: implement SM-2 algorithm in domain layer` |
| `fix:` | `fix: resolve crash when deck is empty` |
| `refactor:` | `refactor: extract Gemini prompt to constants` |
| `test:` | `test: add unit tests for CalculateNextReviewUseCase` |
| `docs:` | `docs: update README with sprint plan` |
| `chore:` | `chore: bump kotlin to 2.0.21` |

> Workflow lengkap (PR template, review process) ada di [`docs/GIT_WORKFLOW.md`](./docs/GIT_WORKFLOW.md).

---

## 📚 Dokumentasi Tambahan

| Dokumen | Deskripsi |
|---------|-----------|
| [🚀 Cara Menjalankan](./docs/CARA_MENJALANKAN.md) | Panduan setup detail step-by-step |
| [🏗️ Struktur Kode](./docs/STRUKTUR_KODE.md) | Penjelasan arsitektur per folder |
| [🌿 Git Workflow](./docs/GIT_WORKFLOW.md) | Branching strategy dan PR workflow |
| [🔧 Troubleshooting](./docs/TROUBLESHOOTING.md) | Solusi untuk masalah umum |
| [📋 Panduan Project](./docs/PANDUAN_PROJECT.md) | Detail penilaian dan timeline |

---

## 🎯 Mapping ke Pembelajaran Mata Kuliah

| Pertemuan | Topik | Penerapan di NeuroDeck |
|-----------|-------|------------------------|
| W1 | Setup Environment | KMP project setup, Gradle config |
| W2 | Kotlin Advanced | Coroutines untuk async, Flow untuk reactive data, Sealed class untuk UI State |
| W3 | Compose Basics | Composables untuk semua screen, Material 3 theming |
| W4 | State & MVVM | StateFlow di setiap ViewModel, UDF pattern |
| W5 | Navigation | NavHost + type-safe routes antar 5 screens |
| W6 | Networking | Ktor Client untuk Gemini API, JSON serialization |
| W7 | Local Storage | SQLDelight untuk decks/cards/reviews, DataStore untuk preferences |
| W8 | Platform Code | expect/actual untuk SQLDelight driver (Android/iOS) |
| W9 | AI Integration | Gemini 2.5 Flash-Lite untuk flashcard generation dan tutor mode |
| W10 | Testing & DI | Koin untuk DI, kotlin.test + Turbine untuk testing |

---

## ⚠️ Disclaimer

- Project ini dibuat untuk **tugas akhir mata kuliah** Pengembangan Aplikasi Mobile, bukan produk komersial.
- **Algoritma SM-2** mengadopsi metode SuperMemo SM-2 oleh Piotr Wozniak (referensi: [super-memory.com](https://www.super-memory.com/english/ol/sm2.htm)). Implementasi original oleh tim.
- **Gemini API** digunakan dengan free tier; tim tidak bertanggung jawab atas perubahan kebijakan dari Google.
- Setiap baris kode original atau dengan attribution yang jelas. **Tidak ada plagiarisme** dari project lain.

---

## 📄 License

MIT License — bebas digunakan untuk pembelajaran.

---

## 🙏 Acknowledgements

- **Pak Habib** ([@mh4Scripts](https://github.com/mh4Scripts)) selaku dosen pengampu dan penyedia template starter
- **SuperMemo Research** atas algoritma SM-2 yang menjadi fondasi spaced repetition
- **Google AI Studio** atas akses Gemini API free tier yang generous
- **JetBrains** atas Kotlin Multiplatform dan Compose Multiplatform

---

<p align="center">
  <strong>Made with ☕ and 🧠 by Tim NeuroDeck</strong><br>
  <em>Institut Teknologi Sumatera — Teknik Informatika 2025/2026</em>
</p>
