# VulnLog

**Vulnerability Tracker & Bug Bounty Journal**

VulnLog adalah aplikasi mobile untuk mencatat dan mengelola temuan vulnerability saat bug bounty hunting. Dibangun dengan Kotlin Multiplatform (KMP) dan Compose Multiplatform, app ini bisa dipakai di Android dan iOS dari satu codebase.

Kenapa bikin ini? Karena kebanyakan bug hunter masih catat temuan di Notion/Google Docs yang formatnya berantakan. VulnLog hadir sebagai jurnal terstruktur yang bisa langsung track status temuan dari "Found" sampai "Paid".

## Fitur Utama

### 1. Vulnerability Logging
- Catat temuan vulnerability lengkap: judul, deskripsi, severity (Critical/High/Medium/Low), status, dan platform target.
- Timestamp otomatis untuk setiap entry.

### 2. Severity Classification
- Color-coded severity level sesuai standar CVSS (merah = Critical, oranye = High, kuning = Medium, biru = Low).
- Filter dan sort berdasarkan severity.

### 3. Status Tracking
- Track status temuan: New, Reported, Triaged, Accepted, Resolved, Paid.
- Dashboard ringkasan per status.

### 4. AI Assistant
- Bantu generate deskripsi vulnerability dari input singkat.
- Suggest severity berdasarkan tipe vulnerability.

### 5. Search & Filter
- Full-text search di semua catatan.
- Filter berdasarkan severity, status, platform, dan tanggal.

### 6. Dark Mode
- Default dark theme dengan aksen neon green (terminal aesthetic).

## Arsitektur

Clean Architecture dengan 3 layer:
- **Presentation**: Jetpack Compose + ViewModel + StateFlow
- **Domain**: Use Cases, Entities, Repository Interfaces
- **Data**: Repository Implementations, SQLDelight (local), Ktor (remote)

```
composeApp/src/commonMain/kotlin/com/example/noteai/
+-- core/           # DI (Koin), network config, utilities
+-- data/           # Repository impl, local DB, remote API
+-- domain/         # Models, repository interfaces, use cases
+-- presentation/   # Screens, ViewModels, components, theme
```

## Tech Stack

| Layer | Teknologi |
|-------|-----------|
| Language | Kotlin |
| UI | Compose Multiplatform |
| Architecture | Clean Architecture + MVVM |
| DI | Koin |
| Database | SQLDelight |
| Network | Ktor Client |
| Serialization | kotlinx.serialization |
| Async | Kotlin Coroutines & Flow |
| AI | Google Gemini API |

## Sprint Roadmap

| Sprint | Fokus | Status |
|--------|-------|--------|
| Sprint 1 | Project setup, tema cybersecurity, README | ✓ Selesai |
| Sprint 2 | CRUD vulnerability, severity system | ✓ Selesai |
| Sprint 3 | Search, filter, status tracking, Settings/Profile | ✓ Selesai |
| Sprint 4 | Unit & UI testing, UI Polish, Bug Fixing | ✓ Selesai |
| Sprint 5 | Final Preparation & Demo UAS | In Progress |

## Setup

```bash
# Clone repo
git clone https://github.com/11-090-AndikaRahmanPratama/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile

# Checkout branch project
git checkout project/123140075-123140090-VulnLog

# Setup API key
cp local.properties.example local.properties
# Edit local.properties, isi GEMINI_API_KEY=...

# Build
./gradlew :composeApp:assembleDebug
```

Dapatkan API key di: [Google AI Studio](https://aistudio.google.com)

## 🧪 Pengujian & Testing (Sprint 4)

Proyek ini dilengkapi dengan pengujian otomatis menggunakan pustaka pengujian standar Kotlin Multiplatform (`kotlin.test`), Turbine untuk pengujian Flow/StateFlow, dan coroutines test dispatcher.

### 1. Menjalankan Unit Tests
Untuk menjalankan pengujian unit secara keseluruhan via gradle:
```bash
./gradlew test
```
Atau Anda dapat mengeklik kanan folder `commonTest` di Android Studio dan memilih **Run 'All Tests'**.

### 2. Struktur Pengujian yang Tersedia
- **NoteRepositoryTest.kt** (13 Skenario Uji):
  - Operasi CRUD dasar (Insert, Update, Delete) database lokal.
  - Skenario pengujian filter dinamis (berdasarkan status, tipe kerentanan, dan tingkat severity).
  - Pengujian fitur pin/unpin dan hapus massal log.
- **HomeViewModelTest.kt** (7 Skenario Uji):
  - Pengujian perubahan *UI State* (Loading, Success, Empty, Error).
  - Validasi query input pencarian dan fungsionalitas debounce 300ms.
  - Pengujian filter terintegrasi berbasis Severity tingkat tinggi.

---

## Tim Pengembang

| Nama | NIM | Role |
|------|-----|------|
| Andika Rahman Pratama | 123140090 | Developer |
| Muhammad Farhan Muzakhi | 123140075 | Developer |

Program Studi Teknik Informatika - Institut Teknologi Sumatera (ITERA)
