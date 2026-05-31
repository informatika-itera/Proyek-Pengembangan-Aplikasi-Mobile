---

## 🛠️ Tech Stack

| Layer | Technology | Keterangan |
|-------|------------|------------|
| **UI** | Compose Multiplatform | Material 3, Dark Mode |
| **State Management** | StateFlow + ViewModel | MVVM pattern |
| **Navigation** | Jetpack Navigation Compose | Type-safe + animasi slide/fade |
| **Networking** | Ktor Client | OkHttp (Android), Darwin (iOS) |
| **Local Database** | SQLDelight | Offline cache, CRUD |
| **Backend** | Supabase | Auth, PostgreSQL, Storage |
| **Preferences** | DataStore | User settings |
| **DI** | Koin | viewModelOf, singleOf, factory |
| **AI** | Google Gemini API | gemini-2.5-flash |
| **Testing** | Kotlin Test + Turbine | Flow testing |
| **CI/CD** | GitHub Actions | Auto build + test |

---

## 🚀 Cara Menjalankan

### Prerequisites

| Software | Versi Minimum |
|----------|---------------|
| Android Studio | Ladybug 2024.2.1+ |
| JDK | 17 |
| Android SDK | API 24+ |

### Setup

**1. Clone repository**
```bash
git clone https://github.com/MNAUFALFAKMAL/InventRa.git
cd InventRa
```

**2. Buat file `local.properties`**
```properties
sdk.dir=/path/to/android/sdk
GEMINI_API_KEY=your_gemini_api_key_here
SUPABASE_URL=your_supabase_url_here
SUPABASE_ANON_KEY=your_supabase_anon_key_here
```

Dapatkan Gemini API key di: https://aistudio.google.com/

**3. Build & Run**
```bash
# Build APK debug
./gradlew :composeApp:assembleDebug

# Install ke device/emulator
./gradlew :composeApp:installDebug
```

Atau buka di Android Studio → pilih `composeApp` → Run.

---

## 📅 Project Plan & Task Assignment

### Sprint 1 — Foundation (Minggu 11)

| Task | PIC | Status |
|------|-----|--------|
| Setup GitHub repository & branch | MNAUFALFAKMAL | ✅ |
| KMP project structure & Clean Architecture | MNAUFALFAKMAL | ✅ |
| GitHub Actions CI/CD setup | MNAUFALFAKMAL | ✅ |
| SQLDelight schema (Item.sq, BorrowRecord.sq) | MNAUFALFAKMAL | ✅ |
| Domain models & repository interfaces | MNAUFALFAKMAL | ✅ |
| Koin DI setup (AppModule, androidModule) | nblable | ✅ |
| Material 3 Theme & brand color HMIF | nblable | ✅ |
| README awal & dokumentasi | nblable | ✅ |
| DataStore setup (UserPreferences) | nblable | ✅ |

### Sprint 2 — Core Features (Minggu 12)

| Task | PIC | Status |
|------|-----|--------|
| ItemRepositoryImpl + BorrowRepositoryImpl | MNAUFALFAKMAL | ✅ |
| AuthRepositoryImpl (Supabase Auth) | MNAUFALFAKMAL | ✅ |
| AppNavHost + Routes + navigation arguments | MNAUFALFAKMAL | ✅ |
| AddEditItemScreen + ViewModel (CRUD) | MNAUFALFAKMAL | ✅ |
| ItemDetailScreen + ViewModel | MNAUFALFAKMAL | ✅ |
| DashboardScreen + ViewModel | nblable | ✅ |
| CatalogScreen + ViewModel | nblable | ✅ |
| HistoryScreen + ViewModel | nblable | ✅ |
| LoginScreen + ViewModel | nblable | ✅ |
| ProfileScreen (Dark Mode toggle) | nblable | ✅ |
| Reusable components (GlassCard, ItemCard, dll) | nblable | ✅ |

### Sprint 3 — Advanced Features (Minggu 13)

| Task | PIC | Status |
|------|-----|--------|
| Offline-first: ItemRepositoryImpl dengan SQLDelight cache | MNAUFALFAKMAL | ✅ |
| Offline-first: BorrowRepositoryImpl dengan SQLDelight cache | MNAUFALFAKMAL | ✅ |
| Background sync Supabase ↔ SQLDelight | MNAUFALFAKMAL | ✅ |
| AppNavHost animasi transisi (slide + fade) | MNAUFALFAKMAL | ✅ |
| SplashScreen dengan fade animation | MNAUFALFAKMAL | ✅ |
| Search debounce 300ms di CatalogViewModel | nblable | ✅ |
| AIInventoryScreen connect ke AIInventoryViewModel | nblable | ✅ |
| EmptyState dengan fade + scale animation | nblable | ✅ |
| Unit tests: ItemRepositoryTest, CatalogViewModelTest | nblable | ✅ |
| Unit tests: DashboardViewModelTest, BorrowRepositoryTest, ItemUseCaseTest | nblable | ✅ |

### Sprint 4 — Polish & Testing (Minggu 14) — In Progress

| Task | PIC | Status |
|------|-----|--------|
| Bug fixes semua screen | MNAUFALFAKMAL | 🔄 |
| UI polish & consistency check | nblable | 🔄 |
| Performance optimization | MNAUFALFAKMAL | 🔄 |
| Complete unit test coverage | nblable | 🔄 |

---

## 🧪 Testing

```bash
# Run semua test
./gradlew allTests

# Run unit test Android debug
./gradlew :composeApp:testDebugUnitTest
```

### Test Coverage

| File | Tests | Coverage |
|------|-------|----------|
| `ItemRepositoryTest` | 6 tests | CRUD, search, delete |
| `CatalogViewModelTest` | 4 tests | UI states, search, filter |
| `DashboardViewModelTest` | 4 tests | Stats, overdue, active borrowings |
| `BorrowRepositoryTest` | 5 tests | CRUD, status filter, return |
| `ItemUseCaseTest` | 6 tests | Sort, validation, delete |
| **Total** | **25 tests** | |

---

## 📊 Progress Sprint

### Sprint 1: Foundation ✅
| Deliverable | Status |
|-------------|--------|
| GitHub repository & collaborators | ✅ |
| KMP project structure clean architecture | ✅ |
| GitHub Actions CI passing | ✅ |
| README lengkap dengan team info & project plan | ✅ |
| Koin DI setup (Bonus) | ✅ |

### Sprint 2: Core Features ✅
| Deliverable | Status |
|-------------|--------|
| 3+ working screens | ✅ 8 screens |
| Navigation dengan arguments | ✅ |
| Repository pattern + SQLDelight | ✅ |
| CRUD operations | ✅ |
| UI States Loading/Success/Error/Empty | ✅ |
| Semua fitur accessible (no dead ends) | ✅ |
| API Integration Supabase (Bonus) | ✅ |

### Sprint 3: Advanced Features ✅
| Deliverable | Status |
|-------------|--------|
| Search/filter dengan debounce | ✅ |
| API Integration Supabase + Gemini | ✅ |
| Offline support SQLDelight cache | ✅ |
| Additional screen (Profile) | ✅ |
| Bonus: Dark mode, animasi, AI Assistant, Splash | ✅ |

### Sprint 4: Polish & Testing 🔄
| Deliverable | Status |
|-------------|--------|
| Bug fixes | 🔄 |
| UI Polish | 🔄 |
| 25 unit tests | ✅ |
| Performance optimization | 🔄 |

---

## 🎥 Demo Video

| Sprint | Link |
|--------|------|
| Sprint 2 & 3 | [Google Drive](https://drive.google.com/file/d/1-H1Nh0JPQjPFbAODvzFAu8Zf7AJ7mHbc/view?usp=drive_link) |

---

## 📚 Referensi

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin DI](https://insert-koin.io/)
- [Supabase Kotlin](https://supabase.com/docs/reference/kotlin/introduction)
- [Google Gemini API](https://ai.google.dev/docs)

---

*InventRa — Solusi Inventaris Digital untuk HMIF ITERA*

```
┌─────────────────────────────────────────┐
│           PRESENTATION LAYER            │
│   Screen (Composable) ↔ ViewModel       │
│         StateFlow, UI Events            │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│             DOMAIN LAYER                │
│    Use Cases (Business Logic)           │
│    Repository Interfaces                │
│    Domain Models                        │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│              DATA LAYER                 │
│   Repository Implementations            │
│   SQLDelight (Local) ↔ Supabase (Remote)│
│   Gemini API (AI)                       │
└─────────────────────────────────────────┘
```

### Struktur Folder

```
composeApp/src/commonMain/kotlin/com/example/inventra/
├── core/
│   ├── di/              # Koin modules (AppModule)
│   └── network/         # HttpClientFactory, SupabaseClient, ApiConfig
├── data/
│   ├── local/
│   │   ├── entity/      # ItemMapper, BorrowRecordMapper
│   │   └── datastore/   # UserPreferences, DataStoreFactory
│   ├── remote/
│   │   ├── api/         # GeminiService
│   │   └── dto/         # GeminiDto, SupabaseDto
│   └── repository/      # ItemRepositoryImpl, BorrowRepositoryImpl, AuthRepositoryImpl, AIRepositoryImpl
├── domain/
│   ├── model/           # Item, BorrowRecord, User
│   ├── repository/      # ItemRepository, BorrowRepository, AuthRepository, AIRepository
│   └── usecase/         # GetAllItemsUseCase, SaveItemUseCase, DeleteItemUseCase, SearchItemsUseCase
└── presentation/
├── components/      # GlassCard, ItemCard, EmptyState, LoadingIndicator, StatusBadge, dll
├── navigation/      # Routes, AppNavHost (animasi transisi)
├── screens/
│   ├── splash/      # SplashScreen
│   ├── auth/        # LoginScreen, LoginViewModel
│   ├── dashboard/   # DashboardScreen, DashboardViewModel
│   ├── catalog/     # CatalogScreen, CatalogViewModel
│   ├── detail/      # ItemDetailScreen, ItemDetailViewModel
│   ├── addedit/     # AddEditItemScreen, AddEditItemViewModel
│   ├── history/     # HistoryScreen, HistoryViewModel
│   ├── ai/          # AIInventoryScreen, AIInventoryViewModel
│   └── profile/     # ProfileScreen
└── theme/           # InventRaTheme, Dark Mode, Color Scheme
```

---

## 🛠️ Tech Stack

| Layer | Technology | Keterangan |
|-------|------------|------------|
| **UI** | Compose Multiplatform | Material 3, Dark Mode |
| **State Management** | StateFlow + ViewModel | MVVM pattern |
| **Navigation** | Jetpack Navigation Compose | Type-safe + animasi slide/fade |
| **Networking** | Ktor Client | OkHttp (Android), Darwin (iOS) |
| **Local Database** | SQLDelight | Offline cache, CRUD |
| **Backend** | Supabase | Auth, PostgreSQL, Storage |
| **Preferences** | DataStore | User settings |
| **DI** | Koin | viewModelOf, singleOf, factory |
| **AI** | Google Gemini API | gemini-2.5-flash |
| **Testing** | Kotlin Test + Turbine | Flow testing |
| **CI/CD** | GitHub Actions | Auto build + test |

---

## 🚀 Cara Menjalankan

### Prerequisites

| Software | Versi Minimum |
|----------|---------------|
| Android Studio | Ladybug 2024.2.1+ |
| JDK | 17 |
| Android SDK | API 24+ |

### Setup

**1. Clone repository**
```bash
git clone https://github.com/MNAUFALFAKMAL/InventRa.git
cd InventRa
```

**2. Buat file `local.properties`**
```properties
sdk.dir=/path/to/android/sdk
GEMINI_API_KEY=your_gemini_api_key_here
SUPABASE_URL=your_supabase_url_here
SUPABASE_ANON_KEY=your_supabase_anon_key_here
```

Dapatkan Gemini API key di: https://aistudio.google.com/

**3. Build & Run**
```bash
# Build APK debug
./gradlew :composeApp:assembleDebug

# Install ke device/emulator
./gradlew :composeApp:installDebug
```

Atau buka di Android Studio → pilih `composeApp` → Run.

---

## 📅 Project Plan & Task Assignment

### Sprint 1 — Foundation (Minggu 11)

| Task | PIC | Status |
|------|-----|--------|
| Setup GitHub repository & branch | MNAUFALFAKMAL | ✅ |
| KMP project structure & Clean Architecture | MNAUFALFAKMAL | ✅ |
| GitHub Actions CI/CD setup | MNAUFALFAKMAL | ✅ |
| SQLDelight schema (Item.sq, BorrowRecord.sq) | MNAUFALFAKMAL | ✅ |
| Domain models & repository interfaces | MNAUFALFAKMAL | ✅ |
| Koin DI setup (AppModule, androidModule) | nblable | ✅ |
| Material 3 Theme & brand color HMIF | nblable | ✅ |
| README awal & dokumentasi | nblable | ✅ |
| DataStore setup (UserPreferences) | nblable | ✅ |

### Sprint 2 — Core Features (Minggu 12)

| Task | PIC | Status |
|------|-----|--------|
| ItemRepositoryImpl + BorrowRepositoryImpl | MNAUFALFAKMAL | ✅ |
| AuthRepositoryImpl (Supabase Auth) | MNAUFALFAKMAL | ✅ |
| AppNavHost + Routes + navigation arguments | MNAUFALFAKMAL | ✅ |
| AddEditItemScreen + ViewModel (CRUD) | MNAUFALFAKMAL | ✅ |
| ItemDetailScreen + ViewModel | MNAUFALFAKMAL | ✅ |
| DashboardScreen + ViewModel | nblable | ✅ |
| CatalogScreen + ViewModel | nblable | ✅ |
| HistoryScreen + ViewModel | nblable | ✅ |
| LoginScreen + ViewModel | nblable | ✅ |
| ProfileScreen (Dark Mode toggle) | nblable | ✅ |
| Reusable components (GlassCard, ItemCard, dll) | nblable | ✅ |

### Sprint 3 — Advanced Features (Minggu 13)

| Task | PIC | Status |
|------|-----|--------|
| Offline-first: ItemRepositoryImpl dengan SQLDelight cache | MNAUFALFAKMAL | ✅ |
| Offline-first: BorrowRepositoryImpl dengan SQLDelight cache | MNAUFALFAKMAL | ✅ |
| Background sync Supabase ↔ SQLDelight | MNAUFALFAKMAL | ✅ |
| AppNavHost animasi transisi (slide + fade) | MNAUFALFAKMAL | ✅ |
| SplashScreen dengan fade animation | MNAUFALFAKMAL | ✅ |
| Search debounce 300ms di CatalogViewModel | nblable | ✅ |
| AIInventoryScreen connect ke AIInventoryViewModel | nblable | ✅ |
| EmptyState dengan fade + scale animation | nblable | ✅ |
| Unit tests: ItemRepositoryTest, CatalogViewModelTest | nblable | ✅ |
| Unit tests: DashboardViewModelTest, BorrowRepositoryTest, ItemUseCaseTest | nblable | ✅ |

### Sprint 4 — Polish & Testing (Minggu 14) — In Progress

| Task | PIC | Status |
|------|-----|--------|
| Bug fixes semua screen | MNAUFALFAKMAL | 🔄 |
| UI polish & consistency check | nblable | 🔄 |
| Performance optimization | MNAUFALFAKMAL | 🔄 |
| Complete unit test coverage | nblable | 🔄 |

---

## 🧪 Testing

```bash
# Run semua test
./gradlew allTests

# Run unit test Android debug
./gradlew :composeApp:testDebugUnitTest
```

### Test Coverage

| File | Tests | Coverage |
|------|-------|----------|
| `ItemRepositoryTest` | 6 tests | CRUD, search, delete |
| `CatalogViewModelTest` | 4 tests | UI states, search, filter |
| `DashboardViewModelTest` | 4 tests | Stats, overdue, active borrowings |
| `BorrowRepositoryTest` | 5 tests | CRUD, status filter, return |
| `ItemUseCaseTest` | 6 tests | Sort, validation, delete |
| **Total** | **25 tests** | |

---

## 📊 Progress Sprint

### Sprint 1: Foundation ✅
| Deliverable | Status |
|-------------|--------|
| GitHub repository & collaborators | ✅ |
| KMP project structure clean architecture | ✅ |
| GitHub Actions CI passing | ✅ |
| README lengkap dengan team info & project plan | ✅ |
| Koin DI setup (Bonus) | ✅ |

### Sprint 2: Core Features ✅
| Deliverable | Status |
|-------------|--------|
| 3+ working screens | ✅ 8 screens |
| Navigation dengan arguments | ✅ |
| Repository pattern + SQLDelight | ✅ |
| CRUD operations | ✅ |
| UI States Loading/Success/Error/Empty | ✅ |
| Semua fitur accessible (no dead ends) | ✅ |
| API Integration Supabase (Bonus) | ✅ |

### Sprint 3: Advanced Features ✅
| Deliverable | Status |
|-------------|--------|
| Search/filter dengan debounce | ✅ |
| API Integration Supabase + Gemini | ✅ |
| Offline support SQLDelight cache | ✅ |
| Additional screen (Profile) | ✅ |
| Bonus: Dark mode, animasi, AI Assistant, Splash | ✅ |

### Sprint 4: Polish & Testing 🔄
| Deliverable | Status |
|-------------|--------|
| Bug fixes | 🔄 |
| UI Polish | 🔄 |
| 25 unit tests | ✅ |
| Performance optimization | 🔄 |

---

## 🎥 Demo Video

| Sprint | Link |
|--------|------|
| Sprint 2 & 3 | [Google Drive](https://drive.google.com/file/d/1-H1Nh0JPQjPFbAODvzFAu8Zf7AJ7mHbc/view?usp=drive_link) |

---

## 📚 Referensi

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin DI](https://insert-koin.io/)
- [Supabase Kotlin](https://supabase.com/docs/reference/kotlin/introduction)
- [Google Gemini API](https://ai.google.dev/docs)

---

*InventRa — Solusi Inventaris Digital untuk HMIF ITERA*