# Struktur Kode dan Arsitektur

Dokumen ini menjelaskan struktur kode dan arsitektur yang digunakan dalam proyek My Bawang Gacha.

## Arsitektur: Clean Architecture + MVVM

Proyek ini menggunakan Clean Architecture yang dikombinasikan dengan pattern MVVM (Model-View-ViewModel). Pemisahan kode dilakukan menjadi tiga layer utama:

```
┌──────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                         │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                         UI (Screen)                         │  │
│  │            Composable functions, UI state rendering         │  │
│  └────────────────────────────────────────────────────────────┘  │
│                              ▲ │                                  │
│                    State     │ │ Events                           │
│                              │ ▼                                  │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                        ViewModel                            │  │
│  │         StateFlow, event handling, UI state management      │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
                               ▲ │
                               │ │ Calls
                               │ ▼
┌──────────────────────────────────────────────────────────────────┐
│                          DOMAIN LAYER                             │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                        Use Cases                            │  │
│  │               Business logic, orchestration                 │  │
│  └────────────────────────────────────────────────────────────┘  │
│                              ▲ │                                  │
│                              │ │                                  │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                  Repository Interface                       │  │
│  │                    Contract/abstraction                     │  │
│  └────────────────────────────────────────────────────────────┘  │
│                                                                   │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                       Domain Models                         │  │
│  │               Pure Kotlin data classes                      │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
                               ▲ │
                               │ │ Implements
                               │ ▼
┌──────────────────────────────────────────────────────────────────┐
│                           DATA LAYER                              │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                Repository Implementation                    │  │
│  │            Coordinates data sources, caching                │  │
│  └────────────────────────────────────────────────────────────┘  │
│              ┌───────────────┼───────────────┐                    │
│              ▼               ▼               ▼                    │
│  ┌──────────────────┐ ┌──────────────┐ ┌─────────────────┐       │
│  │   Local Source   │ │ Remote Source│ │   DataStore     │       │
│  │    (SQLDelight)  │ │    (Ktor)    │ │  (Preferences)  │       │
│  └──────────────────┘ └──────────────┘ └─────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

## Struktur Folder Detail

```
composeApp/src/
│
├── commonMain/kotlin/id/my/sinanonym/mybawanggacha/   # Shared code
│   │
│   ├── core/                                 # Utilitas Inti
│   │   ├── build/                            # Info build aplikasi
│   │   ├── coroutines/                       # Dispatchers platform-specific
│   │   ├── di/                               # Dependency Injection (Koin)
│   │   │   ├── AppModule.kt                  # Koin root module
│   │   │   ├── DatabaseModule.kt             # Konfigurasi DB
│   │   │   ├── NetworkModule.kt              # Ktor client & logger
│   │   │   ├── PreferencesModule.kt          # DataStore settings
│   │   │   ├── RepositoryModule.kt           # Binding repository impl
│   │   │   ├── UseCaseModule.kt              # Use case DI
│   │   │   └── ViewModelModule.kt            # Register ViewModels
│   │   ├── network/                          # Konfigurasi jaringan
│   │   │   ├── ApiConfig.kt                  #expect: API Keys
│   │   │   └── HttpClientFactory.kt          # Ktor client setup
│   │   └── util/                             # Utilitas umum
│   │       └── DatabaseDriverFactory.kt      # expect: DB Driver
│   │
│   ├── data/                                 # Data Layer
│   │   ├── local/                            # Sumber data lokal
│   │   │   ├── datastore/                    # Konfigurasi UserPreferences
│   │   │   ├── entity/                       # Mappers database ke domain
│   │   │   └── source/                       # Local caching data sources
│   │   ├── remote/                           # Sumber data remote (API)
│   │   │   ├── gemini/                       # Layanan Gemini API & DTO
│   │   │   ├── github/                       # Cek rilis update & DTO
│   │   │   └── jikan/                        # Klien Jikan (MyAnimeList) API, DTO, Mapper
│   │   └── repository/                       # Implementasi Repositori
│   │       ├── ai/                           # AIRepositoryImpl, AiChatSessionRepositoryImpl
│   │       ├── jikan/                        # Caching & JikanRequestUsageRepositoryImpl
│   │       ├── anime/                        # AnimeRepositoryImpl
│   │       ├── manga/                        # MangaRepositoryImpl
│   │       ├── library/                      # LibraryRepositoryImpl
│   │       ├── note/                         # NoteRepositoryImpl
│   │       └── settings/                     # SettingsRepositoryImpl
│   │
│   ├── domain/                               # Domain Layer (Pure Kotlin)
│   │   ├── ai/                               # Repositori chat & model AI
│   │   ├── anime/                            # Repositori & model Anime
│   │   ├── manga/                            # Repositori & model Manga
│   │   ├── library/                          # Repositori & model Library
│   │   ├── gacha/                            # Repositori & Use Case Gacha
│   │   ├── note/                             # Repositori, usecase & model Note
│   │   ├── search/                           # Repositori & model Search
│   │   └── settings/                         # Repositori & model Settings/Dashboard
│   │
│   ├── presentation/                         # Presentation Layer (UI)
│   │   ├── components/                       # Reusable UI widgets
│   │   ├── navigation/                       # Routes.kt & AppNavHost.kt
│   │   ├── theme/                            # Material Theme & Palet Warna
│   │   └── screens/                          # Layar-layar aplikasi
│   │       ├── discover/                     # Halaman rekomendasi utama
│   │       ├── search/                       # Pencarian gabungan
│   │       ├── library/                      # Pengelola daftar tonton/baca
│   │       ├── anime/                        # List anime & detail anime
│   │       ├── manga/                        # List manga & detail manga
│   │       ├── gacha/                        # Gacha rekomendasi acak
│   │       ├── notes/                        # Catatan lokal (tambah/detail)
│   │       └── settings/                     # Pengaturan API key, tema & statistik
│   │
│   └── App.kt                                # Poin masuk Compose UI
│
├── commonMain/sqldelight/                    # Skema Database SQLDelight
│   └── id/my/sinanonym/mybawanggacha/data/local/
│       ├── AiChat.sq                         # Riwayat chat asisten AI
│       ├── Anime.sq                          # Caching detail anime
│       ├── Library.sq                        # Tabel library pengguna
│       ├── Manga.sq                          # Caching detail manga
│       ├── MediaCache.sq                     # Caching halaman pencarian & rekomendasi
│       └── Note.sq                           # Catatan lokal pengguna
│
├── androidApp/                               # Modul khusus Android
│   └── src/main/kotlin/id/my/sinanonym/mybawanggacha/
│       ├── MainActivity.kt                   # Activity utama
│       └── MBGApplication.kt                 # Inisialisasi Koin & API Key
│
└── iosApp/                                   # Modul khusus iOS
    └── iosApp/
        └── iOSApp.swift                      # Poin masuk aplikasi iOS Swift
```

## Penjelasan Setiap Layer

### 1. Domain Layer

Layer terdalam yang berisi logika bisnis murni. Layer ini ditulis sepenuhnya dalam bahasa Kotlin murni tanpa ketergantungan pada pustaka pihak ketiga atau framework platform tertentu.

- Models: Representasi data bisnis murni. Contoh: `Anime.kt`, `Manga.kt`, `Library.kt`, `Note.kt`.
- Repository Interface: Abstraksi akses data yang mendefinisikan kontrak operasi tanpa merinci mekanisme penyimpanan. Contoh: `AnimeRepository.kt`.
- Use Cases: Logika bisnis spesifik untuk alur kerja tertentu. Contoh: `RunGachaUseCase.kt`.

### 2. Data Layer

Layer yang mengimplementasikan antarmuka repositori dari domain layer. Bertanggung jawab menentukan sumber data (lokal dari database SQLDelight, berkas DataStore preferensi, atau remote dari Ktor client API).

- Mappers: Mengubah representasi data dari database (Entity) atau API (DTO) ke dalam format objek Domain Model.
- Repository Implementation: Mengoordinasikan alur data, melakukan caching data lokal sebelum melakukan panggilan API (offline support), dan menyimpan kuota penggunaan API. Contoh: `LibraryRepositoryImpl.kt`.

### 3. Presentation Layer

Layer untuk mengatur visualisasi antarmuka pengguna (UI). Ditulis menggunakan Compose Multiplatform dan mengikuti pola reactive state management menggunakan StateFlow.

- UI State: Membungkus seluruh state data layar ke dalam objek tunggal (misal: Loading, Success, Error, Empty).
- ViewModels: Mengelola state UI, meluncurkan coroutine, dan memanggil Use Cases atau Repositori sebagai respon terhadap aksi pengguna.
- Composable Screens: Mengumpulkan data state dari ViewModel dan merendernya ke layar.

## Dependency Flow

Semua dependensi mengarah ke dalam (Domain Layer). Presentation layer dan Data layer sama-sama mengenal Domain layer, tetapi Domain layer tidak mengenal layer luarnya.

```
Presentation Layer ─────────► Domain Layer ◄───────── Data Layer
```

- Presentation layer mengetahui objek Domain Model dan antarmuka repositori.
- Data layer mengetahui objek Domain Model dan mengimplementasikan antarmuka repositori.
- Domain layer berdiri sendiri (pure Kotlin).

## expect/actual Pattern

Pattern ini digunakan untuk mengeksekusi kode spesifik platform dalam basis data bersama:

```kotlin
// commonMain (Deklarasi expect)
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// androidMain (Implementasi actual Android)
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NoteDatabase.Schema, context, "mybawanggacha.db")
    }
}

// iosMain (Implementasi actual iOS)
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(NoteDatabase.Schema, "mybawanggacha.db")
    }
}
```

## Dependency Injection dengan Koin

Registrasi objek dideklarasikan secara modular di dalam paket `core/di/`:

- `DatabaseModule`: Menyediakan `NoteDatabase` via driver lokal platform.
- `NetworkModule`: Menyediakan instansi `HttpClient` tunggal dengan konfigurasi Logger Ktor.
- `PreferencesModule`: Menyediakan instansi `DataStore` untuk pengaturan persistensi lokal.
- `RepositoryModule`: Mengikat kelas implementasi repositori ke antarmuka repositorinya.
- `UseCaseModule`: Menyediakan dependensi instansi Use Cases.
- `ViewModelModule`: Mendaftarkan seluruh ViewModels agar siklus hidupnya dapat diatur otomatis oleh Koin.

Inisialisasi DI dipicu pada poin masuk aplikasi masing-masing platform:
- Android: Di dalam kelas `MBGApplication` menggunakan callback `androidContext(this)`.
- iOS: Di dalam kelas `MainViewController.kt` menggunakan fungsi `initKoinIOS()`.

