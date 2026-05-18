# 🌌 Roomie - Solusi Satu Pintu Fasilitas Kampus

**Roomie** adalah platform manajemen fasilitas terintegrasi yang didesain untuk menyederhanakan birokrasi kampus. Proyek ini menggabungkan sistem **Persetujuan Peminjaman (Approval System)**, **E-Permit Digital**, dan **Pelaporan Fasilitas** dalam satu ekosistem digital yang modern, transparan, dan efisien.

Aplikasi ini dibangun menggunakan **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform**, menargetkan platform Android dan iOS dari satu codebase tunggal dengan arsitektur yang sangat terukur.

---

## 🚀 Fitur Utama (Sprint 1 Completed)

### 1. 🔐 Role-Based Access Control (RBAC) & Smart Auth
- **Dual Perspective:** Alur kerja dinamis untuk **Mahasiswa** (Lapor & Cari) dan **Admin** (Approval & Kontrol).
- **Smart Validation:** Validasi NIM (ITERA Standard) dan NIP menggunakan Regex.
- **Secure Session:** Manajemen session persisten menggunakan **DataStore** dengan obfuscation data sensitif.

### 2. 🏢 Interactive Facility Explorer & Multi-Select
- **Building Hierarchy:** Penjelajahan berbasis gedung (GKU 1, GKU 2, Gedung E, Gedung F).
- **Multi-Room Selection:** Kemampuan memilih beberapa ruangan sekaligus untuk peminjaman massal.
- **Real-time Status:** Visualisasi status ruangan (Tersedia, Penuh, Perbaikan) secara reaktif.

### 3. 📝 Advanced Booking & Approval Workflow
- **Harmony-style Limit:** Pembatasan pengajuan aktif (Anti-Spam) untuk menjaga antrean administrasi.
- **Admin Approval Tab:** Dashboard khusus Admin untuk menyetujui atau menolak pengajuan secara instan.
- **Automatic Status Sync:** Status ruangan otomatis berubah menjadi "BOOKED" setelah Admin memberikan persetujuan.

### 4. 📸 Rich Reporting with Cloud Storage
- **Image Evidence:** Mahasiswa dapat melampirkan foto bukti kerusakan menggunakan **Peekaboo Image Picker**.
- **Supabase Integration:** Foto diunggah secara otomatis ke **Supabase Cloud Storage**.
- **Admin Evidence Viewer:** Admin dapat memvalidasi laporan melalui pratinjau gambar di dashboard.

### 5. 📅 Master Schedule & Notifications
- **Global Calendar:** Transparansi jadwal penggunaan seluruh ruangan kampus bagi semua pengguna.
- **Smart Notifications:** Mahasiswa menerima notifikasi reaktif saat pengajuan mereka disetujui atau ditolak.

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
    - **Unit Testing:** Menjalankan 11 unit tests validasi logic
    - **Build:** Otomatis menghasilkan Debug APK
- **Static Analysis:** Terintegrasi dengan **Detekt** untuk menjaga kualitas kode.

---

## 📅 Project Roadmap (Sprint 1 - 5)

| Sprint | Fokus Utama | Ringkasan Milestone | Status |
|---|---|---|---|
| **Sprint 1** | Infrastructure | Repository Setup, Koin DI, SQLDelight, GitHub Actions CI/CD | ✅ Done |
| **Sprint 2** | Core Features | Login RBAC, Multi-Select Grid, Booking Logic, Supabase Cloud Storage | 🏗️ In Progress |
| **Sprint 3** | Advanced Logic | Ktor API Integration, Reactive Search, Offline-First Support, UX Animations | 🚀 Planned |
| **Sprint 4** | Quality Control | Systematic Bug Fixing, Unit & UI Testing, UI Polish, Performance Profiling | 🚀 Planned |
| **Sprint 5** | Final Delivery | Signed APK Generation, Demo Scripting, Backup Plan, UAS Demo Day Prep | 🏁 Final |

---

## 👥 Tim Pengembang (Kelompok Tubes)

Aplikasi ini dikembangkan oleh:
1. **Mulya Delani** - 123140019 (Lead Developer / Logic Architect)
2. **Nahli Saud Ramdani** - 123140049 (UI Designer / Cloud Specialist)

---

> **"Roomie: Pinjem ruang gampang, lapor fasilitas cepat."**
