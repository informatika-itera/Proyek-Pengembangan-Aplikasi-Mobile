# 🥗 FitKos - AI Healthy Lifestyle Assistant

![CI](https://github.com/raapstronaut/FitKos/actions/workflows/ci.yml/badge.svg?branch=project%2F123140046-123140173-FitKos)


FitKos adalah aplikasi Android berbasis Kotlin yang dirancang untuk membantu penghuni kos menjaga pola hidup sehat sesuai budget harian. Aplikasi ini membantu pengguna mencatat makanan dan pengeluaran makan, memantau jumlah minum air, menandai olahraga ringan, serta mendapatkan tips sehat yang sederhana dan realistis.

FitKos juga dilengkapi dengan AI Assistant berbasis Gemini API yang dapat memberikan rekomendasi sehat hemat, tips makanan, dan saran aktivitas ringan dengan gaya bahasa yang santai dan mudah dipahami.

## 👥 Tim Pengembangan


| Nama | NIM | GitHub |
| --- | --- | --- |
| Jana Rohman Wasiso | 123140046 | [@10-046-JanaRohman](https://github.com/10-046-JanaRohman) |
| Muhammad Rafi Ilham | 123140173 | [@raapstronaut](https://github.com/raapstronaut) |

## ✨  Fitur Utama
### Sudah Diimplementasikan pada Sprint 2
- [x] Dashboard harian
- [x] Catatan makanan harian
- [x] Pencatatan harga/pengeluaran makan
- [x] Add/Edit catatan makanan
- [x] Detail catatan makanan
- [x] Local storage untuk menyimpan catatan makanan
- [x] Basic CRUD untuk catatan makanan
- [x] Multi-screen navigation
- [x] AI Assistant dengan Gemini API

### Sudah Diimplementasikan pada Sprint 3
- [x] Search/filter catatan makanan berdasarkan nama dan kategori
- [x] Water tracker harian
- [x] Exercise timer / stopwatch olahraga ringan
- [x] Target harian untuk air minum dan olahraga
- [x] Reset harian otomatis untuk data olahraga berdasarkan tanggal device
- [x] Offline support dengan strategi Stale-While-Revalidate pada fitur AI/Tips
- [x] Dark mode melalui pengaturan aplikasi
- [x] UI/UX polish pada Dashboard, Meal Log, Add/Edit, Detail, AI, Water Tracker, dan Exercise Screen

### Sudah Diimplementasikan pada Sprint 4

- [x] Bug fixing berdasarkan issue/masalah yang ditemukan selama pengembangan
- [x] UI polish pada screen utama agar tampilan lebih konsisten dan terbaca pada light mode maupun dark mode
- [x] 10+ unit tests untuk Repository, UseCase, dan ViewModel
- [x] 3+ UI tests untuk critical user flows
- [x] Compose UI Test untuk Dashboard, Meal Log navigation, dan Water Tracker navigation
- [x] Kover integration untuk reporting coverage
- [x] 50%+ filtered line coverage pada testable logic utama
- [x] Testing infrastructure dengan Fake Repository dan Test DataStore
- [x] README updated dengan instruksi testing dan coverage report

### Direncanakan untuk Sprint 5
- [x] Memastikan semua bug tersisa sudah diperbaiki dan aplikasi stabil
- [ ] Menyiapkan presentation slides dalam format PDF/PPTX
- [x] Menulis dan melatih demo script untuk Demo Day
- [x] Membuat release APK dan mengujinya pada device/emulator
- [x] Finalisasi README dengan fitur, setup, screenshot, dan instruksi testing
- [x] Menyiapkan backup video demo untuk mengantisipasi kendala teknis
- [x] Melakukan latihan demo minimal 2 kali bersama tim

## 🏗️ Arsitektur & Teknologi Stack
FitKos menggunakan pendekatan Clean Architecture dan MVVM agar kode lebih rapi, mudah dikembangkan, dan mudah diuji.

- Presentation Layer: UI, Screen, Navigation, ViewModel, dan StateFlow
- Domain Layer: Model, Repository Interface, dan Use Case
- Data Layer: Repository Implementation, Local Data Source, Remote Data Source, dan Model Data
- Dependency Injection: Menggunakan Koin

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Framework | Kotlin Multiplatform |
| UI | Compose Multiplatform, Material Design 3 |
| Architecture | MVVM, Clean Architecture, StateFlow |
| Storage | SQLDelight, DataStore |
| Networking & AI | Ktor Client, Gemini API |
| Dependency Injection | Koin |
| CI/CD | GitHub Actions |

## 📁 Project Structure

```text
├── core/                         # Di, network, util
├── data/                         # Local, remote, repository
├── domain/                       # Model, repository, usecases
└── presentation/
    ├── components/               # Shared UI components
    ├── navigation/               # App routes and navigation host
    ├── screens/
    │   ├── dashboard/            # Daily summary and quick actions
    │   ├── home/                 # Meal log list with search and filter
    │   ├── addnote/              # Add/Edit meal log
    │   ├── detail/               # Meal log detail
    │   ├── ai/                   # FitKos AI Assistant
    │   ├── watertracker/         # Daily water tracker
    │   ├── exercise/             # Exercise timer and workout tips
    │   └── settings/             # User preferences and dark mode
    │   └──splash/                # Splash and first user setup
    └── theme/                    # Material theme
```

## 📌 Sprint 1 - Planning & Setup
### Deliverables Sprint 1
- [x] GitHub repository dibuat
- [x] Semua anggota tim ditambahkan sebagai collaborator
- [x] Project Kotlin Multiplatform berhasil disiapkan
- [x] Struktur folder project mengikuti pembagian architecture layer
- [x] GitHub Actions CI disiapkan
- [x] CI badge ditampilkan pada README
- [x] README awal dibuat
- [x] Ide aplikasi dan rencana fitur ditentukan
- [x] Pembagian tugas awal tim dibuat

## 📌 Sprint 2 - Core Features
### Deliverables Sprint 2
- [x] Minimal 3 working screens
- [x] Navigation antar screen dengan arguments
- [x] Data layer menggunakan Repository pattern
- [x] Local storage menggunakan SQLDelight
- [x] Basic CRUD operations working
- [x] UI states dasar
- [x] Semua fitur utama dapat diakses dari aplikasi
- [x] CI tetap passing
      
### CRUD Operations
- Create: menambahkan catatan makanan
- Read: menampilkan daftar dan detail catatan makanan
- Update: mengubah catatan makanan
- Delete: menghapus catatan makanan

### 🎥 Demo Sprint 2
[Demo](https://youtu.be/MZyQGsvdlZo)

## 📌 Sprint 3 - Advanced Features
### Deliverables Sprint 3
- [x] Search/filter functionality pada catatan makanan
- [x] API integration menggunakan Gemini API
- [x] Offline support menggunakan strategi Stale-While-Revalidate pada fitur AI/Tips
- [x] Additional screen: Water Tracker dan Exercise Timer
- [x] Bonus feature: Dark mode dan daily exercise reset
- [x] Core features Sprint 2 tetap berjalan

### Advanced Features
- Search/filter digunakan pada Meal Log untuk mencari catatan makanan berdasarkan nama dan kategori.
- AI Assistant menggunakan Gemini API untuk memberikan rekomendasi sehat hemat.
- Strategi Stale-While-Revalidate diterapkan pada fitur AI/Tips dengan menyimpan rekomendasi terakhir di DataStore, lalu memperbarui data dari Gemini API saat koneksi tersedia.
- Water Tracker menyimpan data berdasarkan tanggal sehingga data harian otomatis dimulai dari 0 pada hari baru.
- Exercise Timer menyimpan durasi olahraga harian dan melakukan reset otomatis ketika aplikasi dibuka pada tanggal yang berbeda.
- Dark mode disimpan menggunakan DataStore sebagai preferensi pengguna.

### 🎥 Demo Sprint 3
[Demo](https://youtu.be/oRgG741M1VE)

## 📌 Sprint 4 - Polish & Testing
### Deliverables Sprint 4
- [x] All known bugs fixed
- [x] UI polished dengan tampilan yang konsisten
- [x] 10+ unit tests
- [x] 3+ UI tests untuk critical flows
- [x] 50%+ code coverage pada filtered testable logic
- [x] README updated dengan instruksi testing

### Bug Fixing & UI Polish
- Bug tracking dilakukan menggunakan dokumen `docs/sprint4-bug-tracking.md`
- UI polish checklist dilakukan menggunakan dokumen `docs/sprint4-ui-polish-checklist.md`
- Search field pada Catatan Makan diperbaiki agar input dan filter berjalan normal
- Tampilan Dashboard dan Water Tracker diperbaiki agar tetap terbaca pada dark mode
- Bottom navigation pada AI Assistant diperbaiki agar konsisten dengan menu utama lainnya
- Padding bawah pada list Catatan Makan diperbaiki agar card makanan tidak tertutup area kosong

### Testing & Coverage
- Unit test mencakup Repository, UseCase, dan ViewModel
- UI test mencakup critical flows seperti Dashboard, Meal Log navigation, dan Water Tracker navigation
- Coverage report dibuat menggunakan Kover
- Filtered line coverage terakhir: 77.1%
- Filtered instruction coverage terakhir: 70.6%

### 🎥 Demo Sprint 4
[Demo Sprint 4](https://youtu.be/at6TFoI4SOg)

oRgG741M1VE)

## 📌 Sprint 5 - 
### Deliverables Sprint 5
- [x] All remaining bugs fixed and application stability verified
- [x] Demo script written and rehearsed by the team
- [x]     Release APK built and tested on Android devices/emulators
- [x] README finalized with features, setup guide, screenshots, and testing instructions
- [x] Backup demo video recorded for technical contingency
- [x] Team completed at least two full demo practice sessions

### Release & Deployment
- Release APK successfully generated and tested
- Application validated on Android devices/emulators
- Versioning finalized for the project release
- Repository cleaned up and documented for submission

### Final Demo Highlights

The final demonstration showcases the complete FitKos experience:

- Dashboard and daily health summary
- Meal Log management (Create, Read, Update, Delete)
- Search and filter functionality
- Daily Water Tracker
- Exercise Timer and activity tracking
- AI Healthy Lifestyle Assistant powered by Gemini API
- Offline support using Stale-While-Revalidate strategy
- Dark Mode preferences
- Clean Architecture and MVVM implementation
- Testing infrastructure and coverage reporting

### 🎥 Demo Final Fitkos
[Demo Final](https://youtu.be/YZUwQ-jnWtg?si=bLViGgofWJJDNbJH)

## 🚀 Getting Started

1. Clone Repository

```bash
git clone https://github.com/raapstronaut/FitKos.git
```

2. Setup API Key

Tambahkan `GEMINI_API_KEY=your_key` di file `local.properties`.

3. Buka di Android Studio Ladybug+ dan jalankan task: `:composeApp:installDebug`

## 🧪 Testing
FitKos menggunakan unit test, UI test, dan coverage report untuk memastikan fitur utama berjalan stabil pada Sprint 4.

### Menjalankan Unit Test
Unit test digunakan untuk menguji logika pada Repository, UseCase, dan ViewModel.
```bash
./gradlew :composeApp:testDebugUnitTest
```
Untuk menjalankan seluruh test:
```bash
./gradlew test
```

### Menjalankan UI Test
UI test digunakan untuk menguji critical user flows menggunakan Compose UI Test. Pengujian ini membutuhkan emulator atau device Android yang aktif.
```bash
./gradlew :composeApp:connectedDebugAndroidTest
```

UI test yang tersedia mencakup:
- Dashboard ditampilkan saat aplikasi dibuka dalam mode test
- Navigasi ke Meal Log melalui bottom navigation
- Navigasi ke Water Tracker melalui bottom navigation

### Laporan Coverage (Kover)
Aplikasi ini menggunakan Kover untuk membuat laporan coverage.
```bash
./gradlew :composeApp:koverHtmlReport
```
Laporan dapat ditemukan di: `composeApp/build/reports/kover/html/index.html`

Hasil coverage terakhir:
- Filtered line coverage: 77.1%
- Filtered instruction coverage: 70.6%

Coverage report screenshot:
<img width="1879" height="957" alt="Screenshot 2026-06-08 224425" src="https://github.com/user-attachments/assets/e35e57b9-5abf-4577-8f2c-610645a89a6f" />

Coverage dihitung menggunakan filtered scope pada testable logic utama, seperti:
- Domain model
- Domain use case
- ViewModel / screen logic utama

Beberapa bagian dikecualikan dari coverage report karena bukan target utama unit test, seperti:
- Generated resources
- Dependency injection
- Navigation shell
- Theme
- Platform-specific configuration
- Remote DTO/API
- Pure UI Compose shell

### Infrastruktur Testing
- **Fakes**: Digunakan `FakeNoteRepository` dan `FakeWaterRepository` untuk mensimulasikan database.
- **DataStore**: Menggunakan `createTestDataStore` (Okio-based) untuk testing preferensi pengguna di `commonTest`.
- **Turbine**: Digunakan untuk testing Kotlin Flows/StateFlow pada ViewModel.

## 📄 Lisensi

---MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.
