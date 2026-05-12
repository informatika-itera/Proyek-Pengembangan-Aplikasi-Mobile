#  AI Travel Planner

Aplikasi **AI Travel Planner** adalah asisten perencana perjalanan berbasis kecerdasan buatan (*Artificial Intelligence*) yang mengotomatisasi penyusunan *itinerary*, pencarian transportasi, rekomendasi akomodasi, dan kalkulasi biaya. Aplikasi ini dibangun secara tangguh menggunakan **Kotlin Multiplatform (KMP)** dengan kepatuhan penuh terhadap prinsip *Clean Architecture* dan pola MVVM untuk memastikan performa *native* di Android dan iOS dari satu basis kode.

Proyek ini dikembangkan sebagai pemenuhan Tugas Mata Kuliah **Pengembangan Aplikasi Mobile** di Institut Teknologi Sumatera (ITERA).

---

## Tim Pengembang

| Nama | NIM |
|------|-----|
| Muhammad Romadhon Santoso | 123140031 |
| Taufik Hidayat NST | 123140188 |

---

## Fitur Utama

- **🤖 Smart AI Itinerary** - Menghasilkan jadwal perjalanan harian yang disesuaikan dengan destinasi, batas *budget*, dan preferensi wisata (seperti *healing* atau kuliner).
- **✈️ Smart Ticket & Hotel Finder** - Rekomendasi tiket transportasi dan akomodasi terkurasi yang disaring ketat berdasarkan parameter *budget* pengguna.
- **📝 CRUD Travel Notes & Wishlist** - Simpan, edit, dan kelola rencana perjalanan atau *itinerary* hasil *generate* AI ke dalam penyimpanan lokal (*offline*) yang aman.
- **💰 Deterministic Budgeting** - Rincian alokasi biaya perjalanan yang presisi, dihitung secara deterministik untuk menghindari halusinasi data AI.
- **🌙 Dynamic Theme** - Dukungan Mode Gelap/Terang adaptif berbasis Compose Multiplatform.

---

## Arsitektur & Teknologi

Proyek ini sangat patuh terhadap prinsip **Clean Architecture**, memisahkan logika antarmuka, aturan bisnis domain, dan sumber data, dikombinasikan dengan delegasi perhitungan berat ke *server* internal.

### Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **UI Framework** | Compose Multiplatform, Material 3 |
| **State Management**| StateFlow, ViewModel |
| **Navigation** | Compose Navigation (Type-safe) |
| **Networking** | Ktor Client (Komunikasi ke Internal Backend) |
| **Local DB** | SQLDelight |
| **Preferences** | DataStore |
| **Dependency Injection**| Koin |
| **AI & Data Source**| Google Gemini API & Travel API (via Backend Proxy) |

---

## Struktur Proyek (Clean Architecture)

Sistem *folder* disusun untuk skalabilitas jangka panjang, memisahkan fungsionalitas inti menjadi modul-modul independen.

```text
composeApp/src/
├── commonMain/kotlin/com/example/wandermind/
│   ├── core/                      # Utilitas Inti (Core)
│   │   ├── di/                    # Modul Koin (Dependency Injection)
│   │   ├── network/               # Konfigurasi Ktor Client & Error Handling
│   │   └── util/                  # Helper & Extensions (Date formatters, Currency)
│   │
│   ├── data/                      # Lapisan Data
│   │   ├── local/
│   │   │   ├── dao/               # SQLDelight DAOs untuk Itinerary & Notes
│   │   │   ├── entity/            # Skema Database Entity
│   │   │   └── datastore/         # DataStore untuk Preferensi User
│   │   ├── remote/
│   │   │   ├── api/               # Layanan Ktor API (berkomunikasi ke Backend internal)
│   │   │   └── dto/               # Data Transfer Objects (TripResponse, BudgetDto)
│   │   └── repository/            # Implementasi dari antarmuka repositori Domain
│   │
│   ├── domain/                    # Lapisan Domain (Pure Kotlin)
│   │   ├── model/                 # Model Domain Murni (Trip, ItineraryDay, Budget)
│   │   ├── repository/            # Antarmuka Repositori
│   │   └── usecase/               # Logika Bisnis (GenerateItineraryUseCase, CalculateBudgetUseCase)
│   │
│   └── presentation/              # Lapisan Presentasi
│       ├── navigation/            # Setup Rute & Parameter Navigasi
│       ├── screens/               # Layar Composable & ViewModels
│       │   ├── home/              # Dashboard & Recent Trips
│       │   ├── planner/           # Form Input Perjalanan & Preferences
│       │   ├── result/            # Tampilan AI Itinerary & Budget Breakdown
│       │   └── notes/             # CRUD Manajemen Catatan Perjalanan
│       ├── components/            # Komponen UI Reusable (TripCard, BudgetBar)
│       └── theme/                 # Material 3 Theme (Light/Dark Mode)
│
├── commonMain/sqldelight/         # Skema tabel SQLDelight (.sq files)
│
├── androidMain/kotlin/            # Implementasi Spesifik Android (expect/actual SqlDriver)
└── iosMain/kotlin/                # Implementasi Spesifik iOS (expect/actual NativeSqliteDriver)
