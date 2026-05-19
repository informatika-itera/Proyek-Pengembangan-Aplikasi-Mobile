# 🥗 FoodSaver
[![FoodSaver CI](https://github.com/rdtngh/123140089-123140125-FoodSaver/actions/workflows/build.yml/badge.svg?branch=project/123140089-123140125-FoodSaver)](https://github.com/rdtngh/123140089-123140125-FoodSaver/actions/workflows/build.yml?query=branch%3Aproject%2F123140089-123140125-FoodSaver)

Aplikasi mobile multiplatform (Android-first) yang dirancang untuk membantu pengguna **mengelola stok bahan makanan**, **memantau tanggal kedaluwarsa secara proaktif**, dan **mengurangi limbah makanan** melalui bantuan asisten cerdas berbasis AI.

---

## 👥 Tim

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| Bening Apni Prameswari | 123140089 | [@beningapniprameswari](https://github.com/beningapniprameswari) | Lead & UI/UX Developer |
| Raditya Alrasyid Nugroho | 123140125 | [@rdtngh](https://github.com/rdtngh) | Logic & Android Dev |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Dosen:** Pak Habib ([@mh4Scripts](https://github.com/mh4Scripts))  
---

## 📱 Deskripsi Aplikasi

**FoodSaver** membantu pengguna mengelola dapur mereka dengan lebih efisien guna mengurangi pemborosan makanan rumah tangga. Aplikasi ini bertindak sebagai asisten dapur pribadi yang:

1. **Digital Inventory**: Melacak stok bahan makanan beserta jumlah, kategori, dan masa simpan secara lokal dengan **SQLDelight**.
2. **Proactive Reminders**: Memberikan peringatan dini melalui notifikasi sebelum bahan makanan mencapai tanggal kedaluwarsa.
3. **AI Recipe Suggestion**: Mengintegrasikan **Gemini AI** untuk memberikan rekomendasi resep masakan kreatif berdasarkan bahan-bahan yang paling mendekati tanggal kedaluwarsa.

---

## ✨ Fitur

### Minimum (Wajib)
- [x] **Setup Project & CI/CD** — Inisialisasi KMP dan GitHub Actions.
- [x] **Architecture Setup** — Implementasi Clean Architecture & Koin DI.
- [ ] **Onboarding & Profil** — Setup awal aplikasi dan preferensi notifikasi (disimpan via DataStore).
- [ ] **Inventory Management** — CRUD (Create, Read, Update, Delete) data stok makanan lengkap dengan kategori.
- [ ] **Expiry Tracker** — Pemantauan tanggal kedaluwarsa secara real-time dengan status indikator visual.
- [ ] **Smart Notifications** — Pengingat otomatis sebelum bahan makanan melewati batas waktu konsumsi.
- [ ] **Navigasi Multi-Screen** — Minimal 5 layar: Dashboard, Inventory, Add Food, AI Assistant, dan Profile.
- [ ] **State Management** — MVVM + StateFlow untuk alur data yang reaktif dan stabil.

### Bonus (Target)
- [x] **Koin DI (+10%)** — Dependency Injection setup untuk modularitas kode.
- [ ] **AI Integration (+10%)** — Integrasi Gemini API untuk asisten resep cerdas berdasarkan sisa bahan di kulkas.
- [ ] **Waste Analytics (+5%)** — Visualisasi statistik (chart) makanan yang berhasil diselamatkan vs yang terbuang.

---

## 🏗️ Arsitektur

Menggunakan pola **Clean Architecture + MVVM** sesuai standar profesional.

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                  │
│   Screens (Compose) ◄──► ViewModel (StateFlow)   │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│               DOMAIN LAYER                       │
│   Use Cases ◄──► Repository Interfaces          │
│           (Pure Kotlin Business Logic)           │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                DATA LAYER                        │
│   Repository Implementation                     │
│   ├── Remote: Ktor + Gemini API (AI)            │
│   └── Local:  SQLDelight (Inventory Database)   │
└─────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform (KMP), Compose Multiplatform |
| **Networking** | Ktor Client + Kotlinx Serialization |
| **Local DB** | SQLDelight (Inventory) |
| **DI** | Koin |
| **CI/CD** | GitHub Actions |

---

## 🗂️ Project Plan

Detail rencana pengerjaan dapat dilihat pada file [PROJECT_PLAN.md](PROJECT_PLAN.md).

---

## 🚀 Setup & Cara Menjalankan

### Prerequisites
- Android Studio Ladybug (2024.2.1) atau lebih baru
- JDK 17+

### Langkah Setup

1. **Clone repository**
   ```bash
   git clone https://github.com/rdtngh/123140089-123140125-FoodSaver.git
   ```

2. **Setup `local.properties`**
   Tambahkan API key Gemini (untuk fitur AI di sprint mendatang):
   ```properties
   GEMINI_API_KEY=your_key_here
   ```

3. **Build & Run**
   Buka di Android Studio, pilih target `composeApp` dan jalankan.

---

## 📄 Lisensi

MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.
