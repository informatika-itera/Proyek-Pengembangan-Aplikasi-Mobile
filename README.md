# 📦 InventRa — Inventaris HMIF ITERA

![CI](https://github.com/MNAUFALFAKMAL/InventRa/actions/workflows/ci.yml/badge.svg)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-brightgreen)
![KMP](https://img.shields.io/badge/KMP-Kotlin%20Multiplatform-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

**InventRa** adalah aplikasi manajemen inventaris cerdas yang dirancang khusus untuk pengurus **HMIF ITERA**. Aplikasi ini memungkinkan peminjaman dan pengembalian aset organisasi secara terorganisir dengan bantuan asisten AI.

---

## 👥 Tim Kelompok

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| **Muhammad Naufal Fikri Akmal** | 123140132 | [@MNAUFALFAKMAL](https://github.com/MNAUFALFAKMAL) | Data Engineer & Navigation Architect |
| **Nabila Ramadhani Mujahidin** | 123140062 | [@nblable](https://github.com/nblable) | UI/UX Engineer & ViewModel Developer |

---

## 🎓 Informasi Akademik

- **Mata Kuliah**: Pengembangan Aplikasi Mobile (IF25-22017)
- **Program Studi**: Teknik Informatika - Institut Teknologi Sumatera (ITERA)
- **Dosen Pengampu**: [M Habib Algifari, S.Kom., M.T.I. (mh4Scripts)](https://github.com/mh4Scripts)

---

---

## ✨ Fitur Utama (Sprint 2)

Aplikasi ini telah menyelesaikan tahap **Sprint 2: Core Features** dengan fungsionalitas berikut:

### 1. Manajemen Inventaris (CRUD)
- **Dashboard Overview**: Ringkasan statistik (Total Item, Dipinjam, Overdue) dan daftar peminjaman aktif.
- **Katalog Barang**: Daftar seluruh aset organisasi dengan fitur pencarian dan filter per kategori.
- **Detail Barang**: Informasi mendalam item, lokasi penyimpanan, penanggung jawab (PIC), dan status ketersediaan.
- **Tambah & Edit**: Form validasi untuk memasukkan atau memperbarui data aset baru.
- **Riwayat Peminjaman**: Rekam jejak seluruh transaksi peminjaman aset.

### 2. Asisten Cerdas (AI Assistant)
- **Analisis Stok**: AI memberikan wawasan tentang kesehatan stok inventaris secara real-time.
- **Saran Pengadaan**: Rekomendasi barang yang perlu diadakan berdasarkan data stok yang kritis.
- **Laporan & Tindakan**: Membantu pengurus menyusun laporan peminjaman dan memberikan panduan tindakan untuk barang yang terlambat (*overdue*).

### 3. Keunggulan Teknis
- **Offline-First**: Data tersimpan secara lokal menggunakan **SQLDelight**.
- **Modern UI**: Menggunakan **Compose Multiplatform** dengan palet warna brand HMIF ITERA.
- **Type-Safe Navigation**: Perpindahan layar yang aman dengan argumen menggunakan Navigation Compose terbaru.

---

## 🏗️ Arsitektur & Struktur Kode

Aplikasi ini menggunakan **Clean Architecture** untuk pemisahan logika bisnis dan tampilan:

```
composeApp/src/commonMain/kotlin/com/example/inventra/
├── core/
│   ├── di/              # Koin modules (AppModule.kt)
│   └── network/         # HttpClient factory & Gemini API
├── data/
│   ├── local/           # SQLDelight entities & mappers
│   └── repository/      # Implementasi Repository (Item & Borrow)
├── domain/
│   ├── model/           # Data classes (Item, BorrowRecord)
│   ├── repository/      # Interface Repository
│   └── usecase/         # Logika bisnis terpusat (ItemUseCases)
└── presentation/
    ├── components/      # UI Reusable (ItemCard, StatusBadge, dll)
    ├── navigation/      # Routes.kt & AppNavHost.kt
    ├── screens/         # Dashboard, Catalog, Detail, AddEdit, AI
    └── theme/           # Palet warna brand HMIF (Theme.kt)
```

---

## 🛠️ Tech Stack

- **Framework**: Kotlin Multiplatform (KMP)
- **UI**: Compose Multiplatform (Material 3)
- **Database**: SQLDelight (Local Persistence)
- **DI**: Koin
- **Networking**: Ktor Client
- **AI**: Google Gemini API
- **DateTime**: kotlinx-datetime
- **Navigation**: Jetpack Navigation Compose (Type-Safe)

---

## 🚀 Cara Menjalankan

### 1. Persiapan API Key
InventRa membutuhkan Gemini API Key untuk fitur AI. 
- Dapatkan key di: [Google AI Studio](https://aistudio.google.com/)
- Buat file `local.properties` di root project.
- Tambahkan baris: `GEMINI_API_KEY=your_api_key_here`

### 2. Build & Run
- **Android**: Buka di Android Studio, pilih modul `composeApp` dan jalankan ke emulator/device.
- **Desktop/iOS**: Konfigurasi tersedia sesuai standar KMP.

---

## 📅 Progress Project

### Sprint 1: Planning & Setup
| Deliverable | Status |
|-------------|--------|
| Setup GitHub repository & CI/CD | ✅ Selesai |
| KMP Project Structure (Clean Architecture) | ✅ Selesai |
| Domain Models & Interfaces Initial Setup | ✅ Selesai |
| Koin DI Basic Configuration | ✅ Selesai |
| Brand Identity & Theme HMIF ITERA | ✅ Selesai |

### Sprint 2: Core Features
| Deliverable | Status |
|-------------|--------|
| Minimal 3 working screens (Dashboard, Catalog, Detail) | ✅ Selesai |
| Navigation dengan passing arguments (`itemId`) | ✅ Selesai |
| Data Layer (Repository Pattern + SQLDelight) | ✅ Selesai |
| CRUD Operations berjalan penuh | ✅ Selesai |
| UI States (Loading, Success, Error, Empty) | ✅ Selesai |
| Fitur Bonus: AI Inventory Assistant | ✅ Selesai |
| Fitur Bonus: History Screen | ✅ Selesai |

---

## 🎥 Video Demo:
Anda dapat melihat demo aplikasi pada link berikut: https://drive.google.com/file/d/1-H1Nh0JPQjPFbAODvzFAu8Zf7AJ7mHbc/view?usp=drive_link

*InventRa — Solusi Inventaris Digital untuk HMIF ITERA*
