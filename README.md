#  Pantau Jompo

![CI](https://github.com/EL-graha26/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

**Aplikasi Pemantauan Kesehatan Cerdas untuk Android**
---
## 📖 Tentang Proyek

**Pantau Jompo** adalah aplikasi kesehatan Android yang menggabungkan pelacakan aktivitas berbasis GPS, analisis nutrisi bertenaga AI, dan portal berita kesehatan dalam satu platform terpadu.

Aplikasi ini dirancang untuk memudahkan pengguna mengelola gaya hidup sehat secara efisien — mulai dari mencatat olahraga harian, memindai kandungan gizi makanan lewat kamera, hingga mendapatkan rekomendasi kalori personal dari AI assistant.

---

## 👥 Tim Pengembang

Proyek ini dikembangkan sebagai bagian dari tugas perkuliahan oleh:

| Nama | NIM | Peran |
|------|-----|-------|
| **Pradana Figo Ariansya** | 123140063 | Android Developer |
| **Muhammad Piela Nugraha** | 123140200 | Android Developer |

---

## 🚀 Fitur Utama

### 📍 Smart Activity Tracking
Pelacakan aktivitas fisik luar ruangan secara *real-time* menggunakan GPS.

- **Auto-Logging** — Rute, jarak tempuh, dan durasi tercatat otomatis (lari, jalan kaki, bersepeda)
- **Calorie Analytics** — Estimasi kalori terbakar dihitung berdasarkan jenis dan intensitas aktivitas
- **Persistent Storage** — Semua rekam jejak disimpan permanen via Room Database

### 📸 AI Nutrition Scanner
Gantikan pencatatan nutrisi manual dengan analisis gambar berbasis Vision AI.

- **Camera Recognition** — Foto makanan → AI identifikasi jenis dan kandungan gizi secara instan
- **Dual Input** — Mendukung input manual via teks atau analisis gambar dari kamera
- **Detail Nutrisi** — Kalori, karbohidrat, protein, lemak, dan serat ditampilkan langsung

### 📝 Fitness History — Full CRUD
Logbook terpadu untuk seluruh data kesehatan pengguna.

- **Create & Read** — Catat riwayat olahraga, asupan gizi, dan keluhan fisik
- **Update & Delete** — Edit atau hapus data kapan saja
- **Search & Filter** — Telusuri data berdasarkan rentang tanggal atau kategori

### 📰 Health News Portal + AI Summarizer
Pusat literasi kesehatan yang aktual dan ringkas.

- **News API Integration** — Berita kesehatan terkini dari sumber terpercaya secara *real-time*
- **AI Summarizer** — Artikel panjang diringkas menjadi poin-poin utama *(TL;DR)*

### 👤 Personal Dashboard & Metrics
Semua data kesehatan pengguna tersaji dalam satu halaman.

- **BMI Calculator** — Perbarui berat dan tinggi badan untuk skor *Body Mass Index* instan
- **Consistency Tracker** — Grafik kerutinan olahraga mingguan/bulanan
- **AI Recommendation** — Target kalori harian yang dipersonalisasi sesuai profil pengguna

### 🌙 Modern UI + Dark Mode
- Desain responsif optimal untuk berbagai ukuran layar Android
- Dukungan tema gelap penuh — efisiensi baterai AMOLED dan nyaman di mata

---

## 🛠️ Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **Language** | Kotlin |
| **UI** | XML Layouts, Material Design 3 |
| **Architecture** | MVVM + Repository Pattern |
| **Local Database** | Room (SQLite) |
| **Async** | Kotlin Coroutines + Flow |
| **HTTP Client** | Retrofit 2 + OkHttp |
| **Image Loading** | Coil |
| **AI / Vision** | Gemini API / ML Kit |
| **Maps & GPS** | Google Maps SDK, FusedLocationProvider |
| **News** | News API |
| **DI** | Hilt (Dagger) |

---

## 🏛️ Arsitektur

Aplikasi ini mengikuti pola **MVVM (Model-View-ViewModel)** yang direkomendasikan Google, dikombinasikan dengan **Repository Pattern** untuk abstraksi sumber data.

```
┌─────────────────────────────────────────────────┐
│                    UI Layer                      │
│         Activity / Fragment / Composable         │
└──────────────────────┬──────────────────────────┘
                       │ observes
┌──────────────────────▼──────────────────────────┐
│                 ViewModel Layer                  │
│        StateFlow / LiveData / UI State           │
└──────────────────────┬──────────────────────────┘
                       │ calls
┌──────────────────────▼──────────────────────────┐
│               Repository Layer                   │
│       Menentukan sumber data (local/remote)      │
└────────┬─────────────────────────────┬───────────┘
         │                             │
┌────────▼────────┐         ┌──────────▼──────────┐
│   Local Source  │         │    Remote Source     │
│  Room Database  │         │  Retrofit / API      │
│   (SQLite)      │         │  Gemini, News, Maps  │
└─────────────────┘         └─────────────────────┘
```

### Alur Data
1. **UI** mengobservasi `StateFlow`/`LiveData` dari **ViewModel**
2. **ViewModel** memanggil **Repository** untuk data atau aksi
3. **Repository** memilih antara **Room** (cache/offline) atau **Retrofit** (sumber remote)
4. Data dikembalikan sebagai Kotlin `Flow` dan di-*collect* oleh ViewModel

---

## 📁 Struktur Proyek

```
app/src/main/
├── java/com/example/pantaujompo/
│   ├── data/
│   │   ├── local/
│   │   │   ├── dao/            # Room DAO interfaces
│   │   │   ├── entity/         # Room Entity classes
│   │   │   └── AppDatabase.kt
│   │   ├── remote/
│   │   │   ├── api/            # Retrofit API interfaces
│   │   │   └── dto/            # Data Transfer Objects
│   │   └── repository/         # Repository implementations
│   ├── domain/
│   │   ├── model/              # Domain models (pure Kotlin)
│   │   └── usecase/            # Use case classes
│   ├── ui/
│   │   ├── tracking/           # Fitur GPS Tracking
│   │   ├── nutrition/          # Fitur AI Scanner
│   │   ├── history/            # Fitur CRUD Riwayat
│   │   ├── news/               # Portal Berita
│   │   ├── dashboard/          # Profil & Metrik
│   │   └── common/             # Shared UI components
│   ├── di/                     # Hilt Dependency Injection modules
│   └── utils/                  # Extension functions & helpers
└── res/
    ├── layout/                 # XML layout files
    ├── drawable/               # Icons & vector assets
    └── values/                 # Strings, colors, themes
```

---

## 🏁 Memulai

### Prasyarat
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK API 26+
- Gradle 8.x

### Instalasi

```bash
# 1. Clone repositori
git clone https://github.com/username/pantau-jompo.git

# 2. Buka di Android Studio
# File > Open > pilih folder pantau-jompo

# 3. Tambahkan API keys (lihat bagian Konfigurasi API)

# 4. Build dan jalankan
# Run > Run 'app' atau Shift+F10
```

---

## 🔑 Konfigurasi API

Buat file `local.properties` di root proyek dan tambahkan key berikut:

```properties
# local.properties — jangan di-commit ke Git!

GEMINI_API_KEY=your_gemini_api_key_here
NEWS_API_KEY=your_news_api_key_here
MAPS_API_KEY=your_google_maps_api_key_here
```

Daftarkan API key di:
- **Gemini API** → [Google AI Studio](https://aistudio.google.com)
- **News API** → [newsapi.org](https://newsapi.org)
- **Google Maps** → [Google Cloud Console](https://console.cloud.google.com)

> ⚠️ **Penting:** Pastikan `local.properties` sudah masuk ke `.gitignore` agar API key tidak ter-*expose* di repositori publik.

---

## 🎯 Status Proyek: SPRINT 2 (Core Features)

[cite_start]Saat ini proyek telah menyelesaikan tahapan **Sprint 2** dengan fokus pada *UI Implementation, Navigation, dan Data Layer* sesuai dengan target *deliverables*[cite: 12, 485]. 

**Fitur yang telah berjalan (100% Real Data):**
- [cite_start]✅ **UI Implementation (Jetpack Compose):** 5 Main Screens (Beranda, Pemindai, Riwayat, Artikel, Profil) dengan tema *Material 3 Dark Mode + Neon Green*[cite: 500].
- [cite_start]✅ **Navigation Setup:** *Bottom Navigation Bar* yang terhubung dengan `NavHost`, termasuk argument passing untuk layar form/edit[cite: 487].
- [cite_start]✅ **Data Layer & CRUD:** Operasi *Create, Read, Update, Delete* (CRUD) fungsional menggunakan **SQLDelight** dengan *Repository Pattern* untuk mencatat Riwayat Aktivitas[cite: 488, 492].
- ✅ **State Management & Persistence:** UI States (Loading, Success, Empty) tertangani dengan baik. [cite_start]Penyimpanan profil pengguna (Nama, Usia, Berat, Tinggi) & kalkulasi BMI *real-time* menggunakan **Jetpack DataStore**[cite: 489, 493].

*(Catatan: Fitur Vision AI, GPS Tracking, dan News API saat ini masih berupa mock/dummy UI dan akan diimplementasikan secara penuh pada Sprint 3 

## 🎥 Demo Sprint 2

Berikut adalah demonstrasi fitur inti (Navigasi & CRUD) dari Sprint 2:
Demo Aplikasi Pantau Jompo Sprint 2


https://github.com/user-attachments/assets/7df9fc3a-b7a5-49a0-a2ce-75d2969fba29


> *Klik gambar di atas untuk memutar video demo (Durasi: ~1 Menit).*

---
<div align="center">

Dibuat dengan ❤️ · Institut Teknologi Sumatera · 2025

</div>
