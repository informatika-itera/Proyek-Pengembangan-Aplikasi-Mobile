# 🥗 FoodSaver

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
- [ ] **Onboarding & Profil** — Setup awal aplikasi dan preferensi notifikasi (disimpan via DataStore).
- [ ] **Inventory Management** — CRUD (Create, Read, Update, Delete) data stok makanan lengkap dengan kategori.
- [ ] **Expiry Tracker** — Pemantauan tanggal kedaluwarsa secara real-time dengan status indikator visual.
- [ ] **Smart Notifications** — Pengingat otomatis sebelum bahan makanan melewati batas waktu konsumsi.
- [ ] **Navigasi Multi-Screen** — Minimal 5 layar: Dashboard, Inventory, Add Food, AI Assistant, dan Profile.
- [ ] **State Management** — MVVM + StateFlow untuk alur data yang reaktif dan stabil.
- [ ] **Koin DI** — Dependency Injection setup untuk modularitas kode.
- [ ] **Testing** — Implementasi unit tests untuk validasi logika bisnis utama.

### Bonus (Target)
- [ ] **AI Integration (+10%)** — Integrasi Gemini API untuk asisten resep cerdas berdasarkan sisa bahan di kulkas.
- [ ] **Waste Analytics (+5%)** — Visualisasi statistik (chart) makanan yang berhasil diselamatkan vs yang terbuang.
- [ ] **Dark Mode (+5%)** — Dukungan tema gelap/terang menggunakan Material 3.
- [ ] **Animations (+5%)** — Animasi transisi antar layar dan loading indicator yang interaktif.

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

### Struktur Folder

```
composeApp/src/commonMain/kotlin/com/example/foodsaver/
├── core/                        # Koin modules & Utilities
│   ├── di/
│   ├── network/
│   └── util/
├── data/
│   ├── local/                   # SQLDelight & DataStore
│   ├── remote/                  # API Services (Gemini) & DTOs
│   └── repository/              # Repository Implementations
├── domain/
│   ├── model/                   # FoodItem, UserProfile
│   ├── repository/              # Interfaces
│   └── usecase/                 # GetInventoryUseCase, SuggestRecipeUseCase
└── presentation/
    ├── navigation/              # NavHost & Routes
    ├── theme/                   # Material3 Colors & Typography
    ├── components/              # Shared UI (FoodCard, StatusChip)
    └── screens/                 # Home, Inventory, AI Assistant, etc.
```

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform (KMP), Compose Multiplatform |
| **Networking** | Ktor Client + Kotlinx Serialization |
| **Local DB** | SQLDelight (Inventory) |
| **Preferences** | DataStore (Theme, Onboarding flag) |
| **AI Engine** | Google Gemini API (gemini-1.5-flash) |
| **DI** | Koin |
| **Testing** | kotlin.test, Turbine |
| **CI/CD** | GitHub Actions |

---

## 🗂️ Sprint Plan

| Sprint | Minggu | Target | PIC |
|--------|--------|--------|-----|
| **Sprint 1** | W11 | Planning, Setup Repo, CI/CD, README | Bersama |
| **Sprint 2** | W12 | DB Setup, UI Dashboard, Navigasi Dasar | Bening |
| **Sprint 2** | W12 | Domain Layer, Use Cases Dasar, Setup Gemini | Raditya |
| **Sprint 3** | W13 | CRUD Logic, Notifikasi Reminder, Integrasi Ktor | Bening |
| **Sprint 3** | W13 | Integrasi Gemini AI & UI AI Assistant | Raditya |
| **Sprint 4** | W14 | Waste Tracker Analytics, Dark Mode, Animasi | Berdua |
| **Sprint 5** | W15 | Unit Tests, Bug Fixes, Dokumentasi Final | Berdua |

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
   Dapatkan API key di [AI Studio](https://aistudio.google.com/) dan tambahkan:
   ```properties
   GEMINI_API_KEY=your_key_here
   ```

3. **Build & Run**
   Buka di Android Studio, pilih target `composeApp` dan jalankan pada emulator atau perangkat fisik.

---

## 📡 API Reference

### Gemini API
- **Model:** `gemini-1.5-flash`
- **Tujuan:** Digunakan untuk generate rekomendasi resep masakan berdasarkan sisa bahan inventaris.

---

## 📄 Lisensi

MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.
