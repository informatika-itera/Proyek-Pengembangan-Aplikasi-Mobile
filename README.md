# 🌌 Roomie - Solusi Satu Pintu Fasilitas Kampus

**Roomie** adalah platform manajemen fasilitas terintegrasi yang didesain untuk menyederhanakan birokrasi kampus. Proyek ini menggabungkan sistem **Persetujuan Peminjaman (Approval System)**, **E-Permit Digital**, dan **Pelaporan Fasilitas** dalam satu ekosistem digital yang modern, transparan, dan efisien.

Aplikasi ini dibangun menggunakan **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform**, menargetkan platform Android dan iOS dari satu codebase tunggal dengan arsitektur yang sangat terukur.

---

## 🚀 Fitur Utama

### 1. 🔐 Role-Based Access Control (RBAC) & Cloud Auth
- **Dual Perspective:** Alur kerja dinamis untuk **Mahasiswa** (Lapor & Cari) dan **Admin** (Approval & Kontrol).
- **Secure Cloud Auth:** Autentikasi terpusat menggunakan **Supabase Auth** dengan sistem *Shadow Email* untuk keamanan NIM/NIP.
- **Persistent Profile:** Manajemen session menggunakan **DataStore** yang menyimpan data profil dan identitas user secara aman.

### 2. 🏢 Smart Facility Explorer & Advanced Search
- **Date-Aware Status:** Status ruangan (Tersedia/Penuh) bersifat dinamis mengikuti kalender. Ruangan bisa terlihat penuh di tanggal 21, tapi tersedia di tanggal 22.
- **Capacity Filtering:** Pencarian ruangan cerdas berdasarkan kapasitas kursi (Range 35 - 60 kursi) menggunakan **Dropdown Filter**.
- **Real-time Multi-Device Sync:** Perubahan status ruangan di satu HP (misal: di-book oleh Mulya) akan langsung terlihat di HP lain (Nahli) secara instan via **Supabase Realtime**.

### 3. 📝 Integrity-Driven Booking Workflow
- **Multi-Room Selection:** Kemampuan memilih beberapa ruangan sekaligus (e.g. Ruang 101 & 102) dalam satu pengajuan peminjaman.
- **NTP Server Time Sync:** Pencegahan manipulasi waktu lokal menggunakan sinkronisasi jam server pusat (NTP) untuk validasi peminjaman.
- **Automatic Status Janitor:** Sistem otomatis mengubah status peminjaman menjadi "COMPLETED" dan membebaskan ruangan jika waktu sewa telah usai.

### 4. 📸 Cloud Reporting & Dynamic Profile
- **Avatar Management:** Fitur ganti foto profil langsung dari galeri yang diunggah ke **Supabase Storage** dan muncul secara sinkron di Header Beranda.
- **Image Evidence:** Pelaporan kerusakan fasilitas dilengkapi dengan lampiran foto bukti menggunakan **Peekaboo Image Picker**.
- **Admin Evidence Viewer:** Admin dapat memvalidasi laporan langsung melalui pratinjau gambar yang ditarik dari Cloud.

### 5. 📅 Master Schedule & Live Notifications
- **Global Transparency:** Kalender jadwal penggunaan seluruh ruangan kampus yang dapat diakses oleh semua pengguna untuk menghindari bentrok internal.
- **Reactive Alerts:** Mahasiswa menerima notifikasi reaktif saat pengajuan mereka disetujui atau ditolak oleh Admin.

---

## 🛠️ Tech Stack

- **UI Framework:** Compose Multiplatform (Material Design 3 - Professional ITERA Theme)
- **Dependency Injection:** Koin (Modular: Data, Domain, ViewModel, Supabase)
- **Local Database:** SQLDelight (Offline-First with v4 Schema Migration)
- **Cloud Storage:** Supabase Storage (Remote Image Management)
- **Networking:** Ktor Client (Engine v2.3.12 for Supabase Compatibility)
- **Concurrency:** Kotlin Coroutines & Flow
- **Image Loading:** Coil 3 (Reactive Network Image Support)
- **Architecture:** Clean Architecture (Data, Domain, Presentation) + MVVM + UseCases

---

## 📂 Struktur Proyek (Clean Architecture)

```text
composeApp/src/commonMain/kotlin/com/example/Roomie/
│
├── core/               # Network Monitor, Database Factory, & Security Utils
├── data/               # RepositoryImpl, Supabase Service, & SQLDelight
├── domain/             # Entities, Repository Contracts, & UseCases
├── di/                 # Koin Modules (Data, Domain, ViewModel, Supabase)
├── presentation/       # UI Layer (Material 3)
│   ├── auth/           # Login, Splash, & Onboarding
│   ├── home/           # Dashboard Student & Master Calendar
│   ├── admin/          # Approval System, Control Center, & Broadcast
│   ├── facility/       # Multi-select Grid, Booking Form, & Search
│   ├── report/         # Rich Reporting (Camera/Gallery Integration)
│   ├── profile/        # Notifications, Theme Settings, & History
│   └── theme/          # Smart Theme Engine (System, Light, Dark)
└── util/               # AppStrings (Localisation-Ready)
```

---

## 🧪 Kualitas Kode & CI/CD

- **GitHub Actions:** CI pipeline otomatis mencakup:
    - Base Setup Java JDK 17
    - Mock `local.properties` creation for secure build
    - Execution permission management
    - **Unit Testing:** Menjalankan 11 unit tests validasi logic (All Passed ✅)
    - **Build:** Otomatis menghasilkan Debug APK
- **Static Analysis:** Terintegrasi dengan **Detekt** untuk menjaga kualitas kode.

---

## 📅 Project Roadmap (Sprint 1 - 5)

| Sprint | Fokus Utama | Ringkasan Milestone | Status |
|---|---|---|---|
| **Sprint 1** | Infrastructure | Repository Setup, Koin DI, SQLDelight, GitHub Actions CI/CD | ✅ Done |
| **Sprint 2** | Core Features | Login RBAC, Multi-Select Grid, Booking Logic, Supabase Cloud Storage | ✅ Done |
| **Sprint 3** | Advanced Logic | Offline-First, Real-time Sync, NTP Time, Command Center UI | ✅ Done |
| **Sprint 4** | Quality Control | Systematic Bug Fixing, Unit & UI Testing, UI Polish, Performance Profiling | 🚀 Planned |
| **Sprint 5** | Final Delivery | Signed APK Generation, Demo Scripting, Backup Plan, UAS Demo Day Prep | 🏁 Final |

---

## 📑 Detail Progress Pengembangan (Sprint 1 - 3)

| Sprint | Kriteria | Status | Detail / Bukti Implementasi |
|:---:|---|:---:|---|
| **1** | GitHub Collaboration | ✅ | Tim (Mulya & Nahli) terdaftar sebagai kolaborator dengan riwayat commit aktif. |
| | KMP Project Structure | ✅ | Struktur folder mengikuti *Clean Architecture*: core, data, domain, presentation, di. |
| | GitHub Actions CI | ✅ | Pipeline CI otomatis aktif untuk validasi Build & Unit Testing (11 tests passed). |
| | Comprehensive README | ✅ | Dokumentasi lengkap mencakup tim, deskripsi fitur, tech stack, dan arsitektur. |
| | Project Plan & Tasks | ✅ | Roadmap terukur per sprint dengan pembagian tugas (Logic vs UI). |
| | Koin DI Setup | ✅ | Dependency Injection modular terbagi dalam AppModule, Data, Domain, & ViewModel. |
| **2** | Minimal 3 Screens | ✅ | Tersedia >10 layar fungsional: Home, Detail, Booking, Report, Admin, Search, dll. |
| | Navigasi & Argumen | ✅ | Navigasi dinamis dengan passing data (e.g. roomId, date, listRoomIds). |
| | Repository Pattern | ✅ | Abstraksi data layer menggunakan interface domain dan implementasi Supabase. |
| | Local & Cloud Storage | ✅ | Sinkronisasi SQLDelight (Local v5), DataStore (Prefs), & Supabase (Cloud Database). |
| | CRUD Operations | ✅ | Operasi Create, Read, Update, Delete fungsional dan tersinkron ke Cloud. |
| | UI States (L/S/E) | ✅ | Penanganan state reaktif (Loading, Success, Error) di seluruh layar utama. |
| | App Accessibility | ✅ | Alur navigasi tertutup (no dead ends), user dapat menjelajah seluruh fitur app. |
| | API Integration | ✅ | Integrasi Supabase SDK (DB/Auth/Realtime) & Ktor Client (Server Time Sync). |
| **3** | Search/Filter | ✅ | Implementasi filter kapasitas cerdas & pencarian reaktif di sisi Mahasiswa & Admin. |
| | Offline-First Support | ✅ | Arsitektur data hibrida (Local SQLite via SQLDelight + Remote Supabase Sync). |
| | Additional Screens | ✅ | Hub Dashboard Admin, Layar Notifikasi, dan Manajemen Profil User. |
| | Bonus Feature | ✅ | Integrasi Peekaboo Image Picker & NTP Anti-Fraud System Time Synchronization. |
| | Extra Feature | ✅ | Interactive Command Center (Gauges interaktif) & Seamless Real-time WebSocket. |
| | Regression Test | ✅ | Validasi seluruh fitur utama Sprint 2 tetap stabil (11 Unit Tests PASSED). |

---

## 👥 Tim Pengembang (Kelompok Tubes)

Aplikasi ini dikembangkan oleh:
1. **Mulya Delani** - 123140019 (Lead Developer / Logic Architect)
2. **Nahli Saud Ramdani** - 123140049 (UI Designer / Cloud Specialist)

---

> **"Roomie: Pinjem ruang gampang, lapor fasilitas cepat."**
