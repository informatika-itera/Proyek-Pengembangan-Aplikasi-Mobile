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

## 🎥 Demo Video

> **Demo Sprint 3 (1–2 menit)** — menunjukkan: pencarian deck, mode offline, AI generate, study session SM-2, dan bonus features (dark mode, AI Tutor, foto profil).

📹 **[ [Video demo](https://drive.google.com/file/d/1h6FUHHRWFLjuAneCT7EWsd3IU_rVtIsk/view?usp=drivesdk) ]**

---

## 👥 Tim Pengembang

| Role | Nama | NIM | GitHub |
|------|------|-----|--------|
| Lead Developer & Full-stack developer Integration | Muhammad Fajri Firdaus | `123140050` | [@fajrifirdaus](https://github.com/fajrifirdaus) |
| Full-stack developer & QA | Nadya Shafwah Yusuf | `123140167` | [@nadshafy](https://github.com/nadshafy) |

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

- [x] **AI Tutor Mode** — Full conversational chat dengan Gemini di tab "AI Chat" (5 tab: Home/Decks/AIChat/Stats/Profile), multi-turn history, persistent SQLDelight
- [x] **Advanced Statistics** — Stats Tab dengan period filter, BigStatCards, Activity 7-day bar chart, Card Status breakdown, **Forgetting Curve chart** (Compose Canvas dengan formula Ebbinghaus `R(t) = e^(-t/S)`)
- [x] **Dark Mode** — Theme selector Light/Dark/System di Profile Tab, persisted di DataStore, **reactive** (toggle langsung re-skin seluruh app)
- [x] **Reactive Data** — Stats, achievements, Home greeting auto-update saat review kartu (Flow + `flatMapLatest`, trigger via SQLDelight table invalidation)
- [x] **Profile Photo dari Galeri** — Image picker (Android Photo Picker, expect/actual KMP), foto di-copy ke internal storage app (persisten), render via Coil
- [x] **Hamburger Drawer Redesign** — Reactive profile header (nama + foto auto-update), Vivid Logic styling

### 🔵 Stretch Goals (Opsional, jika waktu memungkinkan)
- [ ] **Mini Knowledge Graph** — Visualisasi sederhana relasi antar kartu dalam satu deck
- [ ] **Export to CSV** — Export deck untuk backup eksternal

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
| **AI Provider** | Google Gemini API (2.5 Flash) | Free Tier |
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
│       ├── navigation/            # NavHost, Routes, AppDrawer, BottomNav
│       ├── theme/                 # Material 3 theme (Vivid Logic light + Midnight dark)
│       ├── components/            # Reusable composables (RatingButtons, StickyNoteBadge, ...)
│       ├── util/                  # expect/actual ImagePicker (gallery photo)
│       └── screens/               # Feature screens + ViewModels
│           ├── home/              # Home dashboard (reactive greeting)
│           ├── decklibrary/       # Deck Library + search
│           ├── importgenerate/    # Import & AI Generate
│           ├── studysession/      # Study Session (SM-2 flashcards)
│           ├── stats/             # Statistics + Forgetting Curve
│           ├── profile/           # Profile + achievements (reactive)
│           ├── editprofile/       # Edit profile + photo picker
│           ├── aichat/            # AI Tutor chat
│           ├── createdeck/        # Create deck form
│           ├── cardlist/          # Card list per deck
│           ├── addcard/ editcard/ # Card CRUD
│           └── about/             # About screen
│
├── commonMain/sqldelight/         # SQLDelight schema (.sq files)
├── commonMain/composeResources/   # Fonts (Bricolage, JetBrains Mono), drawables, logo
├── androidMain/kotlin/            # Android actual (DB driver, DataStore, ImagePicker)
└── iosMain/kotlin/                # iOS actual: DB driver + DataStore real, ImagePicker stub
```

---

## 🗓️ Project Plan & Sprint Breakdown

### 📊 Sprint Summary

| Sprint | Minggu | Fokus | Status |
|--------|--------|-------|--------|
| **Sprint 1** | W11 | Foundation: Setup, Architecture, CI/CD, Deck CRUD | ✅ Done |
| **Sprint 2** | W12–W13 | Core Features: SM-2, AI Generation, 5-Tab Navigation, AI Chat, Stats | ✅ Done |
| **Sprint 3** | W14–W15 | Advanced Features: Reactive UI, Profile Photo, Drawer Redesign, UI Polish | ✅ Done |
| **UAS** | W16 | Final Demo Day | ⚪ Planned |

### 📌 Sprint 1: Foundation (Minggu 11)

**Goal:** Aplikasi memiliki struktur Clean Architecture yang solid, bisa CRUD deck, dan CI/CD berjalan.

| Task | PIC | Status |
|------|-----|--------|
| Repository setup di GitHub (private), invite kolaborator | NIM 123140050 | ✅ Done |
| KMP project structure dengan Clean Architecture folders | NIM 123140050 | ✅ Done |
| Konfigurasi Gradle, dependencies, Koin DI setup | NIM 123140050 | ✅ Done |
| GitHub Actions CI workflow (build + APK artifact) | NIM 123140167 | ✅ Done |
| SQLDelight schema: `decks`, `cards`, `review_records` (+ `chat_messages`) | NIM 123140050 | ✅ Done |
| Domain models: `Deck`, `Card`, `ReviewRecord`, `CardReviewState`, `UserProfile` | NIM 123140050 | ✅ Done |
| Navigation skeleton untuk 5 screens | NIM 123140167 | ✅ Done |
| Deck Library Screen + ViewModel (CRUD deck) | NIM 123140167 | ✅ Done |
| README lengkap  | NIM 123140050 | ✅ Done |

**Deliverables Sprint 1:**
- ✅ GitHub repository dengan semua collaborator
- ✅ KMP project structure dengan Clean Architecture folders
- ✅ GitHub Actions CI passing (badge `passing` di atas)
- ✅ README lengkap dengan team info, deskripsi, fitur, tech stack, arsitektur
- ✅ Project plan dengan task assignment

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

---

### ✅ Sprint 2 Delivery Report (Selesai)

> **Status:** 🎉 Selesai 100% — semua target Sprint 2 tercapai PLUS sebagian besar cicilan Sprint 3.

#### 📦 Yang Dikirimkan

**5 Tab Utama (Bottom Navigation):**

| Tab | Fitur Utama |
|-----|-------------|
| 🏠 **Home** | Greeting dinamis (pagi/siang/sore/malam), 3 stat mini (Due/Streak/Hari Ini), Quick Actions, Continue Learning (recent 3 decks), Tips of the Day |
| 📚 **Decks** | Search bar real-time, AI Generate banner, FAB Create Deck, edit/delete dengan dialog konfirmasi |
| 💬 **AI Chat** | Multi-turn conversation dengan Gemini, persisten SQLDelight, suggestion chips, typing indicator animasi, clear history |
| 📊 **Stats** | Period filter (7/30/90 hari/All), 4 BigStatCard, Activity bar chart 7 hari (Canvas), Card Status breakdown, **Forgetting Curve chart** (formula Ebbinghaus) |
| 👤 **Profile** | User info editable, achievement stats, theme selector (Light/Dark/System), reset data dengan double-confirm |

**Sub-Screens (Stack Navigation):**

- `CreateDeckScreen` — Form 2-step (nama+desc → pilih Manual/AI Generate)
- `ImportGenerateScreen` — State machine 6-phase: Input → Generating → Preview (editable) → Saving → Done → Error
- `EditProfileScreen` — Form edit dengan character counter
- `AboutScreen` — App version, team info, tech stack, GitHub branch
- `CardListScreen`, `EditCardScreen`, `StudySessionScreen` (existing dari Sprint 1)

**Komponen Reusable:**

- `AppNavHost` — ModalNavigationDrawer + Scaffold(TopBar + BottomNav) + NavHost (proper popUpTo/saveState pattern)
- `AppTopBar`, `BottomNavBar`, `AppDrawer`, `BottomNavItem`
- `ConfirmDialog`, `SectionTitle`, `LoadingIndicator`, `EmptyState`, `ErrorMessage`

#### 🏗️ Tambahan Data Layer

- `ReviewRecordRepository` — query stats: total reviews, daily activity, accuracy, streak (timezone-aware)
- `UserPreferencesRepository` — DataStore-backed (profile + theme), expect/actual cross-platform
- `ChatRepository` — chat history SQLDelight + composite send (persist user msg → AI call → persist reply)
- `AIRepository.chatWithHistory()` — multi-turn Gemini API integration

#### 🧠 Highlight Teknis

1. **Algoritma SM-2 implementasi penuh** — `CalculateNextReviewUseCase` dengan unit test
2. **Gemini API 2 use case** — structured JSON output (flashcards) + plain text streaming (chat)
3. **Clean Architecture konsisten** — domain interface terpisah dari data implementation
4. **Reactive UI** — Flow + StateFlow + `combine()` untuk derived UI state
5. **Forgetting Curve visualization** — Compose Canvas drawing 3 kurva overlay dengan formula `R(t) = e^(-t/S)`, sweet spot SM-2 85% retention dashed line

#### 📊 Statistik Code

- **~50+ file baru** dibuat di Sprint 2 (UI screens, components, ViewModels, repositories)
- **5 tab utama** + **8 sub-screens** (jauh melebihi minimum 3 screen rubrik)
- **3 SQL table baru** (`ReviewRecordEntity` extended, `ChatMessageEntity` baru)
- **2 DataStore preferences** (profile + theme)

#### 🎯 Cicilan Sprint 3 yang Sudah Tercicil di Sprint 2

| Fitur | Status | Lokasi |
|-------|--------|--------|
| Search/Filter (25%) | ✅ Done | Decks SearchBar + Stats PeriodFilter |
| Additional Screen (15%) | ✅ Done | Home + Profile + AIChat + Stats (4 tambahan) |
| API Integration (25%) | ✅ Done | Gemini API 2 use case (generate + chat) |
| Offline Support (20%) | ✅ Done | SQLDelight source of truth |
| Bonus Dark Mode (+5%) | ✅ Done | Theme selector di Profile, DataStore persistence |
| Bonus AI Tutor Mode (+10%) | ✅ Done | AI Chat tab full conversational |
| Bonus Forgetting Curve (+5%) | ✅ Done | Stats tab section "Sains di Balik NeuroDeck" |

#### 🐛 Issues yang Di-Resolve

- SQLDelight cache bug saat tambah `.sq` file baru → solved dengan rename DB file (`neurodeck.db` → `neurodeck_v2.db`)
- MIUI aggressive caching (uninstall app tidak hapus DB file) → solved dengan force fresh DB via filename change
- Gemini v1beta tidak support `role=system` di multi-turn → solved dengan prepend system prompt sebagai `role=user`

### ✅ Sprint 3 Delivery Report (Selesai)

> **Status:** 🎉 Selesai — semua komponen rubrik Sprint 3 terpenuhi, beberapa melebihi minimum.

#### 📦 Yang Dikirimkan di Sprint 3

**1. Reactive Data Layer (auto-update tanpa refresh)**

Sebelumnya Stats & achievements pakai snapshot (`.first()`) — tidak update setelah review kartu. Sekarang fully reactive:

| ViewModel | Pattern | Efek |
|-----------|---------|------|
| `StatsViewModel` | `combine(observeAllDecks, period).flatMapLatest { computeStats() }` | Statistik auto-update saat review |
| `ProfileViewModel` | `observeAllDecks().mapLatest { computeAchievements() }` | Achievement (Decks/Cards/Reviews/Streak) auto-update |
| `HomeViewModel` | `combine(observeAllDecks, observeProfile).mapLatest { ... }` | Greeting + due count + streak reaktif |

**Mekanisme:** `observeAllDecks()` query melakukan `LEFT JOIN CardEntity`. SQLDelight invalidate Flow di level tabel, jadi setiap update SM-2 state pada CardEntity (saat review) otomatis trigger re-emit → recompute → UI update.

**2. Greeting Personalisasi**

Home greeting yang sebelumnya hardcoded `"Mahasiswa"` sekarang membaca `UserProfile.name` dari DataStore — ganti nama di EditProfile langsung tercermin di Home (reactive).

**3. Profile Photo dari Galeri (expect/actual KMP)**

- `commonMain`: `expect fun rememberImagePickerLauncher()`
- `androidMain`: Android Photo Picker (`ActivityResultContracts.PickVisualMedia`) — tanpa permission, system picker
- `iosMain`: stub (project fokus Android)
- Foto di-**copy ke internal storage app** (`filesDir/avatar_*.jpg`) supaya path permanen (content:// URI bisa expire), render via Coil 3 `AsyncImage`
- EditProfile: avatar tappable → galeri → pilih → preview → simpan → persisten setelah restart; tombol "Hapus Foto"

**4. Hamburger Drawer Redesign + Reactive**

- Profile header drawer observe `observeProfile()` via Koin — nama + foto auto-update
- Desain Vivid Logic: header card `primaryContainer` rounded + border, avatar ring + Coil photo, sticky badge "LIHAT PROFIL", nama panjang di-ellipsis (responsif)

**5. UI Polish Konsisten (Vivid Logic Design Language)**

Seluruh screen di-poles ke design language konsisten: `OutlinedCard` border tegas, sticky note badges, section titles uppercase, accent strips, custom font (Bricolage Grotesque + JetBrains Mono), splash screen, launcher icon. Study Session redesigned dengan rating pills pastel + interval preview SM-2.

#### 🎯 Pemenuhan Rubrik Sprint 3

| Komponen Rubrik | Bobot | Status | Bukti |
|-----------------|-------|--------|-------|
| **Search/Filter** | 25% | ✅ | Decks SearchBar (real-time) + Stats PeriodFilter (7/30/90/All) |
| **API/Enhanced Local** | 25% | ✅ | Gemini API (generate + chat) dengan error handling lengkap (network + HTTP 400–503) |
| **Offline Support** | 20% | ✅ | SQLDelight source-of-truth, semua CRUD/study/stats offline; AI degradasi anggun |
| **Additional Screen** | 15% | ✅ | 4 screen tambahan (Profile, Stats, AIChat, About) — minimum 1 |
| **Bonus Features** | 15% | ✅ | 4 bonus (Dark mode, AI Tutor, Forgetting Curve, Photo picker) — minimum 1 |

#### 🐛 Issues Sprint 3 yang Di-Resolve

- Kotlin incremental cache corruption (`Storage already registered`) saat heavy refactoring → solved dengan clear `build/` + `.gradle/` + restart daemon
- Dark mode tidak berefek → root cause `App.kt` panggil `neurodeckTheme()` tanpa param (selalu `isSystemInDarkTheme()`) → fixed wire `observeThemeMode()` → `darkTheme` Boolean
- Type mismatch reactive Flow → return type `computeStats` diubah ke parent `StatsUiState` supaya `.catch` bisa emit `Error`



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
5. **Free tier:** 1.000 request/hari untuk Gemini 2.5 Flash

> ⚠️ **Jangan commit `local.properties` ke Git!** File ini sudah ada di `.gitignore`.

---

## 🔌 API Documentation

NeuroDeck mengintegrasikan **Google Gemini API** untuk dua fitur: AI flashcard generation dan AI Tutor chat.

### Endpoint

| Item | Nilai |
|------|-------|
| **Base URL** | `https://generativelanguage.googleapis.com/v1beta/` |
| **Model** | `gemini-2.5-flash` |
| **Full endpoint** | `/v1beta/models/gemini-2.5-flash:generateContent` |
| **Method** | `POST` |
| **Auth** | API key via query parameter `?key=<GEMINI_API_KEY>` |
| **Content-Type** | `application/json` |
| **Client** | Ktor 3.0.1 dengan `ContentNegotiation` (kotlinx-serialization) + `HttpTimeout` 15s |

### API Key Flow

```
local.properties (GEMINI_API_KEY=...)
   └─► build.gradle.kts (baca via Properties, inject ke BuildConfig)
        └─► ApiConfig (expect/actual) expose key ke commonMain
             └─► GeminiService (Ktor) attach key sebagai query param
```

Key **tidak pernah di-hardcode** di source — hanya dari `local.properties` (gitignored).

### Request Format (generateContent)

```json
{
  "contents": [
    { "role": "user", "parts": [{ "text": "<prompt + materi>" }] }
  ],
  "generationConfig": {
    "temperature": 0.7,
    "responseMimeType": "application/json"
  }
}
```

> **Catatan:** Gemini `v1beta` tidak mendukung `role=system` di multi-turn. System prompt di-prepend sebagai `role=user` di awal history (lihat `AIRepository.chatWithHistory()`).

### Dua Use Case

| Use Case | Output | Keterangan |
|----------|--------|------------|
| **Generate Flashcards** | JSON terstruktur (array `{front, back}`) | `responseMimeType: application/json`, di-parse ke `FlashcardDto` |
| **AI Tutor Chat** | Plain text | Multi-turn dengan history, persisted di SQLDelight |

### Error Handling

`GeminiService` memetakan error ke pesan Bahasa Indonesia yang user-friendly:

| Kondisi | Pesan ke User |
|---------|---------------|
| Network exception (no internet) | "Koneksi gagal. Pastikan internet Anda menyala dan coba lagi." |
| HTTP 400 | "Permintaan tidak valid. Mungkin materi terlalu panjang atau berisi konten yang tidak diizinkan." |
| HTTP 401 / 403 | "API key tidak valid atau sudah expired. Hubungi developer." |
| HTTP 429 | "Rate limit tercapai. Tunggu beberapa menit lalu coba lagi." |
| HTTP 500 / 502 / 503 | "Server AI sedang bermasalah. Coba lagi nanti." |

Error message disimpan sebagai assistant turn (`isError=true`) dan ditampilkan di bubble merah, sehingga chat tetap usable offline (graceful degradation).

### Rate Limit (Free Tier)

Gemini 2.5 Flash free tier: ~15 request/menit, ~1.500 request/hari. Cukup untuk development + demo. Tim memakai 2 API key (tiap anggota) untuk menghindari limit saat development.

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

**Target coverage Sprint 3:**

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
| W9 | AI Integration | Gemini 2.5 Flash untuk flashcard generation dan tutor mode |
| W10 | Testing & DI | Koin untuk DI, kotlin.test + Turbine untuk testing |

---

## ⚠️ Disclaimer

- Project ini dibuat untuk **tugas akhir mata kuliah** Pengembangan Aplikasi Mobile, bukan produk komersial.
- **Algoritma SM-2** mengadopsi metode SuperMemo SM-2 oleh Piotr Wozniak (referensi: [super-memory.com](https://www.super-memory.com/english/ol/sm2.htm)).
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
