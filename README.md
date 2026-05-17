# 🚂 RailLog Nusantara - Manajemen Logistik Kereta Api

> **Aplikasi Logistik Manufaktur Kereta Api Berbasis Adaptive UI dengan Dukungan Kecerdasan Buatan untuk Efisiensi Rantai Pasok dan Verifikasi Dokumen Teknis**

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-brightgreen?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/AI-Google%20Gemini-4285F4?style=for-the-badge&logo=google" />
  <img src="https://img.shields.io/badge/Sprint-1%20%E2%9C%85-success?style=for-the-badge" />
</p>

---

## 📌 Identitas Proyek

| Item | Detail |
|------|--------|
| **Nama Aplikasi** | RailLog Nusantara |
| **Mata Kuliah** | Pengembangan Aplikasi Mobile (IF25-22017) |
| **Program Studi** | Teknik Informatika |
| **Institusi** | Institut Teknologi Sumatera (ITERA) |
| **Dosen Pengampu** | Pak Habib (mh4Scripts) |

---

## 👥 Tim Pengembang

| NIM | Nama | Peran |
|-----|------|-------|
| 123140057 | Muhammad Nurikhsan | Domain Layer, Database, API Integration |
| 123140068 | Giovan Lado | Presentation Layer, UI/UX, Navigation |

> **Branch:** `project/123140057-123140068-RailLog`

---

## 🎯 Deskripsi Aplikasi

**RailLog Nusantara** adalah aplikasi mobile lintas platform (Android & iOS) yang dirancang untuk mendukung operasional logistik pada industri manufaktur kereta api. Aplikasi ini mengintegrasikan kecerdasan buatan berbasis Google Gemini untuk membantu tim logistik dalam:

- **Memantau rantai pasok** komponen dan suku cadang kereta api secara real-time
- **Memverifikasi dokumen teknis** seperti lembar spesifikasi, sertifikat kepatuhan, dan laporan inspeksi menggunakan AI
- **Mengelola inventaris** komponen dengan kategorisasi berbasis jenis subsistem kereta api
- **Menganalisis prioritas pengadaan** berdasarkan status dan urgensi kebutuhan komponen

Aplikasi ini dibangun dengan pendekatan **Adaptive UI** yang menyesuaikan tampilan berdasarkan peran pengguna (operator gudang, manajer logistik, inspektor teknis).

---

## ✨ Fitur Utama

### Sprint 1 (Foundation) — Saat Ini
- [x] Setup project Kotlin Multiplatform
- [x] Arsitektur Clean Architecture + MVVM terdefinisi
- [x] Domain model `SupplyItem` dan `TechnicalDocument`
- [x] SQLDelight schema untuk penyimpanan lokal
- [x] Repository interfaces terdefinisi
- [x] Koin Dependency Injection aktif
- [x] Build berhasil tanpa error

### Sprint 2 (Core Features) — Rencana
- [ ] Dashboard ringkasan status rantai pasok
- [ ] Form input item suku cadang baru
- [ ] Daftar komponen dengan filter status & kategori
- [ ] Penyimpanan lokal dengan SQLDelight
- [ ] Navigasi antar screen

### Sprint 3 (Advanced) — Rencana
- [ ] Verifikasi dokumen teknis via AI (Gemini)
- [ ] Pencarian komponen dengan debounce
- [ ] Filter berdasarkan kategori subsistem kereta
- [ ] Notifikasi komponen kritis
- [ ] Offline-first support

### Sprint 4 (AI & Polish) — Rencana
- [ ] Analisis ringkasan dokumen teknis via AI
- [ ] Saran pengadaan berbasis AI
- [ ] UI polish dan animasi adaptif
- [ ] Unit tests (target 10+)

### Sprint 5 (Final) — Rencana
- [ ] Demo-ready build
- [ ] Release APK
- [ ] Dokumentasi lengkap
- [ ] Video demo

---

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
│                    │  Use Cases (Business Logic) │          │
│                    └──────────────┬──────────────┘          │
│                    ┌──────────────▼──────────────┐          │
│                    │    Repository Interface     │          │
│                    └──────────────┬──────────────┘          │
└───────────────────────────────────┼─────────────────────────┘
                                    │
┌───────────────────────────────────┼─────────────────────────┐
│                       DATA LAYER  │                          │
│                    ┌──────────────▼──────────────┐          │
│                    │  Repository Implementation  │          │
│                    └──────────────┬──────────────┘          │
│         ┌──────────────────────┬──┴───────────────────┐     │
│   ┌─────▼──────┐        ┌──────▼─────┐       ┌────────▼───┐ │
│   │ SQLDelight │        │    Ktor    │       │ DataStore  │ │
│   │  (Lokal)  │        │  (Remote) │       │  (Prefs)   │ │
│   └───────────┘        └───────────┘       └────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🎨 Design System Tokens

- Colors: Surface Slate (#F7F9FB), Primary Navy (#1E3A8A), Success Emerald (#10B981).
- Typography: Geist (UI) dan Geist Mono (Technical Data/Tracking ID).
- Grid: Strict 4px baseline grid untuk presisi industrial.

### Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **UI** | Compose Multiplatform, Material 3 |
| **State Management** | StateFlow, ViewModel |
| **Navigation** | Compose Navigation (Type-safe) |
| **Networking** | Ktor Client |
| **Local Database** | SQLDelight |
| **Preferences** | DataStore |
| **Dependency Injection** | Koin |
| **AI Integration** | Google Gemini API |
| **Testing** | Kotlin Test, Turbine |
| **Platform** | Kotlin Multiplatform (Android + iOS) |

---

## 📁 Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/example/raillog/
│   ├── core/
│   │   ├── di/                     # Koin modules
│   │   ├── network/                # Ktor client, API config
│   │   └── util/                   # Extensions, helpers
│   │
│   ├── data/
│   │   ├── local/
│   │   │   ├── entity/             # SQLDelight mappers
│   │   │   └── datastore/          # Preferences
│   │   ├── remote/
│   │   │   ├── api/                # GeminiService
│   │   │   └── dto/                # Request/Response DTOs
│   │   └── repository/             # Repository implementations
│   │
│   ├── domain/
│   │   ├── model/
│   │   │   ├── SupplyItem.kt       # Model suku cadang kereta
│   │   │   └── TechnicalDocument.kt # Model dokumen teknis
│   │   ├── repository/
│   │   │   ├── SupplyRepository.kt
│   │   │   └── DocumentRepository.kt
│   │   └── usecase/                # Business logic
│   │
│   └── presentation/
│       ├── navigation/             # Routes & NavHost
│       ├── screens/
│       │   ├── dashboard/          # Dashboard logistik
│       │   ├── supply/             # Manajemen suku cadang
│       │   ├── document/           # Verifikasi dokumen
│       │   └── ai/                 # AI Assistant
│       ├── components/             # Komponen UI reusable
│       └── theme/                  # Material theme
│
├── commonMain/sqldelight/          # SQLDelight schema
│   ├── SupplyItem.sq
│   └── TechnicalDocument.sq
│
├── androidMain/kotlin/             # Android-specific
└── iosMain/kotlin/                 # iOS-specific
```

---

## 🚀 Cara Menjalankan

### Prasyarat

| Software | Versi Minimum |
|----------|---------------|
| Android Studio | Ladybug (2024.2.1) |
| JDK | 17 |
| Git | 2.x |
| Android SDK | API 34+ |

### Setup

**1. Clone repository**
```bash
git clone https://github.com/[USERNAME_FORK]/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
git checkout project/123140057-123140068-RailLog
```

**2. Setup `local.properties`**
```bash
cp local.properties.example local.properties
```

Edit `local.properties`:
```properties
sdk.dir=C\:\\Users\\[USERNAME]\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=your_api_key_here
```

Dapatkan API key gratis di: https://aistudio.google.com

**3. Konfigurasi JDK di Android Studio**

`File → Settings → Build, Execution, Deployment → Build Tools → Gradle`

Pastikan **Gradle JDK** mengarah ke **JDK 17**.

**4. Sync & Build**
```bash
./gradlew :composeApp:assembleDebug
```

**5. Run di Android**

Pilih run configuration `composeApp` di Android Studio, lalu klik **Run**.

---

## 🗃️ Domain Model

### SupplyItem — Suku Cadang Kereta Api

| Field | Tipe | Deskripsi |
|-------|------|-----------|
| `id` | Long | Primary key |
| `partCode` | String | Kode part unik |
| `name` | String | Nama komponen |
| `category` | PartCategory | Kategori subsistem |
| `quantity` | Int | Jumlah unit |
| `unit` | String | Satuan (pcs, set, kg) |
| `supplier` | String | Nama pemasok |
| `status` | SupplyStatus | Status pengiriman |
| `priority` | Priority | Tingkat urgensi |
| `documentRef` | String? | Referensi dokumen terkait |

**Kategori Komponen (`PartCategory`):**
- `BOGIE` — Bogie & Roda
- `PROPULSION` — Propulsi & Motor
- `BRAKING` — Sistem Pengereman
- `ELECTRICAL` — Kelistrikan
- `BODY` — Bodi & Struktur
- `INTERIOR` — Interior
- `SAFETY` — Keselamatan
- `MAINTENANCE` — Pemeliharaan

**Status Supply (`SupplyStatus`):**
- `PENDING` → `IN_TRANSIT` → `RECEIVED` → `VERIFIED` / `REJECTED`

---

### TechnicalDocument — Dokumen Teknis

| Field | Tipe | Deskripsi |
|-------|------|-----------|
| `id` | Long | Primary key |
| `title` | String | Judul dokumen |
| `documentType` | DocumentType | Jenis dokumen |
| `content` | String | Isi/teks dokumen |
| `linkedItemId` | Long? | Referensi ke SupplyItem |
| `verificationStatus` | VerificationStatus | Status verifikasi AI |
| `aiSummary` | String? | Ringkasan hasil analisis AI |

**Jenis Dokumen (`DocumentType`):**
- `SPEC_SHEET` — Lembar Spesifikasi
- `INSPECTION_REPORT` — Laporan Inspeksi
- `DELIVERY_NOTE` — Surat Jalan
- `COMPLIANCE_CERT` — Sertifikat Kepatuhan
- `MAINTENANCE_LOG` — Log Pemeliharaan

---

## 🤖 Integrasi AI

RailLog Nusantara menggunakan **Google Gemini API** untuk:

| Fitur AI | Deskripsi |
|----------|-----------|
| **Verifikasi Dokumen** | Menganalisis dokumen teknis untuk mendeteksi inkonsistensi atau ketidaksesuaian standar |
| **Ringkasan Otomatis** | Meringkas laporan inspeksi panjang menjadi poin-poin kritis |
| **Saran Pengadaan** | Memberikan rekomendasi prioritas pengadaan berdasarkan data supply chain |
| **Deteksi Anomali** | Menandai dokumen yang memerlukan perhatian khusus |

---

## 🧪 Testing

```bash
# Jalankan semua test
./gradlew allTests

# Unit test saja
./gradlew :composeApp:testDebugUnitTest
```

Target coverage Sprint 4: minimal 10 unit tests, 50%+ code coverage.

---

## 📅 Timeline Sprint

| Sprint | Minggu | Status | Target |
|--------|--------|--------|--------|
| Sprint 1: Foundation | 11 | ✅ Selesai | Setup, arsitektur, domain model |
| Sprint 2: Core Features | 12 | 🔄 Akan datang | CRUD, navigasi, local storage |
| Sprint 3: Advanced | 13 | — | Search, AI integration, offline |
| Sprint 4: Polish | 14 | — | Testing, bug fix, UI polish |
| Sprint 5: Final | 15 | — | Demo, APK, dokumentasi lengkap |

---

## 📝 Panduan Kontribusi

### Git Workflow

```bash
# Pastikan di branch yang benar
git checkout project/123140057-123140068-RailLog

# Sebelum mulai kerja, selalu pull dulu
git pull origin project/123140057-123140068-RailLog

# Commit dengan format yang benar
git commit -m "feat: add supply item list screen"
git commit -m "fix: resolve database migration issue"
git commit -m "refactor: extract AI logic to use case"
```

### Commit Convention

| Prefix | Penggunaan |
|--------|------------|
| `feat:` | Fitur baru |
| `fix:` | Perbaikan bug |
| `refactor:` | Refactoring kode |
| `style:` | Perubahan UI/styling |
| `test:` | Menambah/update test |
| `docs:` | Update dokumentasi |
| `chore:` | Maintenance |

---

## 🔧 Troubleshooting

| Masalah | Solusi |
|---------|--------|
| `DefaultArtifactPublicationSet` error | Pastikan Gradle JDK = JDK 17, bukan JDK 20 |
| `gradle-wrapper.properties not found` | Buat file wrapper manual atau jalankan `gradle wrapper --gradle-version 8.9` |
| `GEMINI_API_KEY` kosong / 401 | Periksa `local.properties`, pastikan API key valid |
| SQLDelight `no databases set up` | Pastikan blok `sqldelight { databases { ... } }` ada di `build.gradle.kts` |
| Gradle sync lambat | Normal untuk sync pertama (~15 menit), pastikan internet stabil |

---

## 📚 Referensi

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin DI](https://insert-koin.io/)
- [Ktor Client](https://ktor.io/docs/welcome.html)
- [Google Gemini API](https://ai.google.dev/docs)

---

## 📄 Lisensi

MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile di ITERA.

---

*Proyek ini dikembangkan sebagai bagian dari mata kuliah Pengembangan Aplikasi Mobile — Institut Teknologi Sumatera (ITERA)*
