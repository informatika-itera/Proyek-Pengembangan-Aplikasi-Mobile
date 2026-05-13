# 🌌 Synesthesia - AI-Powered Visual Journal
![CI](https://github.com/GarsRayy/NoteAI-Synesthesia/actions/workflows/ci.yml/badge.svg)

**Synesthesia** adalah aplikasi jurnal cerdas berbasis Kotlin Multiplatform yang menggabungkan ekspresi teks dengan harmoni visual Aurora Glass. Aplikasi ini dirancang untuk membantu mahasiswa dan pengguna umum dalam mencatat pikiran, emosi, dan ide dengan bantuan asisten AI yang imersif.

## 👥 Tim Pengembangan (Sprint 1)

| Foto                                                 | Informasi                                                                       | Role Utama |
|------------------------------------------------------|---------------------------------------------------------------------------------|------------|
| ![Profile](https://github.com/GarsRayy.png?size=100) | **Garis Rayya Rabbani** (123140018)<br>[@GarsRayy](https://github.com/GarsRayy) | UI/UX Design, Presentation Layer, Animations, Navigation |
| ![Profile](https://github.com/Ondor-R.png?size=100)  | **Reyhan Oktavian Putra** (123140202)<br>[@Ondor-R](https://github.com/Ondor-R) | Domain & Data Layer (AI, SQLDelight, Ktor) | 

---

## ✨ Fitur Aplikasi

### 🟢 Fitur Minimum (Core)
- [x] **Aurora Glass Interface**: UI modern dengan efek Glassmorphism dan background Aurora dinamis.
- [x] **Midnight Space Theme**: Tema gelap yang dioptimalkan untuk kontras dan kenyamanan visual.
- [ ] **Smart Notes CRUD**: Manajemen catatan dengan kategori, pinning, dan filter.
- [ ] **Offline-First Storage**: Penyimpanan lokal menggunakan SQLDelight yang andal.

### 🤖 Fitur AI & Bonus
- [x] **AI Assistant Screen**: Antarmuka chat imersif untuk berinteraksi dengan Gemini AI.
- [ ] **Sentiment Analysis**: Analisis emosi otomatis dari teks jurnal.
- [ ] **Smart Summarization**: Merangkum catatan panjang menjadi poin-poin penting.
- [x] **Dependency Injection**: Implementasi penuh menggunakan Koin DI (+10% Bonus).

---

## 🏗️ Arsitektur & Teknologi

### Clean Architecture + MVVM
Aplikasi ini mengikuti pola **Clean Architecture** untuk memastikan kode mudah diuji dan dipelihara:
- **Presentation Layer**: Compose Multiplatform, ViewModels (StateFlow).
- **Domain Layer**: Pure Kotlin, Use Cases, Repository Interfaces.
- **Data Layer**: Repository Implementation, SQLDelight (Local), Ktor (Remote), DataStore (Prefs).

### Tech Stack
- **UI**: Compose Multiplatform (1.7.0)
- **Dependency Injection**: Koin (4.0.0)
- **Networking**: Ktor Client (3.0.1)
- **Database**: SQLDelight (2.0.2)
- **AI Engine**: Google Gemini API
- **Concurrency**: Kotlin Coroutines & Flow

---

## 📁 Struktur Project
Project ini menggunakan struktur folder KMP standar dengan pembagian layer yang jelas di `commonMain`:
```
composeApp/src/commonMain/kotlin/com/example/synesthesia/
├── core/                      # DI modules, Network config, Utils
├── data/                      # Local (SQLDelight), Remote (Ktor), Repositories impl
├── domain/                    # Business Models, Repository interfaces, Use Cases
└── presentation/              # Screens, Navigation, Theme, Components
```

---

## 🚀 Getting Started

1. **Clone Repository**
   ```bash
   git clone https://github.com/GarsRayy/NoteAI-Synesthesia.git
   ```
2. **Setup API Key**
   Tambahkan `GEMINI_API_KEY=your_key` di file `local.properties`.
3. **Build & Run**
   Buka di Android Studio Ladybug+ dan jalankan task `:composeApp:installDebug`.

---
*Template ini diperbarui untuk memenuhi persyaratan Sprint 1 mata kuliah Pengembangan Aplikasi Mobile - ITERA.*
