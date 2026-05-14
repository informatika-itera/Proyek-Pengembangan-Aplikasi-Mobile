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
| Sprint 1 | Project setup, tema cybersecurity, README | In Progress |
| Sprint 2 | CRUD vulnerability, severity system | Planned |
| Sprint 3 | Search, filter, status tracking | Planned |
| Sprint 4 | AI integration, UI polish | Planned |
| Sprint 5 | Testing, bug fix, demo | Planned |

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

## Tim Pengembang

| Nama | NIM | Role |
|------|-----|------|
| Andika Rahman Pratama | 123140090 | Developer |
| Muhammad Farhan Muzakhi | 123140075 | Developer |

Program Studi Teknik Informatika - Institut Teknologi Sumatera (ITERA)
